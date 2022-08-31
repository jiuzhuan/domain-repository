package com.github.jiuzhuan.domain.repository.builder.help.entity;

/**
 * 角色表实体
 * @author arrety
 * @date 2022/2/11 17:41
 */
public class RoleEntity {
    private Long roleId;
    private Long userId;
    private String title;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
