package com.github.jiuzhuan.domain.repository.domain.scope;


import com.github.jiuzhuan.domain.repository.builder.builder.SFunction;
import com.github.jiuzhuan.domain.repository.domain.selecter.DomainSelect;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 仓库作用域代理(线程)
 * 由于业务类中通过@Autowired注入DomainSelect, 即使DomainSelect作用域为原型或请求级, 也只会在依赖注入阶段创建一次, 所以永远是单例的
 * 想要通过@Autowired注入 又能实现请求级 就要创建代理类 在每次调用时重新从Spring容器中getBean()创建一个目标类
 * 线程级作用域的领域 并发时 会互相影响, fixme: 增加restful请求级作用域 参考Spring request作用域实现 @Scope(WebApplicationContext.SCOPE_REQUEST)
 * JKD 19 增加虚拟线程(多个虚拟线程对应少量操作系统内核线程, 虚拟线程由JVM管理分配给内核线程), 易于开发和调试, 提高性能.
 * 使用方式和之前大致一样: Thread.newVirtualThread() 或 Executors.newVirtualThreadPerTaskExecutor()
 * @author arrety
 * @date 2022/4/19 14:31
 */
@Component
public class ThreadScopeDomain<DomEntity> implements DomainRepository<DomEntity> {

    @Autowired
    private ApplicationContext applicationContext;
    public ThreadLocal<DomainSelect<DomEntity>> selectDomainThreadLocal = new ThreadLocal<>();

    @SafeVarargs
    public final <T> DomainSelect<DomEntity> select(SFunction<T, ?>... columns) {
        DomainSelect<DomEntity> selectDom = (DomainSelect)applicationContext.getBean("domainSelect");
        this.selectDomainThreadLocal.set(selectDom);
        selectDom.select(columns);
        return selectDom;
    }

    public DomainSelect<DomEntity> selectAll() {
        DomainSelect<DomEntity> selectDom = (DomainSelect)applicationContext.getBean("domainSelect");
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

}
