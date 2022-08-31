package com.github.jiuzhuan.domain.repository.example.domain;

import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import com.github.jiuzhuan.domain.repository.domain.annotation.JoinOn;
import com.github.jiuzhuan.domain.repository.example.entity.OrderGoodDiscountInfo;
import com.github.jiuzhuan.domain.repository.example.entity.OrderGoodInfo;
import com.github.jiuzhuan.domain.repository.example.entity.SlaveOrderInfo;
import lombok.Data;

import java.util.List;

/**
 * @author pengfwang@trip.com
 * @date 2022/8/31 22:12
 */
@Dom
@Data
public class SlaveOrder {

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
    public OrderGoodDiscountInfo orderGoodDiscountInfo;
}
