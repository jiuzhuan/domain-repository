package com.github.jiuzhuan.domain.repository.example.repository;

import com.github.jiuzhuan.domain.repository.example.entity.SlaveOrderInfo;
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