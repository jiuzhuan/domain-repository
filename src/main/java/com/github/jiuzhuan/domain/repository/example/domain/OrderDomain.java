package com.github.jiuzhuan.domain.repository.example.domain;

import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import com.github.jiuzhuan.domain.repository.domain.scope.request.RequestDomain;
import com.github.jiuzhuan.domain.repository.example.domain.agg.Order;

/**
 * 订单聚合
 */
@Dom
public class OrderDomain extends RequestDomain<Order> {

}
