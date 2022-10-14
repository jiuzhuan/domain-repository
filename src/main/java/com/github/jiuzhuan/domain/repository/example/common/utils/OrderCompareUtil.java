package com.github.jiuzhuan.domain.repository.example.common.utils;

import com.github.jiuzhuan.domain.repository.domain.utils.ReflectionUtil;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.Order;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.OrderGood;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.OrderService;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.SlaveOrder;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

public class OrderCompareUtil {

    public static <T> void equals(List<T> exceptOrders, List<T> actuallyOrders) {
        if (ReflectionUtil.getGenericType(exceptOrders).equals(Order.class)) equal_order((List<Order>) exceptOrders, (List<Order>) actuallyOrders);
        if (ReflectionUtil.getGenericType(exceptOrders).equals(SlaveOrder.class)) equals_slaveOrder((List<SlaveOrder>) exceptOrders, (List<SlaveOrder>) actuallyOrders);
        if (ReflectionUtil.getGenericType(exceptOrders).equals(OrderGood.class)) equals_orderGood((List<OrderGood>) exceptOrders, (List<OrderGood>) actuallyOrders);
        if (ReflectionUtil.getGenericType(exceptOrders).equals(OrderService.class)) equals_orderService((List<OrderService>) exceptOrders, (List<OrderService>) actuallyOrders);
    }

    public static void equal_order(List<Order> exceptOrders, List<Order> actuallyOrders) {
        Assert.isTrue(exceptOrders.size() == actuallyOrders.size(), "size error");
        for (int i = 0; i < exceptOrders.size(); i++) {
            Order exceptOrder = exceptOrders.get(i);
            Order actuallyOrder = actuallyOrders.get(i);
            if (allNotNullElseThrow(exceptOrder.masterOrderInfo, actuallyOrder.masterOrderInfo)) {
                Assert.isTrue(Objects.equals(exceptOrder.masterOrderInfo.userName, actuallyOrder.masterOrderInfo.userName), "not equal");
            }
            if (allNotNullElseThrow(exceptOrder.orderAddressInfo, actuallyOrder.orderAddressInfo)) {
                Assert.isTrue(Objects.equals(exceptOrder.orderAddressInfo.address, actuallyOrder.orderAddressInfo.address), "not equal");
            }
            if (allNotNullElseThrow(exceptOrder.slaveOrder, actuallyOrder.slaveOrder)) {
                equals_slaveOrder(exceptOrder.slaveOrder, actuallyOrder.slaveOrder);
            }
        }
    }

    public static void equals_slaveOrder(List<SlaveOrder> excepts, List<SlaveOrder> actuallies) {
        Assert.isTrue(excepts.size() == actuallies.size(), "size error");
        for (int i = 0; i < excepts.size(); i++) {
            SlaveOrder except = excepts.get(i);
            SlaveOrder actually = actuallies.get(i);
            if (allNotNullElseThrow(except.slaveOrderInfo, actually.slaveOrderInfo)) {
                Assert.isTrue(Objects.equals(except.slaveOrderInfo.storeName, actually.slaveOrderInfo.storeName), "not equal");
            }
            if (allNotNullElseThrow(except.orderGoodDiscountInfo, actually.orderGoodDiscountInfo)) {
                Assert.isTrue(except.orderGoodDiscountInfo.discount.compareTo(actually.orderGoodDiscountInfo.discount) == 0, "not equal");
            }
            if (allNotNullElseThrow(except.orderGood, actually.orderGood)) {
                equals_orderGood(except.orderGood, actually.orderGood);
            }
            if (allNotNullElseThrow(except.orderService, actually.orderService)) {
                equals_orderService(except.orderService, actually.orderService);
            }
        }
    }

    public static void equals_orderGood(List<OrderGood> excepts, List<OrderGood> actuallies) {
        Assert.isTrue(excepts.size() == actuallies.size(), "size error");
        for (int i = 0; i < excepts.size(); i++) {
            OrderGood except = excepts.get(i);
            OrderGood actually = actuallies.get(i);
            if (allNotNullElseThrow(except.orderGoodInfo, actually.orderGoodInfo)) {
                Assert.isTrue(Objects.equals(except.orderGoodInfo.goodName, actually.orderGoodInfo.goodName), "not equal");
            }
            if (allNotNullElseThrow(except.orderGoodRemarkInfo, actually.orderGoodRemarkInfo)) {
                Assert.isTrue(Objects.equals(except.orderGoodRemarkInfo.remark, actually.orderGoodRemarkInfo.remark), "not equal");
            }
        }
    }

    public static void equals_orderService(List<OrderService> excepts, List<OrderService> actuallies) {
        Assert.isTrue(excepts.size() == actuallies.size(), "size error");
        for (int i = 0; i < excepts.size(); i++) {
            OrderService except = excepts.get(i);
            OrderService actually = actuallies.get(i);
            if (allNotNullElseThrow(except.orderServiceInfo, actually.orderServiceInfo)) {
                Assert.isTrue(Objects.equals(except.orderServiceInfo.serviceName, actually.orderServiceInfo.serviceName), "not equal");
            }
            if (allNotNullElseThrow(except.orderServicePriceInfo, actually.orderServicePriceInfo)) {
                Assert.isTrue(except.orderServicePriceInfo.price.compareTo(actually.orderServicePriceInfo.price) == 0, "not equal");
            }
        }
    }

    public static boolean allNotNullElseThrow(Object... values) {
        boolean isNull = values[0] != null;
        for (int i = 1; i < values.length; i++) {
            Assert.isTrue(isNull == (values[i] != null), "not together null") ;
        }
        return isNull;
    }
}
