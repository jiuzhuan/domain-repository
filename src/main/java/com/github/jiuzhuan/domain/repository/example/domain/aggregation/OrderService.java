package com.github.jiuzhuan.domain.repository.example.domain.aggregation;

import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import com.github.jiuzhuan.domain.repository.domain.annotation.JoinOn;
import com.github.jiuzhuan.domain.repository.example.domain.entity.OrderServiceInfo;
import com.github.jiuzhuan.domain.repository.example.domain.entity.OrderServicePriceInfo;
import lombok.Data;

/**
 * @author pengfwang@trip.com
 * @date 2022/8/31 22:12
 */
@Dom
@Data
public class OrderService {

    /**
     * 订单服务实体
     */
    @JoinOn(joinEntity = OrderServiceInfo.class, joinField = "id")
    public OrderServiceInfo orderServiceInfo;

    /**
     * 服务价格实体
     */
    @JoinOn(joinEntity = OrderServicePriceInfo.class, joinField = "orderServiceInfoId")
    public OrderServicePriceInfo orderServicePriceInfo;

}
