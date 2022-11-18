package com.github.jiuzhuan.domain.repository.example.domain;

import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import com.github.jiuzhuan.domain.repository.domain.scope.ThreadScopeDomain;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.Order;

/**
 * 订单聚合
 * 1. todo 可以不必定义此类, 直接依赖注入ThreadScopeDomain
 * 2. todo 如果不希望有作用于管理, 且希望有泛型支持, 可以创建继承DomainTemplate的匿名类: new DomainTemplate<xxx>{};
 */
@Dom
public class OrderDomain extends ThreadScopeDomain<Order> {

}
