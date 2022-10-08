package com.github.jiuzhuan.domain.repository.example.domain;

import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import com.github.jiuzhuan.domain.repository.domain.scope.ThreadScopeDomain;
import com.github.jiuzhuan.domain.repository.example.domain.agg.Order;

/**
 * 订单聚合 (todo 可以不必定义此类, 直接依赖注入ThreadScopeDomain)
 */
@Dom
public class OrderDomain extends ThreadScopeDomain<Order> {

}
