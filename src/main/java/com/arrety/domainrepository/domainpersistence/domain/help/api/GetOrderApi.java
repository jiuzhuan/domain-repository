package com.arrety.domainrepository.domainpersistence.domain.help.api;

import com.arrety.domainrepository.domainpersistence.domain.help.entity.MasterOrderInfo;
import com.arrety.domainrepository.domainpersistence.domain.help.entity.OrderDomain;
import com.arrety.domainrepository.domainpersistence.domain.help.entity.SlaveOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author arrety
 * @date 2022/5/12 22:56
 */
@Service
public class GetOrderApi {

    @Autowired
    OrderDomain orderDomain;

    public List<OrderDomain> getOrder(List<Long> masterOrderIds){

        //订单聚合初始查询-主单实体
        orderDomain.selectAll().from(MasterOrderInfo.class).where().in(MasterOrderInfo::getId, masterOrderIds).selectList(OrderDomain.class);

        //关联查询子单实体
        orderDomain.getEntity(SlaveOrderInfo.class);

        //返回聚合结果
        return orderDomain.get();
    }
}
