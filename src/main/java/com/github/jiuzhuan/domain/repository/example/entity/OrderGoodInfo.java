package com.github.jiuzhuan.domain.repository.example.entity;

import lombok.Data;

/**
 * @author arrety
 * @date 2022/5/14 13:45
 */
@Data
public class OrderGoodInfo {

    public Integer id;
    public Integer slaveOrderInfoId;
    public String goodName;

}