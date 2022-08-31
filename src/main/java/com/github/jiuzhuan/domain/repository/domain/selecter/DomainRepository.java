package com.github.jiuzhuan.domain.repository.domain.selecter;

import java.util.List;

/**
 * @author arrety
 * @date 2022/4/10 22:55
 */
public interface DomainRepository {

    <T> List<T> selectList(Class<T> domClass);

    <T> List<T> getEntity(Class<T> entityClass);
}
