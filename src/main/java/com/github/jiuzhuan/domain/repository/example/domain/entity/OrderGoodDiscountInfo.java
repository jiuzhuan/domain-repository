package com.github.jiuzhuan.domain.repository.example.domain.entity;

import lombok.Data;

import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * @author arrety
 * @date 2022/5/14 13:45
 */
@Data
public class OrderGoodDiscountInfo {

    @Id
    public Integer id;
    public Integer slaveOrderInfoId;
    public BigDecimal discount;

}
