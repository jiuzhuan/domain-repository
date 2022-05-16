package com.arrety.domainrepository.domainpersistence.domain.help.api;

import com.arrety.domainrepository.domainpersistence.domain.help.entity.MasterOrderInfo;
import com.arrety.domainrepository.domainpersistence.domain.help.entity.Order;
import com.arrety.domainrepository.domainpersistence.domain.help.entity.SlaveOrder;
import com.arrety.domainrepository.domainpersistence.domain.help.entity.SlaveOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author arrety
 * @date 2022/5/12 22:56
 */
@Service
public class GetOrderApi2 {

    @Autowired
    MasterOrderInfoRepository masterOrderInfoRepository;

    @Autowired
    SlaveOrderInfoRepository slaveOrderInfoRepository;

    public List<Order> getOrder(List<Long> masterOrderIds){

        //查询主单实体
        List<MasterOrderInfo> masterOrderInfos = masterOrderInfoRepository.getByIds(masterOrderIds);

        //关联查询子单实体
        List<SlaveOrderInfo> slaveOrderInfos = slaveOrderInfoRepository.getByMasterOrderInfoIds(masterOrderIds);
        Map<Long, SlaveOrderInfo> slaveOrderInfoMap = slaveOrderInfos.stream().collect(Collectors.toMap(SlaveOrderInfo::getMasterOrderId, slaveOrderInfo -> slaveOrderInfo));

        //组装查询结果
        List<Order> orders = new ArrayList<>();
        for (MasterOrderInfo masterOrderInfo : masterOrderInfos) {
            Order order = new Order();
            order.setMasterOrderInfo(masterOrderInfo);
            SlaveOrder slaveOrder = new SlaveOrder();
            slaveOrder.setSlaveOrderInfo(slaveOrderInfoMap.get(masterOrderInfo.getId()));
            order.setSlaveOrder(slaveOrder);
            orders.add(order);
        }

        //返回聚合结果
        return orders;
    }
}
