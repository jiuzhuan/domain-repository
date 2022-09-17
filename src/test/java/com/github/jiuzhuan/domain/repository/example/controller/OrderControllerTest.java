package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.example.domain.Order;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author pengfwang@trip.com
 * @date 2022/8/31 23:01
 */
@SpringBootTest
class OrderControllerTest {

    @Autowired
    OrderController orderController;

    @Test
    @DisplayName("Should return the order when the ordergoodinfoid is not null")
    void getOrder2WhenOrderGoodInfoIdIsNotNullThenReturnOrder() {
        List<Order> orders = orderController.getOrderByOrderGoodInfoId(1);
        Assert.notEmpty(orders, "The order is empty");
        Assert.isTrue(CollectionUtils.isNotEmpty(orders), "The order is empty");
        assertNotNull(orders);
        assertTrue(CollectionUtils.isNotEmpty(orders));
    }

    @Test
    @DisplayName("Should return the orders when the ordergoodinfoid is null")
    void getOrder2WhenOrderGoodInfoIdIsNullThenReturnOrders() {
        List<Order> orders = orderController.getOrderBySlaveOrderInfoId(null);
        Assert.isTrue(CollectionUtils.isNotEmpty(orders), "The orders should not be empty");
    }

    @Test
    void getOrder() {
        List<Order> orders = orderController.getOrder(1);
        assertNotNull(orders);
    }
}