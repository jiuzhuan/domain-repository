package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.builder.builder.LambdaBuilder;
import com.github.jiuzhuan.domain.repository.builder.builder.LambdaUpdateBuilder;
import com.github.jiuzhuan.domain.repository.example.domain.agg.Order;
import com.github.jiuzhuan.domain.repository.example.domain.agg.OrderGood;
import com.github.jiuzhuan.domain.repository.example.domain.agg.OrderService;
import com.github.jiuzhuan.domain.repository.example.domain.agg.SlaveOrder;
import com.github.jiuzhuan.domain.repository.example.domain.entity.*;
import com.github.jiuzhuan.domain.repository.example.domain.OrderDomain;
import com.github.jiuzhuan.domain.repository.example.svc.OrderSvc;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author arrety
 * @date 2022/1/29 11:12
 */
@RestController
public class OrderController {

    @Autowired
    OrderSvc orderSvc;

    @Autowired
    OrderDomain orderDomain;

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("getOrders")
    public List<Order> getOrders(){
        // 顺序1
        orderDomain.selectAll().from(MasterOrderInfo.class).where();
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(OrderAddressInfo.class);
        orderDomain.getAutoDomains();
        orderDomain.getEntity(OrderGoodRemarkInfo.class);
        // 顺序2
        orderDomain.selectAll().from(OrderGoodInfo.class).where();
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        orderDomain.getEntity(OrderAddressInfo.class);
        orderDomain.getEntity(OrderGoodRemarkInfo.class);
        orderDomain.getEntity(OrderServiceInfo.class);
        orderDomain.getEntity(OrderServicePriceInfo.class);
        orderSvc.checkOrders();
        return orderDomain.getAutoDomains();
    }

    @GetMapping("getOrderGoods")
    public List<OrderGood> getOrderGoods(){
        // 顺序1
        orderDomain.selectAll().from(MasterOrderInfo.class).where();
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(OrderAddressInfo.class);
        orderDomain.getAutoDomains();
        orderDomain.getEntity(OrderGoodRemarkInfo.class);
        // 顺序2
        orderDomain.selectAll().from(OrderGoodInfo.class).where();
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        orderDomain.getEntity(OrderAddressInfo.class);
        orderDomain.getEntity(OrderGoodRemarkInfo.class);
        return orderDomain.getDomains(OrderGood.class);
    }

    @GetMapping("getOrderByOrderId")
    public List<Order> getOrderByOrderId(@RequestParam("id") Integer id){
        // 顺序1
        orderDomain.selectAll().from(MasterOrderInfo.class).where().eq(MasterOrderInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getAutoDomains();
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        // 顺序2
        orderDomain.selectAll().from(MasterOrderInfo.class).where().eq(MasterOrderInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getAutoDomains();
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        return orderDomain.getAutoDomains();
    }

    @GetMapping("getOrderBySlaveOrderInfoId")
    public List<Order> getOrderBySlaveOrderInfoId(@RequestParam("id") Integer id){
        // 顺序1
        orderDomain.selectAll().from(SlaveOrderInfo.class).where().eq(SlaveOrderInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        // 顺序2
        orderDomain.selectAll().from(SlaveOrderInfo.class).where().eq(SlaveOrderInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        return orderDomain.getAutoDomains();
    }

    @GetMapping("getOrderByOrderGoodInfoId")
    public List<Order> getOrderByOrderGoodInfoId(@RequestParam("id") Integer id){
        // 顺序1
        orderDomain.selectAll().from(OrderGoodInfo.class).where().eq(OrderGoodInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        // 顺序2
        orderDomain.selectAll().from(OrderGoodInfo.class).where().eq(OrderGoodInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        // 顺序3
        orderDomain.selectAll().from(OrderGoodInfo.class).where().eq(OrderGoodInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        return orderDomain.getAutoDomains();
    }

    @GetMapping("getOrderByOrderGoodDiscountInfoId")
    public List<Order> getOrderByOrderGoodDiscountInfoId(@RequestParam("id") Integer id){
        // 顺序1
        orderDomain.selectAll().from(OrderGoodDiscountInfo.class).where().eq(OrderGoodDiscountInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        // 顺序2
        orderDomain.selectAll().from(OrderGoodDiscountInfo.class).where().eq(OrderGoodDiscountInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        // 顺序3
        orderDomain.selectAll().from(OrderGoodDiscountInfo.class).where().eq(OrderGoodDiscountInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        return orderDomain.getAutoDomains();
    }

    @GetMapping("getGoodByDiscountId")
    public List<SlaveOrder> getGoodByDiscountId(@RequestParam("id") Integer id){
        orderDomain.selectAll().from(OrderGoodDiscountInfo.class).where().eq(OrderGoodDiscountInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        return orderDomain.getAutoDomains();
    }

    @GetMapping("getMasterByGoodId")
    public List<Order> getMasterByGoodId(@RequestParam("id") Integer id){
        orderDomain.selectAll().from(OrderGoodInfo.class).where().eq(OrderGoodInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        return orderDomain.getAutoDomains();
    }

    @GetMapping("getGoodByServiceId")
    public List<SlaveOrder> getGoodByServiceId(@RequestParam("id") Integer id){
        orderDomain.selectAll().from(OrderServiceInfo.class).where().eq(OrderServiceInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        return orderDomain.getAutoDomains();
    }

    @GetMapping("updateOrderById")
    public List<Order> updateOrderById(@RequestParam("id") Integer id, @RequestParam("userName") String userName){
        // h2不支持 update join
        LambdaBuilder.update(MasterOrderInfo.class)
                .set(MasterOrderInfo::getUserName, userName)
                .where().eq(MasterOrderInfo::getId, id).update();
        orderDomain.selectAll().from(MasterOrderInfo.class).where().eq(MasterOrderInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        return orderDomain.getAutoDomains();
    }

    @GetMapping("updateOrderDomain")
    public List<Order> updateOrderDomain(@RequestParam("id") Integer id){
        orderDomain.selectAll().from(MasterOrderInfo.class).where().eq(MasterOrderInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        List<Order> orders = orderDomain.getDomains(Order.class);
        orders.get(0).masterOrderInfo.userName = "updateOrderDomain11";
        orders.get(0).slaveOrder.get(0).slaveOrderInfo.storeName = "updateOrderDomain11";
        SlaveOrder slaveOrder = new SlaveOrder();
        SlaveOrderInfo slaveOrderInfo = new SlaveOrderInfo();
        slaveOrderInfo.storeName = "add";
        slaveOrder.slaveOrderInfo = slaveOrderInfo;
        OrderGoodDiscountInfo orderGoodDiscountInfo = new OrderGoodDiscountInfo();
        orderGoodDiscountInfo.discount = new BigDecimal("0.11");
        slaveOrder.orderGoodDiscountInfo = orderGoodDiscountInfo;
        orders.get(0).slaveOrder.add(slaveOrder);
        OrderAddressInfo orderAddressInfo = new OrderAddressInfo();
        orderAddressInfo.address = "add";
        orders.get(0).orderAddressInfo = orderAddressInfo;
        List<OrderService> orderServices = new ArrayList<>();
        OrderService orderService = new OrderService();
        OrderServiceInfo orderServiceInfo = new OrderServiceInfo();
        orderServiceInfo.serviceName = "add-service";
        orderService.orderServiceInfo = orderServiceInfo;
        orderServices.add(orderService);
        orders.get(0).slaveOrder.get(0).orderService = orderServices;
        orders.get(0).slaveOrder.get(0).orderGood.get(0).orderGoodInfo.goodName = "good-update";
        orderDomain.save(orders);

        orderDomain.selectAll().from(MasterOrderInfo.class).where().eq(MasterOrderInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderAddressInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderServiceInfo.class);
        return orderDomain.getAutoDomains();
    }
}
