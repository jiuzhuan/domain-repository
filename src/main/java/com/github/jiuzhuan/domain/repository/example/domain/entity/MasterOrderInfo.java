package com.github.jiuzhuan.domain.repository.example.domain.entity;

import lombok.Data;

import javax.persistence.Id;

/**
 * @author arrety
 * @date 2022/5/14 13:45
 */
@Data
public class MasterOrderInfo {

    @Id
    public Integer id;
    public String userName;
}
