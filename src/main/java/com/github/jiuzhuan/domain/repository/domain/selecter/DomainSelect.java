package com.github.jiuzhuan.domain.repository.domain.selecter;

import com.github.jiuzhuan.domain.repository.builder.builder.LambdaSelectDomBuilder;
import com.github.jiuzhuan.domain.repository.builder.builder.LambdaSelectItemBuilder;
import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import com.github.jiuzhuan.domain.repository.domain.selecter.tree.DomainTree;
import com.github.jiuzhuan.domain.repository.domain.selecter.tree.DomainTreeCache;
import com.github.jiuzhuan.domain.repository.domain.selecter.tree.DomainTreeNode;
import com.github.jiuzhuan.domain.repository.domain.utils.ClassReflection;
import com.github.jiuzhuan.domain.repository.domain.utils.ReflectionUtil;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 聚合仓库
 * 1. 可以直接使用, 也可以通过DomainRepository代理使用, 以获取作用域的管理
 *
 * @author arrety
 * @date 2022/4/10 16:40
 */
@Component
//@Scope(WebApplicationContext.SCOPE_REQUEST)
@Scope("prototype")
public class DomainSelect<DomEntity> extends LambdaSelectDomBuilder implements DomainRepository{

    /**
     * 聚合实体类型
     */
    private Class<DomEntity> domClass;

    /**
     * 聚合实体查询结果(引用父类属性 {@link LambdaSelectDomBuilder.data})
     */
    public List<?> domList = null;

    /**
     * 聚合树(有缓存)
     */
    private DomainTree domainTree;

    /**
     * 聚合树约束
     */
    private Map<DomainTreeNode, HashSet<Object>> nodeConstraintMap = new HashMap<>();

    /**
     * 父对子的约束
     */
    private Map<DomainTreeNode, HashSet<Object>> parentNodeConstraintMap = new HashMap<>();

    /**
     * 关联查询构造器
     */
    @Autowired
    private LambdaSelectItemBuilder itemSelectBuilder;


    /**
     * 初始化查询
     */
    public Map<Class<?>, List<Object>> execute(Class<DomEntity> domClass) {

        // 由于泛型擦除机制 所以只能在执行时传入泛型...
        this.domClass = domClass;
        this.domainTree = DomainTreeCache.get(domClass);

        // 执行sql
        Map<Class<?>, List<Object>> classListMap = super.execute();

        // 设置约束
        setConstraint(classListMap);

        return classListMap;
    }

    private void setConstraint(Map<Class<?>, List<Object>> classListMap) {
        // 无论自身约束还是父节点的约束  两者只要有一个有值 就不应该再次赋值(因为最先出现的是最干净的, 第二次赋的值可能被其它实体过滤了)
        for (Map.Entry<Class<?>, List<Object>> classListEntry : classListMap.entrySet()) {

            DomainTreeNode entityNode = domainTree.getNodeByEntity(classListEntry.getKey());

            // 设置对自身的约束
            List<Object> selfJoinIds = ClassReflection.getFieldValue(classListEntry.getValue(), entityNode.entityJoinField);
            if (!parentNodeConstraintMap.containsKey(entityNode)) nodeConstraintMap.computeIfAbsent(entityNode, k -> new HashSet<>(selfJoinIds));

            // 设置对子节点的约束
            for (DomainTreeNode subNode : entityNode.subNodes) {
                setSubNodeConstraint(selfJoinIds, subNode);
            }

            // 设置对父节点的约束
            List<Object> joinIds = selfJoinIds;
            if (entityNode.parentNode != null) {
                if (entityNode.parentJoinField != null) {
                    // 本节点是中间表
                    List<Object> parentJoinIds = ClassReflection.getFieldValue(classListEntry.getValue(), entityNode.parentJoinField);
                    joinIds = parentJoinIds;
                    if (!parentNodeConstraintMap.containsKey(entityNode.parentNode)) nodeConstraintMap.computeIfAbsent(entityNode.parentNode, k -> new HashSet<>(parentJoinIds));
                } else {
                    if (!parentNodeConstraintMap.containsKey(entityNode.parentNode)) nodeConstraintMap.computeIfAbsent(entityNode.parentNode, k -> new HashSet<>(selfJoinIds));
                }
                // 设置同级节点约束
                for (DomainTreeNode subNode : entityNode.parentNode.subNodes) {
                    List<Object> finalJoinIds = joinIds;
                    setSubNodeConstraint(finalJoinIds, subNode);
                }
            }
        }
    }

    private void setSubNodeConstraint(List<Object> selfJoinIds, DomainTreeNode subNode) {
        if (subNode.parentJoinField == null) {
            if (!parentNodeConstraintMap.containsKey(subNode)) nodeConstraintMap.computeIfAbsent(subNode, k -> new HashSet<>(selfJoinIds));
        } else {
            // 设置中间表的父约束
            if (!nodeConstraintMap.containsKey(subNode)) parentNodeConstraintMap.computeIfAbsent(subNode, k -> new HashSet<>(selfJoinIds));
        }
    }

    /**
     * 获取结构化的结果 (投影)
     * data -> domList
     * 1. 未知节点的子节点虽然可以按parentJoinField分组, 但是自身无法分组(没有明确的值), 所以未知节点及以上无法结构化
     * 2. 即使是已知节点 由于关联查询新实体时 新实体可能缺少数据(未关联上) 所以 可能会有部分实体无法分配到组(groupBy joinField) 这部分数据必须舍弃
     * 3. 允许用户指定聚合类型 按指定的聚合类型结构化数据
     *
     * @return 结果
     * @param <T>
     */
    @SneakyThrows
    public <T> List<T> getDomains(Class<T> newDomClass) {

        // 读缓存
        if (domList != null && ReflectionUtil.getGenericType(domList).equals(newDomClass)) return (List<T>) domList;

        // 结构化
        domList = setDomain(newDomClass);

        return (List<T>) domList;
    }

    @SneakyThrows
    public <T> List<T> getAutoDomains() {
        // 结构化: 寻找已知子树的最小聚合
        DomainTreeNode minLevelNode = null;
        for (Class<?> clearClass : data.keySet()) {
            DomainTreeNode node = domainTree.getNodeByEntity(clearClass);
            if (minLevelNode == null) {
                minLevelNode = node;
                continue;
            }
            // 两个节点层级相等时  取其中某节点父节点所在聚合 (不必担心两个节点的父节点不一样 因为已知节点必然是相连的)
            // 但是有一种例外 就是其中一个节点是中间表(isLeaf) 此时取其中任一节点所在聚合
            int compareTo = node.parentDomClassLevel.compareTo(minLevelNode.parentDomClassLevel);
            if (compareTo == 0 && (!node.isLeaf || !minLevelNode.isLeaf)) {
                minLevelNode = node.parentNode != null ? node.parentNode : minLevelNode.parentNode;
            } else if (compareTo < 0){
                minLevelNode = node;
            }
        }
        if (minLevelNode == null) return null;
        return (List<T>) getDomains(minLevelNode.parentDomClass);
    }

    public List<Object> setDomain(Class<?> parentDomClass){
        // 解析聚合结构 并将数据分组
        List<Triple<Field, Class<?>, Map<Object, List<Object>>>> entityGroups = new ArrayList<>();
        for (Field field : parentDomClass.getDeclaredFields()) {
            Class<?> genericType = ReflectionUtil.getGenericType(field);
            if (genericType.isAnnotationPresent(Dom.class)) {
                DomainTree subDomainTree = DomainTreeCache.get(genericType);
                List<?> subDoms = setDomain(genericType);
                if (subDoms == null) continue;
                DomainTreeNode subNode = domainTree.getNodeByEntity(subDomainTree.rootNode.entityClass);
                Map<Object, List<Object>> group = subDoms.stream().collect(Collectors.groupingBy(d -> ClassReflection.getFieldValue(d, subNode.fieldName + "." + subNode.parentJoinField)));
                entityGroups.add(Triple.of(field, genericType, group));
            } else {
                DomainTreeNode entityNode = domainTree.getNodeByEntity(genericType);
                List<Object> subEntitys = data.get(genericType);
                if (subEntitys == null) continue;
                Map<Object, List<Object>> group = subEntitys.stream().collect(Collectors.groupingBy(d -> ClassReflection.getFieldValue(d, entityNode.entityJoinField)));
                entityGroups.add(Triple.of(field, genericType, group));
            }
        }
        // 创建聚合集合 并赋值
        List<Object> list = new ArrayList<>();
        int domSize = entityGroups.stream().map(Triple::getRight).map(Map::size).max(Comparator.comparingInt(a -> a)).orElse(0);
        Triple<Field, Class<?>, Map<Object, List<Object>>> standardFieldGroup = entityGroups.stream().filter(g -> Objects.equals(domSize, g.getRight().size())).findFirst().orElse(null);
        if (standardFieldGroup == null) return null;
        for (Map.Entry<Object, List<Object>> standard : standardFieldGroup.getRight().entrySet()) {
            Object domInstance = ClassReflection.newInstance(parentDomClass);
            for (Triple<Field, Class<?>, Map<Object, List<Object>>> entityGroup : entityGroups) {
                ClassReflection.setFieldValues(domInstance, entityGroup.getLeft(), entityGroup.getRight().get(standard.getKey()));
            }
            list.add(domInstance);
        }
        return list;
    }

    /**
     * 关联查询实体, 特点:
     * 1. 方法本身返回实体集合, 并且会注入到聚合
     * 2. 每次调用时才进行关联查询
     * 3. 自由组合不同实体, 比起JPA Entity Graph更灵活方便 (不需要定义各种概念)
     * @param entityClass
     * @return
     * @param <T>
     */
    public <T> List<T> getEntity(Class<T> entityClass) {

        // 读缓存(如果已经有了就不再查询)
        List<T> items = (List<T>) data.get(entityClass);
        if (items != null) return items;

        // 获取实体对应的字段, 如果实体在子聚合里则获取对应的子聚合
        DomainTreeNode entityTreeNode = domainTree.getNodeByEntity(entityClass);
        // 如果本层有约束(自身约束或父约束) 则直接执行sql查询
        HashSet<Object> constraints = nodeConstraintMap.get(entityTreeNode);
        if (constraints != null) return getItem(entityClass, entityTreeNode.entityJoinField, constraints);
        constraints = parentNodeConstraintMap.get(entityTreeNode);
        if (constraints != null) return getItem(entityClass, entityTreeNode.parentJoinField, constraints);

        // 向上下同时搜寻最近的有约束的层 并返回最短路径(包含当前要查询的类)
        if (nodeConstraintMap.isEmpty()) return null;
        List<DomainTreeNode> domainTreeNodePath = domainTree.recentKnownNode(entityTreeNode, nodeConstraintMap.keySet());

        // 将路径上所有实体查询都执行以getEntity() 以传播约束
        for (DomainTreeNode domainTreeNode : domainTreeNodePath) {
            // TODO: 2022.09.25 递归会重复获取最短路径 改为循环getItem?
            items = (List<T>) getEntity(domainTreeNode.entityClass);
        }
        return items;
    }

    /**
     * 非嵌套关联查询
     * @param entityClass
     * @return
     * @param <T>
     */
    private <T> List<T> getItem(Class<T> entityClass, String joinField, HashSet<Object> joinIds) {

        // 执行sql
        itemSelectBuilder.in(StringUtils.uncapitalize(entityClass.getSimpleName()), joinField, joinIds);
        List<T> items = itemSelectBuilder.selectList(entityClass);

        // 设置约束
        HashMap<Class<?>, List<Object>> current = new HashMap<>();
        current.put(entityClass, (List<Object>) items);
        setConstraint(current);
        data.putAll(current);

        // 清缓存 - 以便于重新结构化聚合
        domList = null;
        return items;
    }
}