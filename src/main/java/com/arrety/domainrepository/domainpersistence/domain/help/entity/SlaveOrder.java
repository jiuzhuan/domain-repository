package com.arrety.domainrepository.domainpersistence.domain.help.entity;

import com.arrety.domainrepository.domainpersistence.domain.core.Dom;
import com.arrety.domainrepository.domainpersistence.domain.core.RequestDomain;
import lombok.Data;

/**
 * 子单聚合
 */
@Dom
@Data
public class SlaveOrder extends RequestDomain<SlaveOrder> {

    /**
     * 子单实体
     */
    public SlaveOrderInfo slaveOrderInfo;

    /**
     * 明细聚合
     */
    public OrderGood orderGood;

}
