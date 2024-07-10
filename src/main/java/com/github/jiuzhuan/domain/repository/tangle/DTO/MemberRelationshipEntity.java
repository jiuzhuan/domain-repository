package com.github.jiuzhuan.domain.repository.tangle.DTO;

import java.util.List;

/**
 * 表实体
 */
public class MemberRelationshipEntity {

    Long inviterUserId;
    List<Long> inviteeUserIds;

    public MemberRelationshipEntity(Long inviterUserId, List<Long> inviteeUserIds) {
        this.inviterUserId = inviterUserId;
        this.inviteeUserIds = inviteeUserIds;
    }

    public Long getInviterUserId() {
        return inviterUserId;
    }

    public void setInviterUserId(Long inviterUserId) {
        this.inviterUserId = inviterUserId;
    }

    public List<Long> getInviteeUserIds() {
        return inviteeUserIds;
    }

    public void setInviteeUserIds(List<Long> inviteeUserIds) {
        this.inviteeUserIds = inviteeUserIds;
    }
}
