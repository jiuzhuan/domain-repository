package com.arrety.domainrepository.domainpersistence.domain.help.entity;

import com.arrety.domainrepository.domainpersistence.domain.core.Dom;
import com.arrety.domainrepository.domainpersistence.domain.core.RequestDomain;
import lombok.Data;

/**
 * @author arrety
 * @date 2022/5/14 13:45
 */
@Data
public class SlaveOrderInfo {

    public Integer id;
    public Integer masterOrderInfoId;
    public String storeName;

}
