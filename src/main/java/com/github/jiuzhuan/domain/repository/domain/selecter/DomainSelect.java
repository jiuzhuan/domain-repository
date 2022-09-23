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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 聚合关联
 *
 * @author arrety
 * @date 2022/4/10 16:40
 */
@Component
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
     * @param tClass
     * @return
     */
    public Map<Class<?>, List<Object>> execute(Class tClass) {

        // 由于泛型擦除机制 所以只能在执行时传入泛型...
        this.domClass = tClass;
        this.domainTree = DomainTreeCache.get(domClass);

        // 执行sql
        Map<Class<?>, List<Object>> classListMap = super.execute();

        // 设置约束
        setConstraint(classListMap);

        return classListMap;
    }

    private void setConstraint(Map<Class<?>, List<Object>> classListMap) {
        // 无论自身约束还是父节点的约束  两者只要有一个有值 就不应该再次赋值(因为最先出现的是最干净的 第二次赋的值可能被其它实体过滤了)
        for (Map.Entry<Class<?>, List<Object>> classListEntry : classListMap.entrySet()) {

            DomainTreeNode entityNode = domainTree.getNodeByEntity(classListEntry.getKey());

            // 设置对自身的约束
            List<Object> selfJoinIds = ClassReflection.getFieldValue(classListEntry.getValue(), entityNode.entityJoinField);
            if (!parentNodeConstraintMap.containsKey(entityNode)) nodeConstraintMap.computeIfAbsent(entityNode, k -> new HashSet<>(selfJoinIds));

            // 设置对子节点的约束
            for (DomainTreeNode subNode : entityNode.subNodes) {
                if (subNode.parentJoinField == null) {
                    if (!parentNodeConstraintMap.containsKey(subNode)) nodeConstraintMap.computeIfAbsent(subNode, k -> new HashSet<>(selfJoinIds));
                } else {
                    // 设置中间表的父约束
                    if (!nodeConstraintMap.containsKey(subNode)) parentNodeConstraintMap.computeIfAbsent(subNode, k -> new HashSet<>(selfJoinIds));
                }
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
                    if (entityNode.parentJoinField != null) {
                        if (!parentNodeConstraintMap.containsKey(subNode)) nodeConstraintMap.computeIfAbsent(subNode, k -> new HashSet<>(finalJoinIds));
                    } else {
                        if (!nodeConstraintMap.containsKey(subNode)) parentNodeConstraintMap.computeIfAbsent(subNode, k -> new HashSet<>(finalJoinIds));
                    }
                }
            }
        }
    }

    /**
     * 获取结构化的结果
     * data -> domList, 未知节点的子节点虽然可以按parentJoinField分组, 但是自身无法分组(没有明确的值), 所以未知节点及以上无法结构化
     *
     * @return 结果
     * @param <T>
     */
    @SneakyThrows
    public <T> List<T> get() {

        // 读缓存
        if (domList != null) return (List<T>) domList;

        //  结构化
        // 寻找已知子树的最小聚合层
        Integer minLevel = Integer.MAX_VALUE;
        DomainTreeNode minLevelNode = null;
        for (Class<?> clearClass : data.keySet()) {
            DomainTreeNode node = domainTree.getNodeByEntity(clearClass);
            if (node.parentDomClassLevel.compareTo(minLevel) < 0) {
                minLevelNode = node;
                minLevel = node.parentDomClassLevel;
            }
        }
        // todo 已知子实体 关联查询父实体时 父实体可能缺少数据  所以 应当按data里最大分组创建聚合并赋值
        domList = setDomain(minLevelNode.parentDomClass, minLevelNode);

        return (List<T>) domList;
    }

    public List<Object> setDomain(Class<?> parentDomClass, DomainTreeNode topNode){
        // 解析聚合的属性结构 todo 缓存起来
        Field topEntityField = parentDomClass.getDeclaredFields()[0];
        List<Pair<Field, Class<?>>> domClassEntityFields = new ArrayList<>();
        List<Pair<Field, Class<?>>> domClassDomainFields = new ArrayList<>();
        for (Field field : parentDomClass.getDeclaredFields()) {
            Class<?> genericType = ReflectionUtil.getGenericType(field);
            if (Objects.equals(genericType, topNode.entityClass)) {
                topEntityField = field;
            } else if (genericType.isAnnotationPresent(Dom.class)) {
                domClassDomainFields.add(Pair.of(field, genericType));
            } else {
                domClassEntityFields.add(Pair.of(field, genericType));
            }
        }
        // 聚合的顶节点分组
        List<Object> list = new ArrayList<>();
        List<Object> topValues = data.get(topNode.entityClass);
        if (topValues == null) return null;
        Map<Object, List<Object>> topValuesMap = topValues.stream().collect(Collectors.groupingBy(d -> ClassReflection.getFieldValue(d, topNode.entityJoinField)));
        // 创建聚合集合 并赋值
        for (Map.Entry<Object, List<Object>> topValue : topValuesMap.entrySet()) {
            Object domInstance = ClassReflection.newInstance(parentDomClass);
            ClassReflection.setFieldValues(domInstance, topEntityField, Lists.newArrayList(topValue.getValue()));
            // 聚合普通属性赋值(分组)
            for (Pair<Field, Class<?>> domClassEntityField : domClassEntityFields) {
                DomainTreeNode entityNode = domainTree.getNodeByEntity(domClassEntityField.getRight());
                List<Object> subEntitys = data.get(domClassEntityField.getRight());
                if (subEntitys == null) continue;
                Map<Object, List<Object>> group = subEntitys.stream().collect(Collectors.groupingBy(d -> ClassReflection.getFieldValue(d, entityNode.entityJoinField)));
                ClassReflection.setFieldValues(domInstance, domClassEntityField.getLeft(), group.get(topValue.getKey()));
            }
            // 聚合内子聚合递归处理(分组)
            for (Pair<Field, Class<?>> domClassDomainField : domClassDomainFields) {
                DomainTree subDomainTree = DomainTreeCache.get(domClassDomainField.getRight());
                List<?> subDoms = setDomain(domClassDomainField.getRight(), subDomainTree.rootNode);
                if (subDoms == null) continue;
                DomainTreeNode subNode = domainTree.getNodeByEntity(subDomainTree.rootNode.entityClass);
                Map<Object, List<Object>> subGroup = subDoms.stream().collect(Collectors.groupingBy(d -> ClassReflection.getFieldValue(d, subNode.fieldName + "." + subNode.parentJoinField)));
                ClassReflection.setFieldValues(domInstance, domClassDomainField.getLeft(), subGroup.get(topValue.getKey()));
            }
            list.add(domInstance);
        }

        return list;
    }

    /**
     * 关联查询实体
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
        List<DomainTreeNode> domainTreeNodePath = domainTree.recentKnownNode(entityTreeNode, nodeConstraintMap.keySet());

        // 将路径上所有实体查询都执行以getEntity() 以传播约束
        for (DomainTreeNode domainTreeNode : domainTreeNodePath) {
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