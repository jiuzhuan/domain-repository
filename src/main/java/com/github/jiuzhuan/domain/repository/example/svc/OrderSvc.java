package com.github.jiuzhuan.domain.repository.example.svc;

import com.github.jiuzhuan.domain.repository.example.domain.OrderDomain;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.Order;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.OrderGood;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.OrderService;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.SlaveOrder;
import com.github.jiuzhuan.domain.repository.example.domain.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
