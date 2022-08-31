package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.builder.builder.LambdaSelectBuilder;
import com.github.jiuzhuan.domain.repository.example.domain.Order;
import com.github.jiuzhuan.domain.repository.example.entity.MasterOrderInfo;
import com.github.jiuzhuan.domain.repository.example.domain.OrderDomain;
import com.github.jiuzhuan.domain.repository.example.entity.OrderGoodDiscountInfo;
import com.github.jiuzhuan.domain.repository.example.entity.OrderGoodInfo;
import com.github.jiuzhuan.domain.repository.example.entity.SlaveOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author arrety
 * @date 2022/1/29 11:12
 */
@RestController
public class OrderController {

    @Autowired
    OrderDomain orderDomain;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    LambdaSelectBuilder lambdaSelectBuilder;


    @GetMapping("getOrder")
    public List<Order> getOrder(@RequestParam("orderId") Integer orderId){

        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from master_order_info where id = ?", orderId);

        lambdaSelectBuilder.clear();
        List<MasterOrderInfo> masterOrderInfos = lambdaSelectBuilder.selectAll().from(MasterOrderInfo.class)
                .where().eq(MasterOrderInfo::getId, orderId).selectList(MasterOrderInfo.class);

        orderDomain.selectAll().from(MasterOrderInfo.class).where().eq(MasterOrderInfo::getId, orderId).selectList(Order.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        orderDomain.getEntity(OrderGoodDiscountInfo.class);
        orderDomain.getEntity(OrderGoodInfo.class);
        return orderDomain.get();
    }
}
