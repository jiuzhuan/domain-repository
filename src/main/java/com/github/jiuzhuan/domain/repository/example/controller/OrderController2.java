package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.builder.builder.LambdaSelectBuilder;
import com.github.jiuzhuan.domain.repository.example.domain.agg.Order;
import com.github.jiuzhuan.domain.repository.example.domain.agg.OrderGood;
import com.github.jiuzhuan.domain.repository.example.domain.agg.OrderService;
import com.github.jiuzhuan.domain.repository.example.domain.agg.SlaveOrder;
import com.github.jiuzhuan.domain.repository.example.domain.entity.*;
import com.google.common.collect.Lists;
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

        List<MasterOrderInfo> masterOrderInfos = getMasterOrderInfos();
        List<Integer> masterOrderInfoIds = masterOrderInfos.stream().map(MasterOrderInfo::getId).collect(Collectors.toList());
        List<OrderAddressInfo> orderAddressInfos = getOrderAddressInfos(masterOrderInfoIds);

        List<SlaveOrderInfo> slaveOrderInfos = getSlaveOrderInfos(masterOrderInfoIds);
        List<Integer> slaveOrderIds = slaveOrderInfos.stream().map(SlaveOrderInfo::getId).collect(Collectors.toList());

        List<OrderGoodInfo> orderGoodInfos = getOrderGoodInfos(slaveOrderIds);
        List<Integer> orderGoodInfoIds = orderGoodInfos.stream().map(OrderGoodInfo::getId).collect(Collectors.toList());
        List<OrderGoodRemarkInfo> orderGoodRemarkInfos = getOrderGoodRemarkInfos(orderGoodInfoIds);

        List<OrderServiceInfo> orderServiceInfos = getOrderServiceInfos(slaveOrderIds);
        List<Integer> orderServiceInfoIds = orderServiceInfos.stream().map(OrderServiceInfo::getId).collect(Collectors.toList());
        List<OrderServicePriceInfo> orderServicePriceInfos = getOrderServicePriceInfos(orderServiceInfoIds);

        List<OrderGoodDiscountInfo> orderGoodDiscounts = getOrderGoodDiscountInfos(slaveOrderIds);

        return buildOrders(masterOrderInfos, orderAddressInfos, slaveOrderInfos, orderGoodInfos, orderGoodRemarkInfos, orderServiceInfos, orderServicePriceInfos, orderGoodDiscounts);
    }

    @GetMapping("getOrderGoods2")
    public List<OrderGood> getOrderGoods2(){
        List<OrderGoodInfo> orderGoodInfos = getOrderGoodInfos();
        List<Integer> orderGoodInfoIds = orderGoodInfos.stream().map(OrderGoodInfo::getId).collect(Collectors.toList());
        List<OrderGoodRemarkInfo> orderGoodRemarkInfos = getOrderGoodRemarkInfos(orderGoodInfoIds);
        return buildOrderGoods(orderGoodInfos, orderGoodRemarkInfos);
    }

    @GetMapping("getOrderByOrderId2")
    public List<Order> getOrderByOrderId2(Integer id){

        List<MasterOrderInfo> masterOrderInfos = getMasterOrderInfos(Lists.newArrayList(id));
        List<Integer> masterOrderInfoIds = masterOrderInfos.stream().map(MasterOrderInfo::getId).collect(Collectors.toList());

        List<SlaveOrderInfo> slaveOrderInfos = getSlaveOrderInfos(masterOrderInfoIds);
        List<Integer> slaveOrderIds = slaveOrderInfos.stream().map(SlaveOrderInfo::getId).collect(Collectors.toList());

        List<OrderGoodInfo> orderGoodInfos = getOrderGoodInfos(slaveOrderIds);

        List<OrderGoodDiscountInfo> orderGoodDiscounts = getOrderGoodDiscountInfos(slaveOrderIds);

        return buildOrders(masterOrderInfos, new ArrayList<>(), slaveOrderInfos, orderGoodInfos, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), orderGoodDiscounts);
    }

    @GetMapping("getOrderBySlaveOrderInfoId2")
    public List<Order> getOrderBySlaveOrderInfoId2(Integer id) {
        return getOrderByOrderId2(id);
    }

    private List<Order> buildOrders(List<MasterOrderInfo> masterOrderInfos, List<OrderAddressInfo> orderAddressInfos, List<SlaveOrderInfo> slaveOrderInfos,
                                    List<OrderGoodInfo> orderGoodInfos, List<OrderGoodRemarkInfo> orderGoodRemarkInfos, List<OrderServiceInfo> orderServiceInfos,
                                    List<OrderServicePriceInfo> orderServicePriceInfos, List<OrderGoodDiscountInfo> orderGoodDiscount) {
        List<SlaveOrder> slaveOrders = buildSlaveOrders(slaveOrderInfos, orderGoodInfos, orderGoodRemarkInfos, orderServiceInfos, orderServicePriceInfos, orderGoodDiscount);
        Map<Integer, List<SlaveOrder>> slaveMap = slaveOrders.stream().collect(Collectors.groupingBy(item -> item.slaveOrderInfo.masterOrderInfoId));
        Map<Integer, OrderAddressInfo> addressMap = orderAddressInfos.stream().collect(Collectors.toMap(OrderAddressInfo::getMasterOrderInfoId, item -> item));
        List<Order> orders = new ArrayList<>();
        for (MasterOrderInfo masterOrderInfo : masterOrderInfos) {
            Order order = new Order();
            order.masterOrderInfo = masterOrderInfo;
            order.orderAddressInfo = addressMap.get(masterOrderInfo.getId());
            order.slaveOrder = slaveMap.get(masterOrderInfo.getId());
            orders.add(order);
        }
        return orders;
    }

    private List<SlaveOrder> buildSlaveOrders(List<SlaveOrderInfo> slaveOrderInfos, List<OrderGoodInfo> orderGoodInfos, List<OrderGoodRemarkInfo> orderGoodRemarkInfos,
                                              List<OrderServiceInfo> orderServiceInfos, List<OrderServicePriceInfo> orderServicePriceInfos, List<OrderGoodDiscountInfo> orderGoodDiscount) {
        List<OrderGood> orderGoods = buildOrderGoods(orderGoodInfos, orderGoodRemarkInfos);
        Map<Integer, List<OrderGood>> goodMap = orderGoods.stream().collect(Collectors.groupingBy(item -> item.orderGoodInfo.slaveOrderInfoId));
        List<OrderService> orderServices = buildOrderServices(orderServiceInfos, orderServicePriceInfos);
        Map<Integer, List<OrderService>> serviceMap = orderServices.stream().collect(Collectors.groupingBy(item -> item.orderServiceInfo.slaveOrderInfoId));
        List<OrderGoodDiscountInfo> orderGoodDiscountInfos = getOrderGoodDiscountInfos(slaveOrderInfos.stream().map(SlaveOrderInfo::getId).collect(Collectors.toList()));
        Map<Integer, OrderGoodDiscountInfo> discountMap = orderGoodDiscountInfos.stream().collect(Collectors.toMap(OrderGoodDiscountInfo::getSlaveOrderInfoId, item -> item));
        List<SlaveOrder> result = new ArrayList<>();
        for (SlaveOrderInfo item : slaveOrderInfos) {
            SlaveOrder resultItem = new SlaveOrder();
            resultItem.slaveOrderInfo = item;
            resultItem.orderGood = goodMap.get(item.id);
            resultItem.orderService = serviceMap.get(item.id);
            resultItem.orderGoodDiscountInfo = discountMap.get(item.id);
            result.add(resultItem);
        }
        return result;
    }

    private List<OrderGood> buildOrderGoods(List<OrderGoodInfo> orderGoodInfos, List<OrderGoodRemarkInfo> orderGoodRemarkInfos) {
        Map<Integer, OrderGoodRemarkInfo> map = orderGoodRemarkInfos.stream().collect(Collectors.toMap(OrderGoodRemarkInfo::getOrderGoodInfoId, item -> item));
        List<OrderGood> result = new ArrayList<>();
        for (OrderGoodInfo item : orderGoodInfos) {
            OrderGood resultItem = new OrderGood();
            resultItem.orderGoodInfo = item;
            resultItem.orderGoodRemarkInfo = map.get(item.id);
            result.add(resultItem);
        }
        return result;
    }

    private List<OrderService> buildOrderServices(List<OrderServiceInfo> orderServiceInfos, List<OrderServicePriceInfo> orderServicePriceInfos) {
        Map<Integer, OrderServicePriceInfo> map = orderServicePriceInfos.stream().collect(Collectors.toMap(OrderServicePriceInfo::getOrderServiceInfoId, item -> item));
        List<OrderService> result = new ArrayList<>();
        for (OrderServiceInfo item : orderServiceInfos) {
            OrderService resultItem = new OrderService();
            resultItem.orderServiceInfo = item;
            resultItem.orderServicePriceInfo = map.get(item.id);
            result.add(resultItem);
        }
        return result;
    }

    private List<MasterOrderInfo> getMasterOrderInfos(){
        return lambdaSelectBuilder.selectAll().from(MasterOrderInfo.class)
                .where().selectList(MasterOrderInfo.class);
    }

    private List<MasterOrderInfo> getMasterOrderInfos(List<Integer> masterOrderInfoIds){
        return lambdaSelectBuilder.selectAll().from(MasterOrderInfo.class)
                .where().in(MasterOrderInfo::getId, masterOrderInfoIds).selectList(MasterOrderInfo.class);
    }

    private List<OrderAddressInfo> getOrderAddressInfos(List<Integer> masterOrderInfoIds){
        return lambdaSelectBuilder.selectAll().from(OrderAddressInfo.class)
                .where().in(OrderAddressInfo::getMasterOrderInfoId, masterOrderInfoIds).selectList(OrderAddressInfo.class);
    }

    private List<SlaveOrderInfo> getSlaveOrderInfos(List<Integer> masterOrderInfoIds){
        return lambdaSelectBuilder.selectAll().from(SlaveOrderInfo.class)
                .where().in(SlaveOrderInfo::getMasterOrderInfoId, masterOrderInfoIds).selectList(SlaveOrderInfo.class);
    }

    private List<OrderGoodInfo> getOrderGoodInfos(){
        return lambdaSelectBuilder.selectAll().from(OrderGoodInfo.class)
                .where().selectList(OrderGoodInfo.class);
    }

    private List<OrderGoodInfo> getOrderGoodInfos(List<Integer> slaveOrderIds){
        return lambdaSelectBuilder.selectAll().from(OrderGoodInfo.class)
                .where().in(OrderGoodInfo::getSlaveOrderInfoId, slaveOrderIds).selectList(OrderGoodInfo.class);
    }

    private List<OrderGoodRemarkInfo> getOrderGoodRemarkInfos(List<Integer> OrderGoodInfoIds){
        return lambdaSelectBuilder.selectAll().from(OrderGoodRemarkInfo.class)
                .where().in(OrderGoodRemarkInfo::getOrderGoodInfoId, OrderGoodInfoIds).selectList(OrderGoodRemarkInfo.class);
    }

    private List<OrderServiceInfo> getOrderServiceInfos(List<Integer> slaveOrderIds){
        return lambdaSelectBuilder.selectAll().from(OrderServiceInfo.class)
                .where().in(OrderServiceInfo::getSlaveOrderInfoId, slaveOrderIds).selectList(OrderServiceInfo.class);
    }

    private List<OrderServicePriceInfo> getOrderServicePriceInfos(List<Integer> orderServiceInfoIds){
        return lambdaSelectBuilder.selectAll().from(OrderServicePriceInfo.class)
                .where().in(OrderServicePriceInfo::getOrderServiceInfoId, orderServiceInfoIds).selectList(OrderServicePriceInfo.class);
    }

    private List<OrderGoodDiscountInfo> getOrderGoodDiscountInfos(List<Integer> slaveOrderIds){
        return lambdaSelectBuilder.selectAll().from(OrderGoodDiscountInfo.class)
                .where().in(OrderGoodDiscountInfo::getSlaveOrderInfoId, slaveOrderIds).selectList(OrderGoodDiscountInfo.class);
    }
}
