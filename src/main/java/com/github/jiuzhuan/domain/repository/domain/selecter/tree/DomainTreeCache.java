package com.github.jiuzhuan.domain.repository.domain.selecter.tree;

import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import com.github.jiuzhuan.domain.repository.domain.annotation.JoinOn;
import com.github.jiuzhuan.domain.repository.domain.utils.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 领域聚合结构缓存
 *
 * @author pengfwang@trip.com
 * @date 2022/9/18 15:27
 */
public class DomainTreeCache {

    /**
     * 领域聚合类 - 树
     */
    private static final Map<Class<?>, DomainTree> domainTreeMap = new HashMap<>();

    public static DomainTree get(Class<?> domainClass) {
        DomainTree tree = domainTreeMap.get(domainClass);
        // todo 并发 双重检查锁
        if (tree == null) {
            return init(domainClass);
        }
        return tree;
    }

    private static DomainTree init(Class<?> domainClass) {
        DomainTree domainTree = new DomainTree();
        // 顶层第一个节点作为父节点
        buildDomainTreeNode(domainClass, domainTree, domainClass, null);
        domainTreeMap.put(domainClass, domainTree);
        return domainTree;
    }

    private static void buildDomainTreeNode(Class<?> domainClass, DomainTree domainTree, Class<?> parentDomainClass, DomainTreeNode parentNode) {
        for (Field declaredField : domainClass.getDeclaredFields()) {
            // 没有JoinOn注解的忽略
            JoinOn joinOn = declaredField.getAnnotation(JoinOn.class);
            if (joinOn == null) continue;
            // 判断 实体 or 聚合
            Class<?> entityType = ReflectionUtil.getGenericType(declaredField);
            Dom dom = entityType.getAnnotation(Dom.class);
            if (dom == null) {
                // 实体
                // 如果是聚合的父节点 则不再创建
                if (parentNode != null && entityType.equals(parentNode.entityClass)) {
                    parentNode.fieldName = declaredField.getName();
                    parentNode.entityJoinField = joinOn.joinField();
                    continue;
                }
                // 创建子节点
                DomainTreeNode currentNode = new DomainTreeNode(parentDomainClass, entityType, null, joinOn.joinField(), null, declaredField.getName());

                // 没有父节点 那么第一个属性就作为父节点(约定)
                if (parentNode == null) {
                    parentNode = currentNode;
                    domainTree.rootNode = currentNode;
                    domainTree.entityNodeMap.put(entityType, currentNode);
                    continue;
                }

                // 构建子节点属性
                currentNode.parentNode = parentNode;
                parentNode.subNodes.add(currentNode);

                // 实体 - 加入索引
                domainTree.entityNodeMap.put(entityType, currentNode);
            } else {
                // 顺便为子聚合创建树缓存
                get(entityType);
                // 聚合 - 先创建聚合的父节点
                DomainTreeNode currentNode = new DomainTreeNode(entityType, joinOn.joinEntity(), parentNode, null, joinOn.joinField(), null);

                // 构建子节点属性
                currentNode.parentNode = parentNode;
                parentNode.subNodes.add(currentNode);

                // 实体 - 加入索引
                domainTree.entityNodeMap.put(joinOn.joinEntity(), currentNode);

                // 加入下一层
                buildDomainTreeNode(entityType, domainTree, entityType, currentNode);
            }
        }
    }
}
