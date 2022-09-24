package com.github.jiuzhuan.domain.repository.example.svc;

import com.github.jiuzhuan.domain.repository.example.domain.OrderDomain;
import com.github.jiuzhuan.domain.repository.example.domain.agg.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class OrderSvc {

    @Autowired
    OrderDomain orderDomain;

    public void checkOrders() {
        List<Order> orders = orderDomain.getAutoDomains();
        for (Order order : orders) {
             Assert.notNull(order.masterOrderInfo, "masterOrderInfo is null");
        }
    }
}
