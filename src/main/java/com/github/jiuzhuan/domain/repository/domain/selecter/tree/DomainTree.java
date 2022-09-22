package com.github.jiuzhuan.domain.repository.domain.selecter.tree;

import com.github.jiuzhuan.domain.repository.example.domain.Order;

import java.util.*;

/**
 * 领域聚合结构化-树
 * 这个树和领域聚合不完全对应, 要对领域聚合做稍微变化: Master实体(表)上浮到父节点那一层
 * 1. 每个节点都是一个实体(对应一个表)
 * 2. 节点的入箭头代表实体包含某个字段用来关联别的实体
 * 3. 节点的出箭头代表实体中某个字段被其它实体保存用以关联
 * 4. 根节点只有出箭头
 * 5. 叶子节点只有入箭头
 * 6. 中间节点有入箭头也有出箭头, 他是特殊的中间实体(表)
 * @author pengfwang@trip.com
 * @date 2022/9/18 15:13
 */
public class DomainTree {

    /**
     * 聚合的根节点
     */
    public DomainTreeNode rootNode;

    /**
     * 索引-记录聚合内每个实体在哪一层
     */
    public Map<Class<?>, DomainTreeNode> entityNodeMap = new HashMap<>();

    public DomainTreeNode getNodeByEntity(Class<?> entity){
        return entityNodeMap.get(entity);
    }

    /**
     * 寻找目标节点最近的已知的约束节点到目标节点路径 (动态规划改)
     */
    public List<DomainTreeNode> recentKnownNode(DomainTreeNode targetNode, Set<DomainTreeNode> domainTreeNodes) {
        // TODO: 2022/9/20
        return Arrays.asList(getNodeByEntity(Order.class));
    }

}
