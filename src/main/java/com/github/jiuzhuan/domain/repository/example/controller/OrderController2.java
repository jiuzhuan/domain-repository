package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.builder.builder.LambdaSelectBuilder;
import com.github.jiuzhuan.domain.repository.example.domain.Order;
import com.github.jiuzhuan.domain.repository.example.domain.SlaveOrder;
import com.github.jiuzhuan.domain.repository.example.domain.entity.MasterOrderInfo;
import com.github.jiuzhuan.domain.repository.example.domain.entity.OrderGoodDiscountInfo;
import com.github.jiuzhuan.domain.repository.example.domain.entity.OrderGoodInfo;
import com.github.jiuzhuan.domain.repository.example.domain.entity.SlaveOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author arrety
 * @date 2022/1/29 11:12
 */
@RestController
public class OrderController2 {

    @Autowired
    LambdaSelectBuilder lambdaSelectBuilder;

    @GetMapping("getOrders2")
    public List<Order> getOrders2(){

        List<MasterOrderInfo> masterOrderInfos = lambdaSelectBuilder.selectAll().from(MasterOrderInfo.class).where().selectList(MasterOrderInfo.class);
        List<Integer> masterOrderInfoIds = masterOrderInfos.stream().map(MasterOrderInfo::getId).collect(Collectors.toList());

        List<SlaveOrderInfo> slaveOrderInfos = lambdaSelectBuilder.selectAll().from(SlaveOrderInfo.class).where().in(SlaveOrderInfo::getMasterOrderInfoId, masterOrderInfoIds).selectList(SlaveOrderInfo.class);
        List<Integer> slaveOrderIds = slaveOrderInfos.stream().map(SlaveOrderInfo::getId).collect(Collectors.toList());
        Map<Integer, List<SlaveOrderInfo>> slaveOrderMap = slaveOrderInfos.stream().collect(Collectors.groupingBy(SlaveOrderInfo::getMasterOrderInfoId));

        List<OrderGoodInfo> orderGoodInfos = lambdaSelectBuilder.selectAll().from(OrderGoodInfo.class).where().in(OrderGoodInfo::getSlaveOrderInfoId, slaveOrderIds).selectList(OrderGoodInfo.class);
        Map<Integer, List<OrderGoodInfo>> orderGoodMap = orderGoodInfos.stream().collect(Collectors.groupingBy(OrderGoodInfo::getSlaveOrderInfoId));

        List<OrderGoodDiscountInfo> orderGoodDiscountInfos = lambdaSelectBuilder.selectAll().from(OrderGoodDiscountInfo.class).where().in(OrderGoodDiscountInfo::getSlaveOrderInfoId, slaveOrderIds).selectList(OrderGoodDiscountInfo.class);
        Map<Integer, OrderGoodDiscountInfo> orderGoodDiscountMap = orderGoodDiscountInfos.stream().collect(Collectors.toMap(OrderGoodDiscountInfo::getSlaveOrderInfoId, orderGoodDiscountInfo -> orderGoodDiscountInfo));

        List<Order> orders = new ArrayList<>();
        for (MasterOrderInfo masterOrderInfo : masterOrderInfos) {
            List<SlaveOrder> slaveOrders = new ArrayList<>();
            for (SlaveOrderInfo slaveOrderInfo : slaveOrderMap.get(masterOrderInfo.getId())) {
                SlaveOrder slaveOrder = new SlaveOrder();
                slaveOrder.setSlaveOrderInfo(slaveOrderInfo);
                slaveOrder.setOrderGoodInfo(orderGoodMap.get(slaveOrderInfo.getId()));
                slaveOrder.setOrderGoodDiscountInfo(orderGoodDiscountMap.get(slaveOrderInfo.getId()));
                slaveOrders.add(slaveOrder);
            }
            Order order = new Order();
            order.setMasterOrderInfo(masterOrderInfo);
            order.setSlaveOrder(slaveOrders);
            orders.add(order);
        }
        return orders;
    }
}
