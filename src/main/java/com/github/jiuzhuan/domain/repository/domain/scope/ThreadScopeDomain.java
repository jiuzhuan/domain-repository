package com.github.jiuzhuan.domain.repository.domain.scope;


import com.github.jiuzhuan.domain.repository.builder.builder.SFunction;
import com.github.jiuzhuan.domain.repository.domain.core.DomainTemplate;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 仓库作用域代理(线程)
 * 由于业务类中通过@Autowired注入DomainTemplate, 即使DomainSelect作用域为原型或请求级, 也只会在依赖注入阶段创建一次, 所以永远是单例的
 * 想要通过@Autowired注入 又能实现请求级 就要创建代理类 在每次调用时重新从Spring容器中getBean()创建一个目标类
 * 线程级作用域的领域 并发时 会互相影响, fixme: 增加restful请求级作用域 参考Spring request作用域实现 @Scope(WebApplicationContext.SCOPE_REQUEST)
 * JKD 19 增加虚拟线程(多个虚拟线程对应少量操作系统内核线程, 虚拟线程由JVM管理分配给内核线程, 是一种用户模式线程, 在GO语言中成功应用)
 * 虚拟线程允许开发者以每个请求一个线程的方式编写代码, 这符合现实世界逻辑, 易于理解易于开发和调试和堆栈跟踪, 也能充分利用硬件资源.
 * 使用方式和之前大致一样: Thread.newVirtualThread() 或 Executors.newVirtualThreadPerTaskExecutor()
 * @author arrety
 * @date 2022/4/19 14:31
 */
@Component
public class ThreadScopeDomain<DomEntity> implements DomainRepository<DomEntity> {

    // spring管理的bean 必须用srping的反射工具类解析泛型
    private Class<DomEntity> entityClass =
            (Class<DomEntity>) GenericTypeResolver.resolveTypeArgument(this.getClass(), ThreadScopeDomain.class);
//            (Class<DomEntity>) TypeUtils.getTypeArguments(this.getClass(), ThreadScopeDomain.class).values().iterator().next();

    @Autowired
    private ApplicationContext applicationContext;
    public ThreadLocal<DomainTemplate<DomEntity>> selectDomainThreadLocal = new ThreadLocal<>();

    @SafeVarargs
    public final <T> DomainTemplate<DomEntity> select(SFunction<T, ?>... columns) {
        DomainTemplate<DomEntity> selectDom = (DomainTemplate)applicationContext.getBean("domainTemplate");
        this.selectDomainThreadLocal.set(selectDom);
        selectDom.select(columns);
        return selectDom;
    }

    public DomainTemplate<DomEntity> selectAll() {
        DomainTemplate<DomEntity> selectDom = (DomainTemplate)applicationContext.getBean("domainTemplate");
        this.selectDomainThreadLocal.set(selectDom);
        selectDom.selectAll();
        return selectDom;
    }

    @SneakyThrows
    public void execute(Class<DomEntity> domClass) {
        selectDomainThreadLocal.get().execute(domClass);
    }

    public <T> List<T> getEntity(Class<T> entityClass) {
        return selectDomainThreadLocal.get().getEntity(entityClass);
    }

    @Override
    public <T> List<T> getDomains(Class<T> newDomClass) {
        return this.selectDomainThreadLocal.get().getDomains(newDomClass);
    }

    @Override
    public <T> List<T> getAutoDomains() {
        return this.selectDomainThreadLocal.get().getAutoDomains();
    }

    /**
     * 如果不执行该方法, 会累积领域类的数量无法辣鸡回收, 积累的数量和业务线程数量一致,
     * 但多次请求使用到同一线程并不会互相干扰污染, 因为每次都是new一个, 只是new的这个不等下次new是不会释放的
     */
    @Override
    public void clear() {
        selectDomainThreadLocal.remove();
    }

    public <T> void save(List<T> domains) {
        DomainTemplate<DomEntity> selectDom = (DomainTemplate)applicationContext.getBean("domainTemplate");
        this.selectDomainThreadLocal.set(selectDom);
        this.selectDomainThreadLocal.get().save(domains);
    }
}
