package com.arrety.domainrepository.controller;

import com.arrety.domainrepository.domainpersistence.builder.LambdaBuilder;
import com.arrety.domainrepository.domainpersistence.builder.LambdaSelectBuilder;
import com.arrety.domainrepository.domainpersistence.domain.help.entity.MasterOrderInfo;
import com.arrety.domainrepository.domainpersistence.domain.help.entity.OrderDomain;
import com.arrety.domainrepository.domainpersistence.domain.help.entity.SlaveOrderInfo;
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
    public List<OrderDomain> getOrder(@RequestParam("orderId") Integer orderId){

        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from master_order_info where id = ?", orderId);

        lambdaSelectBuilder.clear();
        List<MasterOrderInfo> masterOrderInfos = lambdaSelectBuilder.selectAll().from(MasterOrderInfo.class)
                .where().eq(MasterOrderInfo::getId, orderId).selectList(MasterOrderInfo.class);

        orderDomain.selectAll().from(MasterOrderInfo.class).where().eq(MasterOrderInfo::getId, orderId).selectList(OrderDomain.class);
        orderDomain.getEntity(SlaveOrderInfo.class);
        return orderDomain.get();
    }
}
