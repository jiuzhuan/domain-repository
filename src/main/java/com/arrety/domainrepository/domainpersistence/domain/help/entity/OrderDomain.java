package com.arrety.domainrepository.domainpersistence.domain.help.entity;

import com.arrety.domainrepository.domainpersistence.domain.core.Dom;
import com.arrety.domainrepository.domainpersistence.domain.core.JoinOn;
import com.arrety.domainrepository.domainpersistence.domain.core.RequestDomain;
import lombok.Data;

import java.util.List;

/**
 * 订单聚合
 */
@Dom
@Data
public class OrderDomain extends RequestDomain<OrderDomain> {

    /**
     * 主单实体
     */
    @JoinOn(joinId = "id")
    public MasterOrderInfo masterOrderInfo;

    /**
     * 子单聚合
     */
    @JoinOn(joinId = "slaveOrderInfo.masterOrderId")
    public List<SlaveOrder> slaveOrder;
}
