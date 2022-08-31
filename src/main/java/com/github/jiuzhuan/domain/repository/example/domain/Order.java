package com.github.jiuzhuan.domain.repository.example.domain;

import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import com.github.jiuzhuan.domain.repository.domain.annotation.JoinOn;
import com.github.jiuzhuan.domain.repository.example.domain.entity.MasterOrderInfo;
import lombok.Data;

import java.util.List;

/**
 * @author pengfwang@trip.com
 * @date 2022/8/31 22:11
 */
@Dom
@Data
public class Order {

    /**
     * 主单实体
     */
    @JoinOn(joinId = "id")
    public MasterOrderInfo masterOrderInfo;

    /**
     * 子单聚合
     */
    @JoinOn(joinId = "slaveOrderInfo.masterOrderInfoId")
    public List<SlaveOrder> slaveOrder;
}
