package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.example.common.utils.OrderCompareUtil;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.Order;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.OrderGood;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.SlaveOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 测试用例
 * @author pengfwang@trip.com
 * @date 2022/9/23 14:48
 */
@Transactional
@SpringBootTest
class OrderControllerTest {

    @Autowired
    OrderController orderController;

    @Autowired
    OrderController_old orderControllerOld;

    @Test
    void getOrders() {
        // 测试查询全部: 初始化查询全部主单 关联查询全部其它实体 并自动映射
        List<Order> orders = orderController.getOrders();
        OrderCompareUtil.equal_order(orderControllerOld.getOrders2(), orders);
    }

    @Test
    void getOrderGoods() {
        // 测试指定映射: 初始化查询全部主单/商品 关联查询部分其它实体 并指定映射到子聚合
        List<OrderGood> orderGoods = orderController.getOrderGoods();
        OrderCompareUtil.equals(orderControllerOld.getOrderGoods2(), orderGoods);
    }

    @Test
    void getOrderByOrderId() {
        // 测试初始化聚合根查询: 初始化按条件查询主单 关联查询部分其它实体 并自动映射
        List<Order> orders = orderController.getOrderByOrderId(1);
        OrderCompareUtil.equals(orderControllerOld.getOrderByOrderId2(1), orders);
    }

    @Test
    void getOrderBySlaveOrderInfoId() {
        // 测试初始化中间实体查询: 初始化按条件查询子单 关联查询部分其它实体 并自动映射
        List<Order> orders = orderController.getOrderBySlaveOrderInfoId(1);
        OrderCompareUtil.equals(orderControllerOld.getOrderBySlaveOrderInfoId2(1), orders);
    }

    @Test
    void getOrderByOrderGoodInfoId() {
        // 测试初始化叶子实体: 初始化按条件查询商品 关联查询部分其它实体 并自动映射
        List<Order> orders = orderController.getOrderByOrderGoodInfoId(1);
        OrderCompareUtil.equals(orderControllerOld.getOrderByOrderGoodInfoId2(1), orders);
    }

    @Test
    void getOrderByOrderGoodDiscountInfoId() {
        // 测试自下而上关联查询: 初始化按条件查询折扣 关联查询部分其它实体 并自动映射
        List<Order> orders = orderController.getOrderByOrderGoodDiscountInfoId(1);
        OrderCompareUtil.equals(orderControllerOld.getOrderByOrderGoodDiscountInfoId2(1), orders);
    }

    @Test
    void getGoodByDiscountId() {
        // 测试同级叶子实体关联查询: 初始化按条件查询折扣 关联查询商品 并自动映射
        List<SlaveOrder> slaveOrders = orderController.getGoodByDiscountId(1);
        OrderCompareUtil.equals(orderControllerOld.getGoodByDiscountId2(1), slaveOrders);
    }

    @Test
    void getMasterByGoodId() {
        // 测试自下而上跳跃关联查询: 初始化按条件查询商品 关联查询主单 并自动映射
        List<Order> orders = orderController.getMasterByGoodId(1);
        OrderCompareUtil.equals(orderControllerOld.getMasterByGoodId2(1), orders);
    }

    @Test
    void getGoodByServiceId() {
        // 测试同级中间实体关联查询: 初始化按条件查询服务 关联查询商品 并自动映射
        List<SlaveOrder> slaveOrders = orderController.getGoodByServiceId(1);
        OrderCompareUtil.equals(orderControllerOld.getGoodByServiceId2(1), slaveOrders);
    }

    @Test
    void updateOrderBySlaveId() {
        // 测试LambdaBuilder更新
        String userName = "小李-update-1";
        List<Order> orders = orderController.updateOrderById(3, userName);
        assert Objects.equals(orders.get(0).masterOrderInfo.userName, userName);
    }

    @Test
    void updateOrder() {
        // 测试有聚合根的保存(新增和更新)
        List<Order> orders = orderController.updateOrder();
    }

    @Test
    void updateSlaveOrder() {
        // 测试无聚合根的保存(新增和更新)
        List<SlaveOrder> slaveOrders = orderController.updateSlaveOrder();
    }

    @Test
    void addOrder() {
        // 测试纯新增
        List<Order> orders = orderController.addOrder();
    }

    @Test
    void addSlaveOrder() {
        // 测试纯新增
        List<Order> orders = orderController.addSlaveOrder();
    }
}