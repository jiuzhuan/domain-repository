package com.arrety.domainrepository.controller;

import com.arrety.domainrepository.domainpersistence.domain.help.entity.MasterOrderInfo;
import com.arrety.domainrepository.domainpersistence.domain.help.entity.Order;
import com.arrety.domainrepository.domainpersistence.domain.help.entity.SlaveOrderInfo;
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
    Order order;


    @GetMapping("getOrder")
    public List<Order> getOrder(@RequestParam("orderId") Long orderId){
        order.selectAll().from(MasterOrderInfo.class).where().eq(MasterOrderInfo::getId, orderId).selectList(Order.class);
        order.getEntity(SlaveOrderInfo.class);
        return order.get();
    }
}
