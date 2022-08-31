package com.github.jiuzhuan.domain.repository.domain;

import java.util.List;

/**
 * @author arrety
 * @date 2022/4/10 22:55
 */
public interface RequestRepository<DomEntity> {

    List<DomEntity> get();

    void clear();
}