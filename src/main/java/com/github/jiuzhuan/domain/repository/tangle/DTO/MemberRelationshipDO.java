package com.github.jiuzhuan.domain.repository.tangle.DTO;

import java.math.BigDecimal;
import java.util.List;

public class MemberRelationshipDO {

    /** 用户id */
    Long userId;
    /** 是否隐身 */
    Boolean hidden = false;
    /** 用户vip等级 */
    Integer degree = 0;
    /** 用户团队成员最高等级 */
    Integer hiddenHighestDegree;
    /** 用户下单金额（自身得） */
    BigDecimal orderAmount;
    /** 用户下单金额（自身得）(可见的)  */
    BigDecimal visibleOrderAmount = BigDecimal.ZERO;
    /** 用户所有团队成员下单金额（不包含自身得） */
    BigDecimal teamTotalOrderAmount;
    /** 用户所有团队成员下单金额（不包含自身得）(可见的) */
    BigDecimal visibleTeamTotalOrderAmount = BigDecimal.ZERO;
    /** 用户所有团队成员成员，等级严格递减成员下单金额（不包含自身）(可见的) */
    BigDecimal segmentTotalOrderAmount;
    /** 用户直接下级成员 */
    List<MemberRelationshipDO> sons;

    public MemberRelationshipDO(Long userId, Integer hiddenHighestDegree, BigDecimal orderAmount, BigDecimal teamTotalOrderAmount) {
        this.userId = userId;
        this.hiddenHighestDegree = hiddenHighestDegree;
        this.orderAmount = orderAmount;
        this.teamTotalOrderAmount = teamTotalOrderAmount;
    }

    public Integer getHiddenHighestDegree() {
        return hiddenHighestDegree;
    }

    public void setHiddenHighestDegree(Integer hiddenHighestDegree) {
        this.hiddenHighestDegree = hiddenHighestDegree;
    }

    public BigDecimal getTeamTotalOrderAmount() {
        return teamTotalOrderAmount;
    }

    public void setTeamTotalOrderAmount(BigDecimal teamTotalOrderAmount) {
        this.teamTotalOrderAmount = teamTotalOrderAmount;
    }

    public Integer getDegree() {
        return degree;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    public List<MemberRelationshipDO> getSons() {
        return sons;
    }

    public void setSons(List<MemberRelationshipDO> sons) {
        this.sons = sons;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getSegmentTotalOrderAmount() {
        return segmentTotalOrderAmount;
    }

    public void setSegmentTotalOrderAmount(BigDecimal segmentTotalOrderAmount) {
        this.segmentTotalOrderAmount = segmentTotalOrderAmount;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public BigDecimal getVisibleTeamTotalOrderAmount() {
        return visibleTeamTotalOrderAmount;
    }

    public void setVisibleTeamTotalOrderAmount(BigDecimal visibleTeamTotalOrderAmount) {
        this.visibleTeamTotalOrderAmount = visibleTeamTotalOrderAmount;
    }

    public BigDecimal getVisibleOrderAmount() {
        return visibleOrderAmount;
    }

    public void setVisibleOrderAmount(BigDecimal visibleOrderAmount) {
        this.visibleOrderAmount = visibleOrderAmount;
    }
}
