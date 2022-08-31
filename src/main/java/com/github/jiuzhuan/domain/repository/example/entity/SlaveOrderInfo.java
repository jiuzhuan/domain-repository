package com.github.jiuzhuan.domain.repository.example.entity;

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
