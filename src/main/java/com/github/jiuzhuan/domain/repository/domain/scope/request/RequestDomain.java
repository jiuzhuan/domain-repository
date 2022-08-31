package com.github.jiuzhuan.domain.repository.domain.scope.request;


import com.github.jiuzhuan.domain.repository.builder.builder.SFunction;
import com.github.jiuzhuan.domain.repository.domain.selecter.DomainSelect;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 线程级生命周期的领域
 * @author arrety
 * @date 2022/4/19 14:31 仓库
 */
@Component
public class RequestDomain<DomEntity> implements RequestRepository<DomEntity> {

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
    public <T> List<T> selectList(Class<T> domClass) {
        return selectDomainThreadLocal.get().selectList(domClass);
    }

    public <T> List<T> getEntity(Class<T> entityClass) {
        return selectDomainThreadLocal.get().getEntity(entityClass);
    }

    @Override
    public List<DomEntity> get() {
        return this.selectDomainThreadLocal.get().domList;
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
