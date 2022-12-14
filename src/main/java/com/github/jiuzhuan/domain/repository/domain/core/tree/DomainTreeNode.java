package com.github.jiuzhuan.domain.repository.domain.core.tree;

import java.lang.reflect.Field;
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
     * 自身的约束字段（出箭头）
     */
    public String entityJoinField;

    /**
     * 父节点对本节点的约束字段（入箭头）
     */
    public String parentJoinField;

    /**
     * 所属的聚合
     */
    public Class<?> parentDomClass;

    /**
     * 所属的聚合的层次
     */
    public Integer parentDomClassLevel;

    /**
     * 是否叶子节点
     */
    public Boolean isLeaf;

    /**
     * 所在属性的属性名
     */
    public String fieldName;

    /**
     * 所在聚合在父聚合里的属性名
     */
    public String parentFieldName;

    /**
     * 主键属性
     */
    public Field idField;


    public DomainTreeNode(Integer parentDomClassLevel, Class<?> parentDomClass, Class<?> entityClass, DomainTreeNode parentNode,
                          String entityJoinField, String parentJoinField, Boolean isLeaf, String fieldName, String parentFieldName, Field idField) {
        this.parentDomClass = parentDomClass;
        this.entityClass = entityClass;
        this.parentNode = parentNode;
        this.entityJoinField = entityJoinField;
        this.parentJoinField = parentJoinField;
        this.parentDomClassLevel = parentDomClassLevel;
        this.isLeaf = isLeaf;
        this.fieldName = fieldName;
        this.parentFieldName = parentFieldName;
        this.idField = idField;
    }
}
