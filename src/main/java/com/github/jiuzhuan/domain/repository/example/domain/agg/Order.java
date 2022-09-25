package com.github.jiuzhuan.domain.repository.example.domain.agg;

import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import com.github.jiuzhuan.domain.repository.domain.annotation.JoinOn;
import com.github.jiuzhuan.domain.repository.example.domain.entity.MasterOrderInfo;
import com.github.jiuzhuan.domain.repository.example.domain.entity.OrderAddressInfo;
import com.github.jiuzhuan.domain.repository.example.domain.entity.SlaveOrderInfo;
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
    @JoinOn(joinEntity = MasterOrderInfo.class, joinField = "id")
    public MasterOrderInfo masterOrderInfo;

    /**
     * 收货地址实体
     */
    @JoinOn(joinEntity = OrderAddressInfo.class, joinField = "masterOrderInfoId")
    public OrderAddressInfo orderAddressInfo;

    /**
     * 子单聚合
     */
    @JoinOn(joinEntity = SlaveOrderInfo.class, joinField = "masterOrderInfoId")
    public List<SlaveOrder> slaveOrder;
}
