package com.github.jiuzhuan.domain.repository.domain.core;

import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;

/**
 * @author arrety
 * @date 2022/4/10 22:55
 */
public interface DomainRepository {

    @SneakyThrows
    <T> List<T> getDomains(Class<T> newDomClass);

    @SneakyThrows
    <T> List<T> getAutoDomains();

    <T> List<T> getEntity(Class<T> entityClass);

    <T> void save(List<T> domians);
}
