package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.example.domain.agg.Order;
import com.github.jiuzhuan.domain.repository.example.domain.agg.OrderGood;
import com.github.jiuzhuan.domain.repository.example.domain.agg.SlaveOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author pengfwang@trip.com
 * @date 2022/9/23 14:48
 */
@Transactional
@SpringBootTest
class OrderControllerTest {

    @Autowired
    OrderController orderController;

    @Test
    void getOrders() {
        List<Order> orders = orderController.getOrders();

        AssertDefaultData.assertOrder1(orders.get(0).masterOrderInfo);
        AssertDefaultData.assertAddress1(orders.get(0).orderAddressInfo);

        AssertDefaultData.assertSlave1(orders.get(0).slaveOrder.get(0).slaveOrderInfo);
        AssertDefaultData.assertService1(orders.get(0).slaveOrder.get(0).orderService.get(0).orderServiceInfo);
        AssertDefaultData.assertServicePrice1(orders.get(0).slaveOrder.get(0).orderService.get(0).orderServicePriceInfo);
        AssertDefaultData.assertGood1(orders.get(0).slaveOrder.get(0).orderGood.get(0).orderGoodInfo);
        AssertDefaultData.assertRemark1(orders.get(0).slaveOrder.get(0).orderGood.get(1).orderGoodRemarkInfo);
        AssertDefaultData.assertGood2(orders.get(0).slaveOrder.get(0).orderGood.get(1).orderGoodInfo);
        AssertDefaultData.assertDiscount1(orders.get(0).slaveOrder.get(0).orderGoodDiscountInfo);

        AssertDefaultData.assertSlave2(orders.get(0).slaveOrder.get(1).slaveOrderInfo);
        AssertDefaultData.assertGood3(orders.get(0).slaveOrder.get(1).orderGood.get(0).orderGoodInfo);
        AssertDefaultData.assertDiscount2(orders.get(0).slaveOrder.get(1).orderGoodDiscountInfo);


        AssertDefaultData.assertOrder2(orders.get(1).masterOrderInfo);
        assertNull(orders.get(1).orderAddressInfo);

        AssertDefaultData.assertSlave3(orders.get(1).slaveOrder.get(0).slaveOrderInfo);
        AssertDefaultData.assertGood4(orders.get(1).slaveOrder.get(0).orderGood.get(0).orderGoodInfo);
        AssertDefaultData.assertDiscount3(orders.get(1).slaveOrder.get(0).orderGoodDiscountInfo);
    }

    @Test
    void getOrderGoods() {
        List<OrderGood> orderGoods = orderController.getOrderGoods();

        assert orderGoods.size() == 5;
        AssertDefaultData.assertGood1(orderGoods.get(0).orderGoodInfo);
        AssertDefaultData.assertGood2(orderGoods.get(1).orderGoodInfo);
    }

    @Test
    void getOrderByOrderId() {
        List<Order> orders = orderController.getOrderByOrderId(1);

        AssertDefaultData.assertOrder1(orders.get(0).masterOrderInfo);

        AssertDefaultData.assertSlave1(orders.get(0).slaveOrder.get(0).slaveOrderInfo);
        AssertDefaultData.assertGood1(orders.get(0).slaveOrder.get(0).orderGood.get(0).orderGoodInfo);
        AssertDefaultData.assertGood2(orders.get(0).slaveOrder.get(0).orderGood.get(1).orderGoodInfo);
        AssertDefaultData.assertDiscount1(orders.get(0).slaveOrder.get(0).orderGoodDiscountInfo);

        AssertDefaultData.assertSlave2(orders.get(0).slaveOrder.get(1).slaveOrderInfo);
        AssertDefaultData.assertGood3(orders.get(0).slaveOrder.get(1).orderGood.get(0).orderGoodInfo);
        AssertDefaultData.assertDiscount2(orders.get(0).slaveOrder.get(1).orderGoodDiscountInfo);
    }

    @Test
    void getOrderBySlaveOrderInfoId() {
        List<Order> orders = orderController.getOrderBySlaveOrderInfoId(1);

        AssertDefaultData.assertOrder1(orders.get(0).masterOrderInfo);
        AssertDefaultData.assertSlave1(orders.get(0).slaveOrder.get(0).slaveOrderInfo);
        AssertDefaultData.assertGood1(orders.get(0).slaveOrder.get(0).orderGood.get(0).orderGoodInfo);
        AssertDefaultData.assertGood2(orders.get(0).slaveOrder.get(0).orderGood.get(1).orderGoodInfo);
        AssertDefaultData.assertDiscount1(orders.get(0).slaveOrder.get(0).orderGoodDiscountInfo);
    }

    @Test
    void getOrderByOrderGoodInfoId() {
        List<Order> orders = orderController.getOrderByOrderGoodInfoId(1);

        AssertDefaultData.assertOrder1(orders.get(0).masterOrderInfo);
        AssertDefaultData.assertSlave1(orders.get(0).slaveOrder.get(0).slaveOrderInfo);
        AssertDefaultData.assertGood1(orders.get(0).slaveOrder.get(0).orderGood.get(0).orderGoodInfo);
        assert (orders.get(0).slaveOrder.get(0).orderGood.size() == 1);
        AssertDefaultData.assertDiscount1(orders.get(0).slaveOrder.get(0).orderGoodDiscountInfo);
    }

    @Test
    void getOrderByOrderGoodDiscountInfoId() {
        List<Order> orders = orderController.getOrderByOrderGoodDiscountInfoId(1);

        AssertDefaultData.assertOrder1(orders.get(0).masterOrderInfo);
        AssertDefaultData.assertSlave1(orders.get(0).slaveOrder.get(0).slaveOrderInfo);
        AssertDefaultData.assertGood1(orders.get(0).slaveOrder.get(0).orderGood.get(0).orderGoodInfo);
        AssertDefaultData.assertGood2(orders.get(0).slaveOrder.get(0).orderGood.get(1).orderGoodInfo);
        AssertDefaultData.assertDiscount1(orders.get(0).slaveOrder.get(0).orderGoodDiscountInfo);
    }

    @Test
    void getGoodByDiscountId() {
        List<SlaveOrder> slaveOrders = orderController.getGoodByDiscountId(1);

        assert (slaveOrders.get(0).slaveOrderInfo == null);
        AssertDefaultData.assertGood1(slaveOrders.get(0).orderGood.get(0).orderGoodInfo);
        AssertDefaultData.assertGood2(slaveOrders.get(0).orderGood.get(1).orderGoodInfo);
        AssertDefaultData.assertDiscount1(slaveOrders.get(0).orderGoodDiscountInfo);
    }

    @Test
    void getMasterByGoodId() {
        List<Order> orders = orderController.getMasterByGoodId(1);

        AssertDefaultData.assertOrder1(orders.get(0).masterOrderInfo);
        AssertDefaultData.assertSlave1(orders.get(0).slaveOrder.get(0).slaveOrderInfo);
        AssertDefaultData.assertGood1(orders.get(0).slaveOrder.get(0).orderGood.get(0).orderGoodInfo);
        assert (orders.get(0).slaveOrder.get(0).orderGood.size() == 1);
    }

    @Test
    void getGoodByServiceId() {
        List<SlaveOrder> slaveOrders = orderController.getGoodByServiceId(1);

        AssertDefaultData.assertGood1(slaveOrders.get(0).orderGood.get(0).orderGoodInfo);
        AssertDefaultData.assertGood2(slaveOrders.get(0).orderGood.get(1).orderGoodInfo);
        AssertDefaultData.assertService1(slaveOrders.get(0).orderService.get(0).orderServiceInfo);
    }

    @Test
    void updateOrderBySlaveId() {
        String userName = "小李-update-1";
        List<Order> orders = orderController.updateOrderById(3, userName);

        assert Objects.equals(orders.get(0).masterOrderInfo.userName, userName);
    }

    @Test
    void updateOrderDomain() {
        List<Order> orders = orderController.updateOrderDomain(3);
    }

    @Test
    void addOrderDomain() {
        List<Order> orders = orderController.addOrderDomain();
    }
}