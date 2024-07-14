package com.github.jiuzhuan.domain.repository.tangle.VIP;

import java.math.BigDecimal;
import java.util.*;

public class Points {


    /**
     * 计算用户vip积分主方法
     * 积分 = 顺差 + 逆差
     * 顺差 = leader 积分率 * 累计团队下单金额
     * 逆差 = 积分率相减 * 累计团队订单金额
     *
     * @param leaderUserId 用户id
     * @return 积分
     */
    public static BigDecimal calculateLeaderPoints(Long leaderUserId) {

        // 复用计算等级方法
        MemberRelationshipDO leader = Degree.calculateLeaderDegree(leaderUserId);

        // 路径上第一次出现逆差之前属于严格递减不存在极差： 用领导者积分率 * 路径上用户下单总额
        List<MemberRelationshipDO> relativeRelations = new ArrayList<>();
        BigDecimal absOrderAmount = iterateRelation(leader, relativeRelations);

        // 第二次出现的逆差需要特殊处理 （大于第二次的逆差和 第二次逆差处理方式相同）
        List<MemberRelationshipDO> relativeRelationSegments = iterateRelativeRelation(leader.getDegree(), 0, relativeRelations, new ArrayList<>());

        // 严格递减： leader 积分率 * 累计下单金额
        Double leaderDegreeRate = degreeRate.get(leader.getDegree());
        BigDecimal absPoints = absOrderAmount.multiply(BigDecimal.valueOf(leaderDegreeRate));

        // 逆差：积分率相减 * 订单金额
        BigDecimal relativePoints = BigDecimal.ZERO;
        for (MemberRelationshipDO segment : relativeRelationSegments) {
            double rateGap = leaderDegreeRate - degreeRate.get(segment.getDegree());
            BigDecimal segmentPoint = segment.getSegmentTotalOrderAmount().multiply(BigDecimal.valueOf(rateGap));
            relativePoints = relativePoints.add(segmentPoint);
        }
        return absPoints.add(relativePoints);
    }

    /**
     * 剪枝，使每个分支严格递减，计算每个严格递减分支的团队下单金额，返回所有分支的头节点
     *
     * @param leaderDegree       等级超过领导者直接删除
     * @param directLeaderDegree 等级超过直接上级则剪枝
     * @param sonRelations       直接下级成员
     * @return 所有分支的头节点
     */
    private static List<MemberRelationshipDO> iterateRelativeRelation(Integer leaderDegree, Integer directLeaderDegree, List<MemberRelationshipDO> sonRelations, List<MemberRelationshipDO> relativeRelationSegments) {

        for (MemberRelationshipDO relativeRelation : sonRelations) {

            // 领导者逆差：直接丢弃团队
            if (relativeRelation.getDegree() > leaderDegree)
                continue;

            // 先记录片段头
            relativeRelationSegments.add(relativeRelation);

            // 处理连续递减
            List<MemberRelationshipDO> absSegment = relativeAbsSegment(leaderDegree, directLeaderDegree, relativeRelation, relativeRelationSegments);

            // 普通逆差 第二次逆差 剪枝 减去订单金额
            relativeRelation.setSegmentTotalOrderAmount(relativeRelation.getVisibleTeamTotalOrderAmount());
            if (absSegment == null) continue;
            for (MemberRelationshipDO abs : absSegment) {
                BigDecimal subtract = relativeRelation.getVisibleTeamTotalOrderAmount()
                        .subtract(abs.getVisibleTeamTotalOrderAmount())
                        .subtract(abs.getVisibleOrderAmount());
                relativeRelation.setSegmentTotalOrderAmount(subtract);
            }
        }
        return relativeRelationSegments;
    }

    /**
     * 处理片段中的连续递减
     *
     * @param leaderDegree             透传
     * @param directLeaderDegree       判断是否连续递减
     * @param relativeRelation         要处理的分支
     * @param relativeRelationSegments 片段头
     * @return 严格递减的分支列表
     */
    private static List<MemberRelationshipDO> relativeAbsSegment(Integer leaderDegree, Integer directLeaderDegree, MemberRelationshipDO relativeRelation, List<MemberRelationshipDO> relativeRelationSegments) {

        List<MemberRelationshipDO> sons = relativeRelation.getSons();

        // 尾节点对积分不产生任何影响，直接丢弃
        if (sons == null || sons.isEmpty())
            return null;

        List<MemberRelationshipDO> newSegments = new ArrayList<>();
        Iterator<MemberRelationshipDO> iterator = sons.iterator();
        while (iterator.hasNext()) {
            MemberRelationshipDO next = iterator.next();

            // 严格递减
            if (relativeRelation.getDegree() > next.getDegree()) {
                List<MemberRelationshipDO> segments = relativeAbsSegment(leaderDegree, next.getDegree(), next, relativeRelationSegments);
                Optional.ofNullable(segments).ifPresent(newSegments::addAll);
            }

            // 逆差出现，剪枝
            else {

                // 普通逆差 第二次逆差 剪枝
                List<MemberRelationshipDO> segments = iterateRelativeRelation(leaderDegree, directLeaderDegree, List.of(next), relativeRelationSegments);
                newSegments.addAll(segments);
                iterator.remove();
            }
        }
        return newSegments;
    }

    /**
     * 第一次迭代，获取顺差下单金额，获取逆差头节点
     *
     * @param relativeRelations 所有逆差头节点
     * @return 顺差下单金额
     */
    private static BigDecimal iterateRelation(MemberRelationshipDO leader, List<MemberRelationshipDO> relativeRelations) {

        BigDecimal absOrderAmount = BigDecimal.ZERO;
        List<MemberRelationshipDO> sons = leader.getSons();
        if (sons == null || sons.isEmpty()) return absOrderAmount;

        for (MemberRelationshipDO son : sons) {

            // 严格递减
            if (leader.getDegree() > son.getDegree()) {
                absOrderAmount = absOrderAmount.add(son.getVisibleOrderAmount());
                BigDecimal sonAbsOrderAmount = iterateRelation(son, relativeRelations);
                absOrderAmount = absOrderAmount.add(sonAbsOrderAmount);
            }

            // 第一次逆差 剪枝
            else
                relativeRelations.add(son);
        }
        return absOrderAmount;
    }

    public static Map<Integer, Double> degreeRate = new HashMap<>();

    static {
        degreeRate.put(0, 0.0);
        degreeRate.put(1, 0.1);
        degreeRate.put(2, 0.2);
        degreeRate.put(3, 0.3);
        degreeRate.put(4, 0.4);
        degreeRate.put(5, 0.5);
        degreeRate.put(6, 0.6);
    }}
