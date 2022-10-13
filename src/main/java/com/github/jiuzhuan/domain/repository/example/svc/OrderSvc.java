package com.github.jiuzhuan.domain.repository.example.svc;

import com.github.jiuzhuan.domain.repository.example.domain.OrderDomain;
import com.github.jiuzhuan.domain.repository.example.domain.agg.Order;
import com.github.jiuzhuan.domain.repository.example.domain.agg.OrderGood;
import com.github.jiuzhuan.domain.repository.example.domain.agg.OrderService;
import com.github.jiuzhuan.domain.repository.example.domain.agg.SlaveOrder;
import com.github.jiuzhuan.domain.repository.example.domain.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;



@Service
public class OrderSvc {

    public static final String ADD = "add";
    public static final String UPDATE = "update";

    @Autowired
    OrderDomain orderDomain;

    public void checkScope() {
        List<Order> orders = orderDomain.getAutoDomains();
        Optional.ofNullable(orders).orElseThrow().stream().findFirst().map(Order::getMasterOrderInfo).map(MasterOrderInfo::getUserName).orElseThrow();
    }

    public void equal_order(List<Order> exceptOrders, List<Order> actuallyOrders) {
        Assert.isTrue(exceptOrders.size() == actuallyOrders.size(), "size error");
        for (int i = 0; i < exceptOrders.size(); i++) {
            Order exceptOrder = exceptOrders.get(i);
            Order actuallyOrder = actuallyOrders.get(i);
            if (!togetherNullElseThrow(exceptOrder.masterOrderInfo, actuallyOrder.masterOrderInfo)) {
                Assert.isTrue(Objects.equals(exceptOrder.masterOrderInfo.userName, actuallyOrder.masterOrderInfo.userName), "not equal");
            }
            if (!togetherNullElseThrow(exceptOrder.orderAddressInfo, actuallyOrder.orderAddressInfo)) {
                Assert.isTrue(Objects.equals(exceptOrder.orderAddressInfo.address, actuallyOrder.orderAddressInfo.address), "not equal");
            }
            if (!togetherNullElseThrow(exceptOrder.slaveOrder, actuallyOrder.slaveOrder)) {
                equals_slaveOrder(exceptOrder.slaveOrder, actuallyOrder.slaveOrder);
            }
        }
    }

    public void equals_slaveOrder(List<SlaveOrder> excepts, List<SlaveOrder> actuallies) {
        Assert.isTrue(excepts.size() == actuallies.size(), "size error");
        for (int i = 0; i < excepts.size(); i++) {
            SlaveOrder except = excepts.get(i);
            SlaveOrder actually = actuallies.get(i);
            if (!togetherNullElseThrow(except.slaveOrderInfo, actually.slaveOrderInfo)) {
                Assert.isTrue(Objects.equals(except.slaveOrderInfo.storeName, actually.slaveOrderInfo.storeName), "not equal");
            }
            if (!togetherNullElseThrow(except.orderGoodDiscountInfo, actually.orderGoodDiscountInfo)) {
                Assert.isTrue(except.orderGoodDiscountInfo.discount.compareTo(actually.orderGoodDiscountInfo.discount) == 0, "not equal");
            }
            if (!togetherNullElseThrow(except.orderGood, actually.orderGood)) {
                equals_orderGood(except.orderGood, actually.orderGood);
            }
            if (!togetherNullElseThrow(except.orderService, actually.orderService)) {
                equals_orderService(except.orderService, actually.orderService);
            }
        }
    }

    public void equals_orderGood(List<OrderGood> excepts, List<OrderGood> actuallies) {
        Assert.isTrue(excepts.size() == actuallies.size(), "size error");
        for (int i = 0; i < excepts.size(); i++) {
            OrderGood except = excepts.get(i);
            OrderGood actually = actuallies.get(i);
            if (!togetherNullElseThrow(except.orderGoodInfo, actually.orderGoodInfo)) {
                Assert.isTrue(Objects.equals(except.orderGoodInfo.goodName, actually.orderGoodInfo.goodName), "not equal");
            }
            if (!togetherNullElseThrow(except.orderGoodRemarkInfo, actually.orderGoodRemarkInfo)) {
                Assert.isTrue(Objects.equals(except.orderGoodRemarkInfo.remark, actually.orderGoodRemarkInfo.remark), "not equal");
            }
        }
    }

    public void equals_orderService(List<OrderService> excepts, List<OrderService> actuallies) {
        Assert.isTrue(excepts.size() == actuallies.size(), "size error");
        for (int i = 0; i < excepts.size(); i++) {
            OrderService except = excepts.get(i);
            OrderService actually = actuallies.get(i);
            if (!togetherNullElseThrow(except.orderServiceInfo, actually.orderServiceInfo)) {
                Assert.isTrue(Objects.equals(except.orderServiceInfo.serviceName, actually.orderServiceInfo.serviceName), "not equal");
            }
            if (!togetherNullElseThrow(except.orderServicePriceInfo, actually.orderServicePriceInfo)) {
                Assert.isTrue(except.orderServicePriceInfo.price.compareTo(actually.orderServicePriceInfo.price) == 0, "not equal");
            }
        }
    }

    public boolean togetherNullElseThrow(Object... values) {
        boolean isNull = values[0] == null;
        for (int i = 1; i < values.length; i++) {
            Assert.isTrue(isNull == (values[i] == null), "not together null") ;
        }
        return isNull;
    }
    
    public List<Order> orders(){
        List<Order> orders = new ArrayList<>();
        orders.add(order());
        return orders;
    }

    public Order order(){
        Order order = new Order();
        order.masterOrderInfo = masterOrderInfo();
        order.orderAddressInfo = orderAddressInfo();
        order.slaveOrder = slaveOrders();
        return order;
    }

    public MasterOrderInfo masterOrderInfo(){
        MasterOrderInfo masterOrderInfo = new MasterOrderInfo();
        masterOrderInfo.userName = ADD;
        return masterOrderInfo;
    }
    
    public OrderAddressInfo orderAddressInfo(){
        OrderAddressInfo orderAddressInfo = new OrderAddressInfo();
        orderAddressInfo.address = ADD;
        return orderAddressInfo;
    }

    public List<SlaveOrder> slaveOrders() {
        List<SlaveOrder> slaveOrders = new ArrayList<>();
        slaveOrders.add(slaveOrder());
        return slaveOrders;
    }
    
    public SlaveOrder slaveOrder(){
        SlaveOrder slaveOrder = new SlaveOrder();
        slaveOrder.slaveOrderInfo = slaveOrderInfo();
        slaveOrder.orderGood = orderGoods();
        slaveOrder.orderService = orderServices();
        slaveOrder.orderGoodDiscountInfo = orderGoodDiscountInfo();
        return slaveOrder;
    }

    public SlaveOrderInfo slaveOrderInfo(){
        SlaveOrderInfo slaveOrderInfo = new SlaveOrderInfo();
        slaveOrderInfo.storeName = ADD;
        return slaveOrderInfo;
    }

    public List<OrderGood> orderGoods(){
        List<OrderGood> orderGoods = new ArrayList<>();
        orderGoods.add(orderGood());
        return orderGoods;
    }

    public OrderGood orderGood() {
        OrderGood orderGood = new OrderGood();
        orderGood.orderGoodInfo = orderGoodInfo();
        orderGood.orderGoodRemarkInfo = orderGoodRemarkInfo();
        return orderGood;
    }

    public OrderGoodInfo orderGoodInfo() {
        OrderGoodInfo orderGoodInfo = new OrderGoodInfo();
        orderGoodInfo.goodName = ADD;
        return orderGoodInfo;
    }

    public OrderGoodRemarkInfo orderGoodRemarkInfo() {
        OrderGoodRemarkInfo orderGoodRemarkInfo = new OrderGoodRemarkInfo();
        orderGoodRemarkInfo.remark = ADD;
        return orderGoodRemarkInfo;
    }

    public List<OrderService> orderServices(){
        List<OrderService> orderServices = new ArrayList<>();
        orderServices.add(orderService());
        return orderServices;
    }

    public OrderService orderService(){
        OrderService orderService = new OrderService();
        orderService.orderServiceInfo = orderServiceInfo();
        orderService.orderServicePriceInfo = orderServicePriceInfo();
        return orderService;
    }

    public OrderServiceInfo orderServiceInfo(){
        OrderServiceInfo orderServiceInfo = new OrderServiceInfo();
        orderServiceInfo.serviceName = ADD;
        return orderServiceInfo;
    }

    public OrderServicePriceInfo orderServicePriceInfo() {
        OrderServicePriceInfo orderServicePriceInfo = new OrderServicePriceInfo();
        orderServicePriceInfo.price = new BigDecimal("333");
        return orderServicePriceInfo;
    }

    public OrderGoodDiscountInfo orderGoodDiscountInfo(){
        OrderGoodDiscountInfo orderGoodDiscountInfo = new OrderGoodDiscountInfo();
        orderGoodDiscountInfo.discount = new BigDecimal("0.33");
        return orderGoodDiscountInfo;
    }
}
