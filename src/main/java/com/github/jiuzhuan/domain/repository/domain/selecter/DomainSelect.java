package com.github.jiuzhuan.domain.repository.domain.selecter;

import com.github.jiuzhuan.domain.repository.builder.builder.LambdaSelectDomBuilder;
import com.github.jiuzhuan.domain.repository.builder.builder.LambdaSelectItemBuilder;
import com.github.jiuzhuan.domain.repository.domain.selecter.tree.DomainTree;
import com.github.jiuzhuan.domain.repository.domain.selecter.tree.DomainTreeCache;
import com.github.jiuzhuan.domain.repository.domain.selecter.tree.DomainTreeNode;
import com.github.jiuzhuan.domain.repository.domain.utils.ClassReflection;
import com.github.jiuzhuan.domain.repository.domain.utils.ReflectionUtil;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
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
     * 聚合实体查询结果
     */
    public List<DomEntity> domList = new ArrayList<>();

    /**
     * 聚合树
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

        for (Map.Entry<Class<?>, List<Object>> classListEntry : classListMap.entrySet()) {

            DomainTreeNode entityNode = domainTree.getNodeByEntity(classListEntry.getKey());

            // 设置对自身的约束
            List<Object> selfJoinIds = ClassReflection.getFieldValue(classListEntry.getValue(), entityNode.entityJoinField);
            nodeConstraintMap.computeIfAbsent(entityNode, k -> new HashSet<>());
            nodeConstraintMap.get(entityNode).addAll(new HashSet<>(selfJoinIds));

            // 设置对子节点的约束
            for (DomainTreeNode subNode : entityNode.subNodes) {
                if (subNode.parentJoinField == null) {
                    nodeConstraintMap.computeIfAbsent(subNode, k -> new HashSet<>());
                    nodeConstraintMap.get(subNode).addAll(new HashSet<>(selfJoinIds));
                } else {
                    parentNodeConstraintMap.computeIfAbsent(subNode, k -> new HashSet<>());
                    parentNodeConstraintMap.get(subNode).addAll(new HashSet<>(selfJoinIds));
                }
            }

            // 设置对父节点的约束
            if (entityNode.parentNode != null && entityNode.parentJoinField != null) {
                List<Object> parentJoinIds = ClassReflection.getFieldValue(classListEntry.getValue(), entityNode.parentJoinField);
                nodeConstraintMap.computeIfAbsent(entityNode.parentNode, k -> new HashSet<>());
                nodeConstraintMap.get(entityNode.parentNode).addAll(new HashSet<>(parentJoinIds));
            }
        }
    }

    /**
     * 初始化查询
     * @return 结果
     * @param <T>
     */
    @SneakyThrows
    public <T> List<T> get() {

        // 读缓存
        if (CollectionUtils.isNotEmpty(domList)) {
            return (List<T>)domList;
        }

        // todo 结构化: data -> domList 根节点/分组
        Object dom = domClass.getDeclaredConstructors()[0].newInstance();
        for (Field field : domClass.getDeclaredFields()) {
            Class<?> fieldType = ReflectionUtil.getGenericType(field);
            List<Object> fieldValues = data.get(fieldType);
            ClassReflection.setFieldValue(dom, field, fieldValues);
        }

        domList.add((DomEntity)dom);

        return (List<T>)domList;
    }

    /**
     * 关联查询
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

        // 向上下同时搜寻最近的有约束的层 并返回最短路径(包含当前要查询的类) todo 最短路径 初始化树的时候遍历记下所有节点到所有别的节点的距离?
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
        return items;
    }
}
