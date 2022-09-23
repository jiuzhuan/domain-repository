package com.github.jiuzhuan.domain.repository.example.domain.entity;

import lombok.Data;

/**
 * @author pengfwang@trip.com
 * @date 2022/9/23 18:20
 */
@Data
public class OrderAddressInfo {

    public Integer id;
    public Integer masterOrderInfoId;
    public String address;
}
