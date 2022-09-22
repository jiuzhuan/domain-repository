package com.github.jiuzhuan.domain.repository.domain.selecter;

import com.github.jiuzhuan.domain.repository.builder.builder.LambdaSelectDomBuilder;
import com.github.jiuzhuan.domain.repository.builder.builder.LambdaSelectItemBuilder;
import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
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
     * 聚合实体查询结果(引用父类属性 {@link LambdaSelectDomBuilder.data})
     */
    public List<DomEntity> domList = null;

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

        for (Map.Entry<Class<?>, List<Object>> classListEntry : classListMap.entrySet()) {

            DomainTreeNode entityNode = domainTree.getNodeByEntity(classListEntry.getKey());

            // 设置对自身的约束
            List<Object> selfJoinIds = ClassReflection.getFieldValue(classListEntry.getValue(), entityNode.entityJoinField);
            nodeConstraintMap.computeIfAbsent(entityNode, k -> new HashSet<>(selfJoinIds));

            // 设置对子节点的约束
            for (DomainTreeNode subNode : entityNode.subNodes) {
                if (subNode.parentJoinField == null) {
                    nodeConstraintMap.computeIfAbsent(subNode, k -> new HashSet<>(selfJoinIds));
                } else {
                    parentNodeConstraintMap.computeIfAbsent(subNode, k -> new HashSet<>(selfJoinIds));
                }
            }

            // 设置对父节点的约束
            if (entityNode.parentNode != null) {
                if (entityNode.parentJoinField != null) {
                    List<Object> parentJoinIds = ClassReflection.getFieldValue(classListEntry.getValue(), entityNode.parentJoinField);
                    nodeConstraintMap.computeIfAbsent(entityNode.parentNode, k -> new HashSet<>(parentJoinIds));
                } else {
                    nodeConstraintMap.computeIfAbsent(entityNode.parentNode, k -> new HashSet<>(selfJoinIds));
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
        domList = (List<DomEntity>) struct();

        return (List<T>) domList;
    }

    private List<?> struct() {
        // 寻找已知子树的顶节点
        Optional<Class<?>> nodeClassOpt = data.keySet().stream().findFirst();
        if (nodeClassOpt.isEmpty()) return null;
        DomainTreeNode topNode = findClearTopNode(domainTree.getNodeByEntity(nodeClassOpt.get()));
        List<?> list = setDomain(topNode.parentDomClass, topNode);
        return list;
    }

    private DomainTreeNode findClearTopNode(DomainTreeNode node) {
        DomainTreeNode parentNode = node.parentNode;
        if (parentNode != null && data.get(parentNode.entityClass) != null) {
            return findClearTopNode(parentNode);
        } else {
            return node;
        }
    }

    public <T> List<T> setDomain(Class<T> domClass, DomainTreeNode topNode){
        // 非聚合的实体
        Field topEntityField = domClass.getDeclaredFields()[0];
        List<Pair<Field, Class<?>>> domClassEntityFields = new ArrayList<>();
        List<Pair<Field, Class<?>>> domClassDomainFields = new ArrayList<>();
        for (Field field : domClass.getDeclaredFields()) {
            Class<?> genericType = ReflectionUtil.getGenericType(field);
            if (Objects.equals(genericType, topNode.entityClass)) {
                topEntityField = field;
            } else if (genericType.isAnnotationPresent(Dom.class)) {
                domClassDomainFields.add(Pair.of(field, genericType));
            } else {
                domClassEntityFields.add(Pair.of(field, genericType));
            }
        }
        List<T> list = new ArrayList<>();
        List<Object> topValues = data.get(topNode.entityClass);
        if (topValues == null) return null;
        for (Object topValue : topValues) {
            T domInstance = ClassReflection.newInstance(domClass);
            ClassReflection.setFieldValues(domInstance, topEntityField, Arrays.asList(topValue));
            Object id = ClassReflection.getFieldValue(topValue, topNode.entityJoinField);
            for (Pair<Field, Class<?>> domClassEntityField : domClassEntityFields) {
                DomainTreeNode entityNode = domainTree.getNodeByEntity(domClassEntityField.getRight());
                List<Object> subEntitys = data.get(domClassEntityField.getRight());
                if (subEntitys == null) continue;
                Map<Object, List<Object>> group = subEntitys.stream().collect(Collectors.groupingBy(d -> ClassReflection.getFieldValue(d, entityNode.entityJoinField)));
                ClassReflection.setFieldValues(domInstance, domClassEntityField.getLeft(), group.get(id));
            }
            for (Pair<Field, Class<?>> domClassDomainField : domClassDomainFields) {
                DomainTree subDomainTree = DomainTreeCache.get(domClassDomainField.getRight());
                List<?> subDoms = setDomain(domClassDomainField.getRight(), subDomainTree.rootNode);
                if (subDoms == null) continue;
                DomainTreeNode subNode = domainTree.getNodeByEntity(subDomainTree.rootNode.entityClass);
                Map<Object, List<Object>> subGroup = subDoms.stream().collect(Collectors.groupingBy(d -> ClassReflection.getFieldValue(d, subNode.fieldName + "." + subNode.parentJoinField)));
                ClassReflection.setFieldValues(domInstance, domClassDomainField.getLeft(), subGroup.get(id));
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

        // 清缓存
        domList = null;
        return items;
    }
}