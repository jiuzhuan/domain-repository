package com.arrety.domainrepository.domainpersistence.domain.help.api;

import com.arrety.domainrepository.domainpersistence.domain.help.entity.SlaveOrderInfo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arrety
 * @date 2022/5/15 0:10
 */
@Repository
public class SlaveOrderInfoRepository {


    public List<SlaveOrderInfo> getByMasterOrderInfoIds(List<Long> masterOrderIds) {
        return new ArrayList<>();
    }
}
