package com.arrety.domainrepository.domainpersistence.domain.core;

import java.util.List;

/**
 * @author arrety
 * @date 2022/4/10 22:55
 */
public interface DomainRepository {

    <T> List<T> selectList(Class<T> domClass);

    <T> List<T> getEntity(Class<T> entityClass);
}
