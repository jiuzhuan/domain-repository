package com.arrety.domainrepository.domainpersistence.help;



/**
 * 用户-角色联表 聚合实体
 * @author arrety
 * @date 2022/2/11 17:41
 */
public class UserRoleEntity {
    // 如果是自己创建的聚合实体(一般用于联表查询)需要手动加上@Column 和@Type 注解, dal生成的实体自带这两个注解
    private Long userId;
    private Long roleId;
    private String name;
    private Integer phoneNumber;
    private String title;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
