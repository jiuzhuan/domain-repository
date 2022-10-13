package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.builder.builder.LambdaBuilder;
import com.github.jiuzhuan.domain.repository.example.common.transaction.Rollback;
import com.github.jiuzhuan.domain.repository.example.domain.agg.Order;
import com.github.jiuzhuan.domain.repository.example.domain.agg.OrderGood;
import com.github.jiuzhuan.domain.repository.example.domain.agg.SlaveOrder;
import com.github.jiuzhuan.domain.repository.example.domain.entity.*;
import com.github.jiuzhuan.domain.repository.example.domain.OrderDomain;
import com.github.jiuzhuan.domain.repository.example.svc.OrderSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author arrety
 * @date 2022/1/29 11:12
 */
@Rollback
@RestController
public class OrderController {

    @Autowired
    OrderSvc orderSvc;

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
        orderDomain.selectAll().from(OrderGoodInfo.class).where();
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(MasterOrderInfo.class);
        orderDomain.getEntity(OrderAddressInfo.class);
        orderDomain.getEntity(OrderGoodRemarkInfo.class);
        orderDomain.getEntity(OrderServiceInfo.class);
        orderDomain.getEntity(OrderServicePriceInfo.class);
        orderSvc.checkScope();
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

    @GetMapping("updateOrder")
    public List<Order> updateOrder(){
        Integer masterOrderInfoId = 3;

        orderDomain.selectAll().from(MasterOrderInfo.class).where().eq(MasterOrderInfo::getId, masterOrderInfoId);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(OrderAddressInfo.class);
        orderDomain.getEntity(OrderGoodRemarkInfo.class);
        orderDomain.getEntity(OrderServiceInfo.class);
        orderDomain.getEntity(OrderServicePriceInfo.class);
        List<Order> orders = orderDomain.getDomains(Order.class);

        // 更新
        orders.get(0).masterOrderInfo.userName = OrderSvc.UPDATE;
        orders.get(0).slaveOrder.get(0).slaveOrderInfo.storeName = OrderSvc.UPDATE;
        orders.get(0).slaveOrder.get(0).orderGood.get(0).orderGoodInfo.goodName = OrderSvc.UPDATE;
        orders.get(0).slaveOrder.get(0).orderGoodDiscountInfo.discount = new BigDecimal("0.22");
        // 新增
        orders.get(0).orderAddressInfo = orderSvc.orderAddressInfo();
        orders.get(0).slaveOrder.get(0).orderGood.get(0).orderGoodRemarkInfo = orderSvc.orderGoodRemarkInfo();
        orders.get(0).slaveOrder.get(0).orderGood.add(orderSvc.orderGood());
        orders.get(0).slaveOrder.get(0).orderService = orderSvc.orderServices();
        orders.get(0).slaveOrder.add(orderSvc.slaveOrder());
        orderDomain.save(orders);

        orderDomain.selectAll().from(MasterOrderInfo.class).where().eq(MasterOrderInfo::getId, masterOrderInfoId);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(OrderAddressInfo.class);
        orderDomain.getEntity(OrderGoodRemarkInfo.class);
        orderDomain.getEntity(OrderServiceInfo.class);
        orderDomain.getEntity(OrderServicePriceInfo.class);
        List<Order> ac =  orderDomain.getAutoDomains();

        // 验证插入是否正确 (有事务 可重复执行)
        orderSvc.equal_order(orders, ac);
        return ac;
    }

    @GetMapping("updateSlaveOrder")
    public List<SlaveOrder> updateSlaveOrder(){
        Integer orderGoodInfoId = 4;

        orderDomain.selectAll().from(OrderGoodInfo.class).where().eq(OrderGoodInfo::getId, orderGoodInfoId);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        List<SlaveOrder> slaveOrders = orderDomain.getAutoDomains();

        // 更新
        slaveOrders.get(0).orderGood.get(0).orderGoodInfo.goodName = OrderSvc.UPDATE;
        slaveOrders.get(0).orderGoodDiscountInfo.discount = new BigDecimal("0.22");
        // 新增
        slaveOrders.get(0).orderGood.get(0).orderGoodRemarkInfo = orderSvc.orderGoodRemarkInfo();
        slaveOrders.get(0).orderGood.add(orderSvc.orderGood());
        slaveOrders.get(0).orderService = orderSvc.orderServices();
        slaveOrders.add(orderSvc.slaveOrder());
        orderDomain.save(slaveOrders);

        orderDomain.selectAll().from(OrderServiceInfo.class).where().eq(OrderServiceInfo::getServiceName, OrderSvc.ADD);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderServicePriceInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(OrderGoodRemarkInfo.class);
        List<SlaveOrder> ac =  orderDomain.getAutoDomains();

        orderDomain.selectAll().from(OrderGoodInfo.class).where().eq(OrderGoodInfo::getSlaveOrderInfoId, 5);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderServiceInfo.class);

        // 验证插入是否正确 (有事务 可重复执行)
        slaveOrders.get(1).slaveOrderInfo = null;
        orderSvc.equals_slaveOrder(slaveOrders, ac);
        return ac;
    }

    @GetMapping("addOrder")
    public List<Order> addOrder(){

        List<Order> orders = orderSvc.orders();
        orders.add(orderSvc.order());
        orderDomain.save(orders);

        orderDomain.selectAll().from(MasterOrderInfo.class).where().eq(MasterOrderInfo::getUserName, OrderSvc.ADD);
        orderDomain.execute(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        orderDomain.getEntity(OrderAddressInfo.class);
        orderDomain.getEntity(OrderGoodRemarkInfo.class);
        orderDomain.getEntity(OrderServiceInfo.class);
        orderDomain.getEntity(OrderServicePriceInfo.class);
        List<Order> ac =  orderDomain.getAutoDomains();

        // 验证插入是否正确 (有事务  可重复执行)
        orderSvc.equal_order(orders, ac);
        return ac;
    }
}
