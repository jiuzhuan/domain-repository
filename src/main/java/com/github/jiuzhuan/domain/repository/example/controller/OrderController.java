package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.builder.builder.LambdaSelectBuilder;
import com.github.jiuzhuan.domain.repository.example.domain.Order;
import com.github.jiuzhuan.domain.repository.example.domain.SlaveOrder;
import com.github.jiuzhuan.domain.repository.example.domain.entity.*;
import com.github.jiuzhuan.domain.repository.example.domain.OrderDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author arrety
 * @date 2022/1/29 11:12
 */
@RestController
public class OrderController {

    @Autowired
    OrderDomain orderDomain;

    @GetMapping("getOrders")
    public List<Order> getOrders(){
        // 顺序1
        orderDomain.selectAll().from(MasterOrderInfo.class).where();
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(OrderAddressInfo.class);
        orderDomain.get();
        orderDomain.getEntity(OrderGoodRemarkInfo.class);
        // 顺序2
        orderDomain.clear();
        orderDomain.selectAll().from(OrderGoodInfo.class).where();
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        orderDomain.getEntity(OrderAddressInfo.class);
        orderDomain.getEntity(OrderGoodRemarkInfo.class);
        return orderDomain.get();
    }

    @GetMapping("getOrderByOrderId")
    public List<Order> getOrderByOrderId(@RequestParam("id") Integer id){
        // 顺序1
        orderDomain.selectAll().from(MasterOrderInfo.class).where().eq(MasterOrderInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.get();
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        // 顺序2
        orderDomain.clear();
        orderDomain.selectAll().from(MasterOrderInfo.class).where().eq(MasterOrderInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.get();
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        return orderDomain.get();
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
        orderDomain.clear();
        orderDomain.selectAll().from(SlaveOrderInfo.class).where().eq(SlaveOrderInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        return orderDomain.get();
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
        orderDomain.clear();
        orderDomain.selectAll().from(OrderGoodInfo.class).where().eq(OrderGoodInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        // 顺序3
        orderDomain.clear();
        orderDomain.selectAll().from(OrderGoodInfo.class).where().eq(OrderGoodInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        return orderDomain.get();
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
        orderDomain.clear();
        orderDomain.selectAll().from(OrderGoodDiscountInfo.class).where().eq(OrderGoodDiscountInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        // 顺序3
        orderDomain.clear();
        orderDomain.selectAll().from(OrderGoodDiscountInfo.class).where().eq(OrderGoodDiscountInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        return orderDomain.get();
    }

    @GetMapping("getGoodByDiscountId")
    public List<SlaveOrder> getGoodByDiscountId(@RequestParam("id") Integer id){
        orderDomain.selectAll().from(OrderGoodDiscountInfo.class).where().eq(OrderGoodDiscountInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        return orderDomain.get();
    }

    @GetMapping("getMasterByGoodId")
    public List<Order> getMasterByGoodId(@RequestParam("id") Integer id){
        orderDomain.selectAll().from(OrderGoodInfo.class).where().eq(OrderGoodInfo::getId, id);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        return orderDomain.get();
    }
}
