package com.arrety.domainrepository.domainpersistence.domain.help.entity;

import com.arrety.domainrepository.domainpersistence.domain.core.Dom;
import com.arrety.domainrepository.domainpersistence.domain.core.RequestDomain;

/**
 * @author arrety
 * @date 2022/5/14 13:45
 */
@Dom
public class OrderGoodInfo extends RequestDomain<OrderGoodInfo> {

    public OrderGoodInfo orderGoodInfo;
    public OrderGoodDiscountInfo orderGoodDisCountInfo;

}
