package com.github.jiuzhuan.domain.repository.example.service;

import com.github.jiuzhuan.domain.repository.example.domain.Order;
import com.github.jiuzhuan.domain.repository.example.domain.SlaveOrder;
import com.github.jiuzhuan.domain.repository.example.entity.MasterOrderInfo;
import com.github.jiuzhuan.domain.repository.example.entity.SlaveOrderInfo;
import com.github.jiuzhuan.domain.repository.example.repository.MasterOrderInfoRepository;
import com.github.jiuzhuan.domain.repository.example.repository.SlaveOrderInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author arrety
 * @date 2022/5/12 22:56
 */
@Service
public class OrderServiceOld {

    @Autowired
    MasterOrderInfoRepository masterOrderInfoRepository;

    @Autowired
    SlaveOrderInfoRepository slaveOrderInfoRepository;

    public List<Order> getOrder(List<Long> masterOrderIds){

        //查询主单实体
        List<MasterOrderInfo> masterOrderInfos = masterOrderInfoRepository.getByIds(masterOrderIds);

        //关联查询子单实体
        List<SlaveOrderInfo> slaveOrderInfos = slaveOrderInfoRepository.getByMasterOrderInfoIds(masterOrderIds);
        Map<Integer, SlaveOrderInfo> slaveOrderInfoMap = slaveOrderInfos.stream().collect(Collectors.toMap(SlaveOrderInfo::getMasterOrderInfoId, slaveOrderInfo -> slaveOrderInfo));

        //组装查询结果
        List<Order> orders = new ArrayList<>();
        for (MasterOrderInfo masterOrderInfo : masterOrderInfos) {
            Order order = new Order();
            order.setMasterOrderInfo(masterOrderInfo);
            SlaveOrder slaveOrder = new SlaveOrder();
            slaveOrder.setSlaveOrderInfo(slaveOrderInfoMap.get(masterOrderInfo.getId()));
            order.setSlaveOrder(Collections.singletonList(slaveOrder));
            orders.add(order);
        }

        //返回聚合结果
        return orders;
    }
}
