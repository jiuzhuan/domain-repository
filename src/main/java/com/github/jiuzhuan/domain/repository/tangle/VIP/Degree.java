package com.github.jiuzhuan.domain.repository.tangle.VIP;

import com.github.jiuzhuan.domain.repository.tangle.DTO.MemberRelationshipDO;
import com.github.jiuzhuan.domain.repository.tangle.DTO.MemberRelationshipEntity;

import java.math.BigDecimal;
import java.util.*;

public class Degree {

    /**
     * 计算用户VIP等级，用户团队包括直接邀请和简介邀请
     * 用户团队（领导者除外）下单金额达到150000，团队领导者升级为VIP1
     * 用户团队中任意2条邀请路径（每个直接下级算一条路径）上有1个及以上用户达到vip1及以上时，领导者升级为vip2
     * 用户团队中任意2条邀请路径（每个直接下级算一条路径）上有1个及以上用户达到vip2及以上时，领导者升级为vip3
     * 用户团队中任意2条邀请路径（每个直接下级算一条路径）上有1个及以上用户达到vip3及以上时，领导者升级为vip4
     * 用户团队中任意2条邀请路径（每个直接下级算一条路径）上有1个及以上用户达到vip4及以上时，领导者升级为vip5
     * 用户团队中任意2条邀请路径（每个直接下级算一条路径）上有1个及以上用户达到vip5及以上时，领导者升级为vip6
     *
     * @param leaderUserId 用户id
     * @return 团队所有成员情况（结构化的）
     * @see MemberRelationshipDO#getDegree()
     */
    public static MemberRelationshipDO calculateLeaderDegree(Long leaderUserId) {

        MemberRelationshipDO memberRelationshipDO = new MemberRelationshipDO(leaderUserId, 0, BigDecimal.ZERO, BigDecimal.ZERO);

        // todo 查表：查询用户leaderUserId订单金额
        BigDecimal orderAmount = orderAmountMap.get(leaderUserId);
        memberRelationshipDO.setOrderAmount(orderAmount);

        // todo 查表： 查询用户是否隐藏
        if (!Objects.equals(true, hiddenMap.get(leaderUserId)))
            memberRelationshipDO.setVisibleOrderAmount(orderAmount);

        // todo 查表：查询用户leaderUserId邀请关系
        MemberRelationshipEntity entity = relationMap.get(leaderUserId);

        // 叶子节点直接返回
        if (entity == null) {
            return memberRelationshipDO;
        }

        // 遍历直接下级成员，设置团队成员总下单金额，设置自身等级，设置隐藏最高等级，设置直接下级成员
        Integer topOneDegree = 0;
        Integer topTwoDegree = 0;
        List<MemberRelationshipDO> sonMemberRelationDOs = new ArrayList<>();
        for (Long inviteeUserId : entity.getInviteeUserIds()) {

            // 深度优先遍历
            MemberRelationshipDO sonMemberRelationshipDO = calculateLeaderDegree(inviteeUserId);

            sonMemberRelationDOs.add(sonMemberRelationshipDO);
            BigDecimal sonTotal = sonMemberRelationshipDO.getTeamTotalOrderAmount().add(sonMemberRelationshipDO.getOrderAmount());
            memberRelationshipDO.setTeamTotalOrderAmount(memberRelationshipDO.getTeamTotalOrderAmount().add(sonTotal));
            BigDecimal visibleSonTotal = sonMemberRelationshipDO.getVisibleTeamTotalOrderAmount().add(sonMemberRelationshipDO.getVisibleOrderAmount());
            memberRelationshipDO.setVisibleTeamTotalOrderAmount(memberRelationshipDO.getVisibleTeamTotalOrderAmount().add(visibleSonTotal));

            // 记录成员中等级最高的两名
            Integer hiddenHighestDegree = sonMemberRelationshipDO.getHiddenHighestDegree();
            if (hiddenHighestDegree > topOneDegree) {
                topTwoDegree = topOneDegree;
                topOneDegree = hiddenHighestDegree;
            } else if (hiddenHighestDegree > topTwoDegree)
                topTwoDegree = hiddenHighestDegree;
        }
        memberRelationshipDO.setSons(sonMemberRelationDOs);

        // 规则2、3、4、5、6、7： 任意2条路径上有1个用户达到vip1时，用户升级为vip2
        if (topOneDegree > 0) {
            if (topOneDegree.equals(topTwoDegree))
                memberRelationshipDO.setDegree(topOneDegree + 1);
            else
                memberRelationshipDO.setDegree(Math.min(topOneDegree, topTwoDegree) + 1);
        }

        // 规则1：用户团队累计下单金额达标，用户升级为vip1
        else if (memberRelationshipDO.getTeamTotalOrderAmount().compareTo(new BigDecimal(150000)) >= 0)
            memberRelationshipDO.setDegree(1);

        memberRelationshipDO.setHiddenHighestDegree(Math.max(topOneDegree, memberRelationshipDO.getDegree()));
        return memberRelationshipDO;
    }


    public static Map<Long, MemberRelationshipEntity> relationMap = new HashMap<>();
    public static Map<Long, BigDecimal> orderAmountMap = new HashMap<>();
    public static Map<Long, Boolean> hiddenMap = new HashMap<>();

    static {
        relationMap.put(1L, new MemberRelationshipEntity(1L, List.of(2L, 6L)));
        relationMap.put(2L, new MemberRelationshipEntity(2L, List.of(3L)));
        relationMap.put(3L, new MemberRelationshipEntity(3L, List.of(4L, 5L)));
        relationMap.put(4L, new MemberRelationshipEntity(4L, List.of(13L)));
        relationMap.put(5L, new MemberRelationshipEntity(5L, List.of(14L)));
        relationMap.put(6L, new MemberRelationshipEntity(6L, List.of(7L, 8L, 9L)));
        relationMap.put(8L, new MemberRelationshipEntity(8L, List.of(11L)));
        relationMap.put(9L, new MemberRelationshipEntity(9L, List.of(10L)));
        relationMap.put(11L, new MemberRelationshipEntity(11L, List.of(12L)));
        relationMap.put(14L, new MemberRelationshipEntity(14L, List.of(15L)));
        orderAmountMap.put(1L, new BigDecimal(0));
        orderAmountMap.put(2L, new BigDecimal(0));
        orderAmountMap.put(3L, new BigDecimal(8888));
        orderAmountMap.put(4L, new BigDecimal(0));
        orderAmountMap.put(5L, new BigDecimal(150000));
        orderAmountMap.put(6L, new BigDecimal(0));
        orderAmountMap.put(7L, new BigDecimal(0));
        orderAmountMap.put(8L, new BigDecimal(0));
        orderAmountMap.put(9L, new BigDecimal(0));
        orderAmountMap.put(10L, new BigDecimal(150000));
        orderAmountMap.put(11L, new BigDecimal(100000));
        orderAmountMap.put(12L, new BigDecimal(50000));
        orderAmountMap.put(13L, new BigDecimal(150000));
        orderAmountMap.put(14L, new BigDecimal(150000));
        orderAmountMap.put(15L, new BigDecimal(150000));
        hiddenMap.put(15L, true);
    }
}
