package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.example.common.utils.OrderCompareUtil;
import com.github.jiuzhuan.domain.repository.example.domain.agg.Order;
import com.github.jiuzhuan.domain.repository.example.domain.agg.OrderGood;
import com.github.jiuzhuan.domain.repository.example.domain.agg.SlaveOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author pengfwang@trip.com
 * @date 2022/9/23 14:48
 */
@Transactional
@SpringBootTest
class OrderControllerTest {

    @Autowired
    OrderController orderController;

    @Autowired
    OrderController_old orderControllerOld;

    @Test
    void getOrders() {
        List<Order> orders = orderController.getOrders();
        OrderCompareUtil.equal_order(orderControllerOld.getOrders2(), orders);
    }

    @Test
    void getOrderGoods() {
        List<OrderGood> orderGoods = orderController.getOrderGoods();
        OrderCompareUtil.equals(orderControllerOld.getOrderGoods2(), orderGoods);
    }

    @Test
    void getOrderByOrderId() {
        List<Order> orders = orderController.getOrderByOrderId(1);
        OrderCompareUtil.equals(orderControllerOld.getOrderByOrderId2(1), orders);
    }

    @Test
    void getOrderBySlaveOrderInfoId() {
        List<Order> orders = orderController.getOrderBySlaveOrderInfoId(1);
        OrderCompareUtil.equals(orderControllerOld.getOrderBySlaveOrderInfoId2(1), orders);
    }

    @Test
    void getOrderByOrderGoodInfoId() {
        List<Order> orders = orderController.getOrderByOrderGoodInfoId(1);
        OrderCompareUtil.equals(orderControllerOld.getOrderByOrderGoodInfoId2(1), orders);
    }

    @Test
    void getOrderByOrderGoodDiscountInfoId() {
        List<Order> orders = orderController.getOrderByOrderGoodDiscountInfoId(1);
        OrderCompareUtil.equals(orderControllerOld.getOrderByOrderGoodDiscountInfoId2(1), orders);
    }

    @Test
    void getGoodByDiscountId() {
        List<SlaveOrder> slaveOrders = orderController.getGoodByDiscountId(1);
        OrderCompareUtil.equals(orderControllerOld.getGoodByDiscountId2(1), slaveOrders);
    }

    @Test
    void getMasterByGoodId() {
        List<Order> orders = orderController.getMasterByGoodId(1);
        OrderCompareUtil.equals(orderControllerOld.getMasterByGoodId2(1), orders);
    }

    @Test
    void getGoodByServiceId() {
        List<SlaveOrder> slaveOrders = orderController.getGoodByServiceId(1);
        OrderCompareUtil.equals(orderControllerOld.getGoodByServiceId2(1), slaveOrders);
    }

    @Test
    void updateOrderBySlaveId() {
        String userName = "小李-update-1";
        List<Order> orders = orderController.updateOrderById(3, userName);
        assert Objects.equals(orders.get(0).masterOrderInfo.userName, userName);
    }

    @Test
    void updateOrder() {
        List<Order> orders = orderController.updateOrder();
    }

    @Test
    void updateSlaveOrder() {
        List<SlaveOrder> slaveOrders = orderController.updateSlaveOrder();
    }

    @Test
    void addOrder() {
        List<Order> orders = orderController.addOrder();
    }
}