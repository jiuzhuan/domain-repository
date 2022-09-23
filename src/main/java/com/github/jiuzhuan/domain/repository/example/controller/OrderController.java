package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.example.domain.agg.Order;
import com.github.jiuzhuan.domain.repository.example.domain.agg.OrderGood;
import com.github.jiuzhuan.domain.repository.example.domain.agg.SlaveOrder;
import com.github.jiuzhuan.domain.repository.example.domain.entity.*;
import com.github.jiuzhuan.domain.repository.example.domain.OrderDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        orderDomain.getAutoDomains();
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
        orderDomain.getEntity(OrderServiceInfo.class);
        orderDomain.getEntity(OrderServicePriceInfo.class);
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
        orderDomain.clear();
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
        orderDomain.clear();
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
        orderDomain.clear();
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
        // TODO: 2022.09.24 索引不对 
        orderDomain.selectAll().from(OrderServiceInfo.class).where().eq(OrderServiceInfo::getId, id);
        orderDomain.execute(Order.class);
        // // TODO: 2022.09.24  最小聚合不对
        orderDomain.getEntity(OrderGoodInfo.class);
        return orderDomain.getAutoDomains();
    }
}
