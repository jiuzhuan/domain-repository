package com.arrety.domainrepository.domainpersistence.domain.help.entity;

import com.arrety.domainrepository.domainpersistence.domain.core.Dom;
import com.arrety.domainrepository.domainpersistence.domain.core.JoinOn;
import com.arrety.domainrepository.domainpersistence.domain.core.RequestDomain;
import lombok.Data;

import java.util.List;

/**
 * 子单聚合
 */
@Dom
@Data
public class SlaveOrder extends RequestDomain<SlaveOrder> {

    /**
     * 子单实体
     */
    @JoinOn(joinId = "id")
    public SlaveOrderInfo slaveOrderInfo;

    /**
     * 明细实体
     */
    @JoinOn(joinId = "slaveOrderInfoId")
    public List<OrderGoodInfo> orderGoodInfo;

    /**
     * 优惠分摊实体
     */
    @JoinOn(joinId = "slaveOrderInfoId")
    public OrderGoodDiscountInfo orderGoodDisCountInfo;

}
