package com.github.jiuzhuan.domain.repository.example.domain;

import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import com.github.jiuzhuan.domain.repository.domain.annotation.JoinOn;
import com.github.jiuzhuan.domain.repository.example.domain.entity.OrderGoodDiscountInfo;
import com.github.jiuzhuan.domain.repository.example.domain.entity.OrderGoodInfo;
import com.github.jiuzhuan.domain.repository.example.domain.entity.OrderGoodRemarkInfo;
import com.github.jiuzhuan.domain.repository.example.domain.entity.SlaveOrderInfo;
import lombok.Data;

import java.util.List;

/**
 * @author pengfwang@trip.com
 * @date 2022/8/31 22:12
 */
@Dom
@Data
public class OrderGood {

    /**
     * 商品实体
     */
    @JoinOn(joinEntity = OrderGoodInfo.class, joinField = "id")
    public OrderGoodInfo orderGoodInfo;

    /**
     * 商品备注实体
     */
    @JoinOn(joinEntity = OrderGoodRemarkInfo.class, joinField = "orderGoodInfoId")
    public OrderGoodRemarkInfo orderGoodRemarkInfo;

}
