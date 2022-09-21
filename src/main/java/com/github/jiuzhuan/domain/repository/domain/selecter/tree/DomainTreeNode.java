package com.github.jiuzhuan.domain.repository.domain.selecter.tree;

import java.util.*;

/**
 * 领域聚合结构化中树的节点
 * @author pengfwang@trip.com
 * @date 2022/9/18 15:03
 */
public class DomainTreeNode {

    /**
     * 节点对应的实体类(表)
     */
    public Class<?> entityClass;

    /* 父节点 */
    public DomainTreeNode parentNode;

    /* 下层 */
    public List<DomainTreeNode> subNodes = new ArrayList<>();

    /**
     * 自身的约束字段
     */
    public String entityJoinField;


    public DomainTreeNode(Class<?> entityClass, DomainTreeNode parentNode, String entityJoinField) {
        this.entityClass = entityClass;
        this.parentNode = parentNode;
        this.entityJoinField = entityJoinField;
    }
}