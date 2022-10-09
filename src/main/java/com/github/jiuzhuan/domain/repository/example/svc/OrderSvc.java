package com.github.jiuzhuan.domain.repository.example.svc;

import com.github.jiuzhuan.domain.repository.example.domain.OrderDomain;
import com.github.jiuzhuan.domain.repository.example.domain.agg.Order;
import com.github.jiuzhuan.domain.repository.example.domain.entity.MasterOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrderSvc {

    @Autowired
    OrderDomain orderDomain;

    public void checkOrders() {
        List<Order> orders = orderDomain.getAutoDomains();
        Optional.ofNullable(orders).orElseThrow().stream().findFirst().map(Order::getMasterOrderInfo).map(MasterOrderInfo::getUserName).orElseThrow();
    }
}
