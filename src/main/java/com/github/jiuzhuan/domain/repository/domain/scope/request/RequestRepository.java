package com.github.jiuzhuan.domain.repository.domain.scope.request;

import java.util.List;

/**
 * @author arrety
 * @date 2022/4/10 22:55
 */
public interface RequestRepository<DomEntity> {

    /**
     * 将结果赋值到指定聚合类集合
     * @param newDomClass
     * @return
     * @param <T>
     */
    <T> List<T> getDomains(Class<T> newDomClass);

    /**
     * 将结果赋值到最小聚合类集合
     * @return
     * @param <T>
     */
    <T> List<T> getAutoDomains();

    void clear();
}
