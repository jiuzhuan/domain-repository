package com.arrety.domainrepository.domainpersistence.domain.help.entity;

import com.arrety.domainrepository.domainpersistence.domain.core.Dom;
import com.arrety.domainrepository.domainpersistence.domain.core.RequestDomain;
import lombok.Data;

/**
 * @author arrety
 * @date 2022/5/14 13:45
 */
@Dom
@Data
public class OrderGoodDiscountInfo extends RequestDomain<OrderGoodDiscountInfo> {

    public long id;
    public long orderGoodId;

}
