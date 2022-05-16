package com.arrety.domainrepository.domainpersistence.help;

/**
 * 用户表实体
 * @author arrety
 * @date 2022/2/11 17:40
 */
public class UserEntity {

    private Long userId;
    private String name;
    private Integer phoneNumber;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
