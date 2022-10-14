package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.builder.builder.LambdaSelectBuilder;
import com.github.jiuzhuan.domain.repository.example.common.utils.CollectionUtil;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.Order;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.OrderGood;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.OrderService;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.SlaveOrder;
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
public class OrderController_old {

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

        List<SlaveOrderInfo> slaveOrderInfos = getSlaveOrderInfosByIds(Lists.newArrayList(id));
        List<Integer> slaveOrderIds = slaveOrderInfos.stream().map(SlaveOrderInfo::getId).collect(Collectors.toList());
        List<Integer> masterOrderIds = slaveOrderInfos.stream().map(SlaveOrderInfo::getMasterOrderInfoId).collect(Collectors.toList());

        List<MasterOrderInfo> masterOrderInfos = getMasterOrderInfos(masterOrderIds);

        List<OrderGoodInfo> orderGoodInfos = getOrderGoodInfos(slaveOrderIds);

        List<OrderGoodDiscountInfo> orderGoodDiscounts = getOrderGoodDiscountInfos(slaveOrderIds);

        return buildOrders(masterOrderInfos, new ArrayList<>(), slaveOrderInfos, orderGoodInfos, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), orderGoodDiscounts);
    }

    @GetMapping("getOrderByOrderGoodInfoId2")
    public List<Order> getOrderByOrderGoodInfoId2(Integer id) {

        List<OrderGoodInfo> orderGoodInfos = getOrderGoodInfosByIds(Lists.newArrayList(id));
        List<Integer> slaveOrderIds = orderGoodInfos.stream().map(OrderGoodInfo::getSlaveOrderInfoId).collect(Collectors.toList());

        List<SlaveOrderInfo> slaveOrderInfos = getSlaveOrderInfosByIds(slaveOrderIds);
        List<Integer> masterOrderIds = slaveOrderInfos.stream().map(SlaveOrderInfo::getMasterOrderInfoId).collect(Collectors.toList());

        List<MasterOrderInfo> masterOrderInfos = getMasterOrderInfos(masterOrderIds);

        List<OrderGoodDiscountInfo> orderGoodDiscounts = getOrderGoodDiscountInfos(slaveOrderIds);

        return buildOrders(masterOrderInfos, new ArrayList<>(), slaveOrderInfos, orderGoodInfos, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), orderGoodDiscounts);
    }

    @GetMapping("getOrderByOrderGoodDiscountInfoId2")
    public List<Order> getOrderByOrderGoodDiscountInfoId2(Integer id) {

        List<OrderGoodDiscountInfo> orderGoodDiscounts = getOrderGoodDiscountInfosByIds(Lists.newArrayList(id));
        List<Integer> slaveOrderIds = orderGoodDiscounts.stream().map(OrderGoodDiscountInfo::getSlaveOrderInfoId).collect(Collectors.toList());

        List<OrderGoodInfo> orderGoodInfos = getOrderGoodInfos(slaveOrderIds);

        List<SlaveOrderInfo> slaveOrderInfos = getSlaveOrderInfosByIds(slaveOrderIds);
        List<Integer> masterOrderIds = slaveOrderInfos.stream().map(SlaveOrderInfo::getMasterOrderInfoId).collect(Collectors.toList());

        List<MasterOrderInfo> masterOrderInfos = getMasterOrderInfos(masterOrderIds);

        return buildOrders(masterOrderInfos, new ArrayList<>(), slaveOrderInfos, orderGoodInfos, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), orderGoodDiscounts);
    }

    @GetMapping("getGoodByDiscountId2")
    public List<SlaveOrder> getGoodByDiscountId2(Integer id) {

        List<OrderGoodDiscountInfo> orderGoodDiscounts = getOrderGoodDiscountInfosByIds(Lists.newArrayList(id));
        List<Integer> slaveOrderIds = orderGoodDiscounts.stream().map(OrderGoodDiscountInfo::getSlaveOrderInfoId).collect(Collectors.toList());

        List<OrderGoodInfo> orderGoodInfos = getOrderGoodInfos(slaveOrderIds);

        return buildSlaveOrders(new ArrayList<>(), orderGoodInfos, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), orderGoodDiscounts);
    }

    @GetMapping("getMasterByGoodId2")
    public List<Order> getMasterByGoodId2(Integer id) {

        List<OrderGoodInfo> orderGoodInfos = getOrderGoodInfosByIds(Lists.newArrayList(id));
        List<Integer> slaveOrderIds = orderGoodInfos.stream().map(OrderGoodInfo::getSlaveOrderInfoId).collect(Collectors.toList());

        List<SlaveOrderInfo> slaveOrderInfos = getSlaveOrderInfosByIds(slaveOrderIds);
        List<Integer> masterOrderIds = slaveOrderInfos.stream().map(SlaveOrderInfo::getMasterOrderInfoId).collect(Collectors.toList());

        List<MasterOrderInfo> masterOrderInfos = getMasterOrderInfos(masterOrderIds);
        return buildOrders(masterOrderInfos, new ArrayList<>(), slaveOrderInfos, orderGoodInfos, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @GetMapping("getGoodByServiceId2")
    public List<SlaveOrder> getGoodByServiceId2(Integer id) {
        List<OrderServiceInfo> orderServiceInfos = getOrderServiceInfos(Lists.newArrayList(id));
        List<Integer> slaveOrderIds = orderServiceInfos.stream().map(OrderServiceInfo::getSlaveOrderInfoId).collect(Collectors.toList());
        List<OrderGoodInfo> orderGoodInfos = getOrderGoodInfos(slaveOrderIds);

        return buildSlaveOrders(new ArrayList<>(), orderGoodInfos, new ArrayList<>(), orderServiceInfos, new ArrayList<>(), new ArrayList<>());
    }

    private List<Order> buildOrders(List<MasterOrderInfo> masterOrderInfos, List<OrderAddressInfo> orderAddressInfos, List<SlaveOrderInfo> slaveOrderInfos,
                                    List<OrderGoodInfo> orderGoodInfos, List<OrderGoodRemarkInfo> orderGoodRemarkInfos, List<OrderServiceInfo> orderServiceInfos,
                                    List<OrderServicePriceInfo> orderServicePriceInfos, List<OrderGoodDiscountInfo> orderGoodDiscount) {
        Map<Integer, MasterOrderInfo> masterMap = masterOrderInfos.stream().filter(item -> item.id != null)
                .collect(Collectors.toMap(item -> item.id, item -> item));
        List<SlaveOrder> slaveOrders = buildSlaveOrders(slaveOrderInfos, orderGoodInfos, orderGoodRemarkInfos, orderServiceInfos, orderServicePriceInfos, orderGoodDiscount);
        Map<Integer, List<SlaveOrder>> slaveMap = slaveOrders.stream().filter(item -> item.slaveOrderInfo.masterOrderInfoId != null)
                .collect(Collectors.groupingBy(item -> item.slaveOrderInfo.masterOrderInfoId));
        Map<Integer, OrderAddressInfo> addressMap = orderAddressInfos.stream().filter(item -> item.masterOrderInfoId != null)
                .collect(Collectors.toMap(OrderAddressInfo::getMasterOrderInfoId, item -> item));
        List<Order> orders = new ArrayList<>();
        for (Integer join : CollectionUtil.union(masterMap.keySet(), slaveMap.keySet(), addressMap.keySet())) {
            Order order = new Order();
            order.masterOrderInfo = masterMap.get(join);
            order.orderAddressInfo = addressMap.get(join);
            order.slaveOrder = slaveMap.get(join);
            orders.add(order);
        }
        return orders;
    }

    private List<SlaveOrder> buildSlaveOrders(List<SlaveOrderInfo> slaveOrderInfos, List<OrderGoodInfo> orderGoodInfos, List<OrderGoodRemarkInfo> orderGoodRemarkInfos,
                                              List<OrderServiceInfo> orderServiceInfos, List<OrderServicePriceInfo> orderServicePriceInfos, List<OrderGoodDiscountInfo> orderGoodDiscountInfos) {
        Map<Integer, SlaveOrderInfo> slaveMap = slaveOrderInfos.stream().filter(item -> item.id != null)
                .collect(Collectors.toMap(SlaveOrderInfo::getId, item -> item));
        List<OrderGood> orderGoods = buildOrderGoods(orderGoodInfos, orderGoodRemarkInfos);
        Map<Integer, List<OrderGood>> goodMap = orderGoods.stream().filter(item -> item.orderGoodInfo.slaveOrderInfoId != null)
                .collect(Collectors.groupingBy(item -> item.orderGoodInfo.slaveOrderInfoId));
        List<OrderService> orderServices = buildOrderServices(orderServiceInfos, orderServicePriceInfos);
        Map<Integer, List<OrderService>> serviceMap = orderServices.stream().filter(item -> item.orderServiceInfo.slaveOrderInfoId != null)
                .collect(Collectors.groupingBy(item -> item.orderServiceInfo.slaveOrderInfoId));
        Map<Integer, OrderGoodDiscountInfo> discountMap = orderGoodDiscountInfos.stream().collect(Collectors.toMap(OrderGoodDiscountInfo::getSlaveOrderInfoId, item -> item));
        List<SlaveOrder> result = new ArrayList<>();
        for (Integer join : CollectionUtil.union(slaveMap.keySet(), goodMap.keySet(), serviceMap.keySet(), discountMap.keySet())) {
            SlaveOrder resultItem = new SlaveOrder();
            resultItem.slaveOrderInfo = slaveMap.get(join);
            resultItem.orderGood = goodMap.get(join);
            resultItem.orderService = serviceMap.get(join);
            resultItem.orderGoodDiscountInfo = discountMap.get(join);
            result.add(resultItem);
        }
        return result;
    }

    private List<OrderGood> buildOrderGoods(List<OrderGoodInfo> orderGoodInfos, List<OrderGoodRemarkInfo> orderGoodRemarkInfos) {
        Map<Integer, OrderGoodInfo> goodMap = orderGoodInfos.stream().filter(item -> item.id != null)
                .collect(Collectors.toMap(OrderGoodInfo::getId, item -> item));
        Map<Integer, OrderGoodRemarkInfo> remarkMap = orderGoodRemarkInfos.stream().filter(item -> item.orderGoodInfoId != null)
                .collect(Collectors.toMap(OrderGoodRemarkInfo::getOrderGoodInfoId, item -> item));
        List<OrderGood> result = new ArrayList<>();
        for (Integer join : CollectionUtil.union(goodMap.keySet(), remarkMap.keySet())) {
            OrderGood resultItem = new OrderGood();
            resultItem.orderGoodInfo = goodMap.get(join);
            resultItem.orderGoodRemarkInfo = remarkMap.get(join);
            result.add(resultItem);
        }
        return result;
    }

    private List<OrderService> buildOrderServices(List<OrderServiceInfo> orderServiceInfos, List<OrderServicePriceInfo> orderServicePriceInfos) {
        Map<Integer, OrderServiceInfo> infoMap = orderServiceInfos.stream().filter(item -> item.id != null)
                .collect(Collectors.toMap(OrderServiceInfo::getId, item -> item));
        Map<Integer, OrderServicePriceInfo> priceMap = orderServicePriceInfos.stream().filter(item -> item.orderServiceInfoId != null)
                .collect(Collectors.toMap(OrderServicePriceInfo::getOrderServiceInfoId, item -> item));
        List<OrderService> result = new ArrayList<>();
        for (Integer join : CollectionUtil.union(infoMap.keySet(), priceMap.keySet())) {
            OrderService resultItem = new OrderService();
            resultItem.orderServiceInfo = infoMap.get(join);
            resultItem.orderServicePriceInfo = priceMap.get(join);
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

    private List<SlaveOrderInfo> getSlaveOrderInfosByIds(List<Integer> ids){
        return lambdaSelectBuilder.selectAll().from(SlaveOrderInfo.class)
                .where().in(SlaveOrderInfo::getId, ids).selectList(SlaveOrderInfo.class);
    }

    private List<OrderGoodInfo> getOrderGoodInfos(){
        return lambdaSelectBuilder.selectAll().from(OrderGoodInfo.class)
                .where().selectList(OrderGoodInfo.class);
    }

    private List<OrderGoodInfo> getOrderGoodInfos(List<Integer> slaveOrderIds){
        return lambdaSelectBuilder.selectAll().from(OrderGoodInfo.class)
                .where().in(OrderGoodInfo::getSlaveOrderInfoId, slaveOrderIds).selectList(OrderGoodInfo.class);
    }

    private List<OrderGoodInfo> getOrderGoodInfosByIds(ArrayList<Integer> ids) {
        return lambdaSelectBuilder.selectAll().from(OrderGoodInfo.class)
                .where().in(OrderGoodInfo::getId, ids).selectList(OrderGoodInfo.class);
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

    private List<OrderGoodDiscountInfo> getOrderGoodDiscountInfosByIds(ArrayList<Integer> ids) {
        return lambdaSelectBuilder.selectAll().from(OrderGoodDiscountInfo.class)
                .where().in(OrderGoodDiscountInfo::getId, ids).selectList(OrderGoodDiscountInfo.class);
    }
}
