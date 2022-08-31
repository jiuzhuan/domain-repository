package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.example.domain.Order;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author pengfwang@trip.com
 * @date 2022/8/31 23:01
 */
@SpringBootTest
class OrderControllerTest {

    @Autowired
    OrderController orderController;

    @Test
    void getOrder() {
        List<Order> orders = orderController.getOrder(1);
        assertNotNull(orders);
    }
}