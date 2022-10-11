package com.github.jiuzhuan.domain.repository.example.domain.agg;

import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import com.github.jiuzhuan.domain.repository.domain.annotation.JoinOn;
import com.github.jiuzhuan.domain.repository.example.domain.entity.OrderGoodDiscountInfo;
import com.github.jiuzhuan.domain.repository.example.domain.entity.OrderGoodInfo;
import com.github.jiuzhuan.domain.repository.example.domain.entity.OrderServiceInfo;
import com.github.jiuzhuan.domain.repository.example.domain.entity.SlaveOrderInfo;
import lombok.AllArgsConstructor;
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
    @JoinOn(joinEntity = SlaveOrderInfo.class, joinField = "id")
    public SlaveOrderInfo slaveOrderInfo;

    /**
     * 商品聚合
     */
    @JoinOn(joinEntity = OrderGoodInfo.class, joinField = "slaveOrderInfoId")
    public List<OrderGood> orderGood;

    /**
     * 附加服务 运费险/保价险
     */
    @JoinOn(joinEntity = OrderServiceInfo.class, joinField = "slaveOrderInfoId")
    public List<OrderService> orderService;

    /**
     * 优惠分摊实体
     */
    @JoinOn(joinEntity = OrderGoodDiscountInfo.class, joinField = "slaveOrderInfoId")
    public OrderGoodDiscountInfo orderGoodDiscountInfo;
}
