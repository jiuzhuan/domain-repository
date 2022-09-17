package com.github.jiuzhuan.domain.repository.domain.selecter;

import com.github.jiuzhuan.domain.repository.builder.builder.LambdaSelectDomBuilder;
import com.github.jiuzhuan.domain.repository.builder.builder.LambdaSelectItemBuilder;
import com.github.jiuzhuan.domain.repository.common.utils.PropertyNamer;
import com.github.jiuzhuan.domain.repository.common.exception.ReflectionException;
import com.github.jiuzhuan.domain.repository.domain.annotation.JoinOn;
import com.github.jiuzhuan.domain.repository.domain.utils.DomainFieldCache;
import com.github.jiuzhuan.domain.repository.domain.utils.DomainFieldUtil;
import com.github.jiuzhuan.domain.repository.domain.utils.ReflectionUtil;
import com.google.common.base.Joiner;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
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
     * 索引(解决一对多问题)
     */
    private Map<Object, Integer> domIdMap = new HashMap<>();
    /**
     * 约束(解决关联问题)
     */
    private HashSet<Object> joinIds = new HashSet<>();
    /**
     * 是否执行过自身约束查询
     */
    private boolean isInitSelect = false;
    /**
     * 是否已经向父聚合注入过
     */
    private boolean hasSetIntoParent = false;
    /**
     * 是否准备好初始化查询sql
     */
    private boolean isPrepareSelect = false;
    /**
     * 关联查询构造器
     */
    @Autowired
    private LambdaSelectItemBuilder itemSelectBuilder;
    /**
     * 表对应的子聚合(解决嵌套聚合问题)
     * key: 本聚合的所有表名(包含嵌套的不包含子聚合本身, 用以通过表名获取value)
     * value:
     *      class: 表名对应的类
     *      String: joinId
     *      DomainSelect: 表所在的子聚合
     */
    private Map<String, Triple<Class, String, DomainSelect>> tableSelectDomainMap = new HashMap<>();
    /**
     * 子聚合集合(解决嵌套聚合问题)
     */
    private List<DomainSelect> itemDomainSelect = new ArrayList<>();
    /**
     * 父聚合(解决自下向上联动问题)
     */
    private DomainSelect parentDomainSelect = null;
    /**
     * 父聚合关联类
     */
    private Class<?> parentJoinClass = null;
    /**
     * 表对应的子聚合对应的属性
     * key: 表名
     * value: 如果该表是本聚合内的-则value为表对应的属性, 如果该表是子聚合内的, 则value为子聚合
     */
    private Map<String, Field> tableFieldMap = new HashMap<>();

    public DomainSelect() {
    }
    public DomainSelect(Class<DomEntity> domClass, Class<?> parentJoinClass) {
        this.domClass = domClass;
        this.parentJoinClass = parentJoinClass;
    }

    /**
     * 初始化查询
     * @param domClass 领域聚合类
     * @return 结果
     * @param <T>
     */
    @Override
    @SneakyThrows
    public <T> List<T> selectList(Class<T> domClass) {
        //读缓存
        if (CollectionUtils.isNotEmpty(domList)) {
            return (List<T>)domList;
        }

        //重写方法的话... 由于父方法方法是泛型方法, 而本类是泛型类, 所以必须强转
        this.domClass = (Class<DomEntity>) domClass;

        //领域初始化 todo 这一步应该可以提前到应用启动阶段 并且应该只执行一次
        initDomain(null);

        //聚合查询, 通过结果集映射拓展方法传播约束和索引
        domList = (List<DomEntity>)super.selectList(domClass);

        // 子聚合查询语句准备(解决间接约束问题)
        setIndirectConstraint();
        return (List<T>)domList;
    }

    /**
     * 初始化表和子聚合的映射关系, 初始化父聚合
     * @param parentDomainSelect
     */
    private void initDomain(DomainSelect parentDomainSelect) {
        if (parentDomainSelect != null) {
            this.parentDomainSelect = parentDomainSelect;
        }
        DomainFieldCache.initDomain(domClass, itemDomainSelect, tableSelectDomainMap, Triple.of(null, null, this),
                tableFieldMap, null, null);
        for (DomainSelect<?> itemDomain : itemDomainSelect) {
            itemDomain.initDomain(this);
        }
    }

    /**
     * 结果集映射拓展方法传播约束和索引
     * @param tableAndCol
     * @param i
     * @param value
     */
    @Override
    protected void mapToObject(String[] tableAndCol, Integer i, Object value) {
        // 固定自身索引和约束 索引:一对多 约束:where条件
        selfConstraint(tableAndCol, i, value);
        // 向上固定约束
        upConstraint(tableAndCol, null, value);
        // 向下固定约束
        downConstraint(tableAndCol, null, value);
    }

    /**
     * 自身约束
     * @param tableAndCol
     * @param i
     * @param value
     */
    private void selfConstraint(String[] tableAndCol, Integer i, Object value) {
        if (tableFieldMap.get(tableAndCol[0]) == null) {
            return;
        }
        String map = Joiner.on('.').join(tableAndCol);
        String joinId = tableFieldMap.get(tableAndCol[0]).getAnnotation(JoinOn.class).joinId();
        if (!joinId.contains(".")) {
            joinId = tableFieldMap.get(tableAndCol[0]).getName() + "." + joinId;
        }
        //  本聚合: 命中join字段时产生一个约束
        if (ObjectUtils.equals(joinId, map)) {
            isInitSelect = true;
            // 索引
            if (i != null) domIdMap.put(value, i);
            // 约束
            joinIds.add(value);
        }
    }

    /**
     * 向下传播约束: 遍历所有子聚合, 命中join字段时产生一个约束
     * @param tableAndCol
     * @param i
     * @param value
     */
    private void downConstraint(String[] tableAndCol, Integer i, Object value) {
        for (DomainSelect itemDomain : itemDomainSelect) {
            itemDomain.selfConstraint(tableAndCol, i, value);
            itemDomain.downConstraint(tableAndCol, i, value);
        }
    }

    /**
     *  向上传播约束: 遍历所有父聚合, 命中join字段时产生一个约束
     * @param tableAndCol
     * @param i
     * @param value
     */
    private void upConstraint(String[] tableAndCol, Integer i, Object value) {
        if (parentDomainSelect != null) {
            parentDomainSelect.selfConstraint(tableAndCol, i, value);
            parentDomainSelect.upConstraint(tableAndCol, i, value);
        }
    }

    /**
     * 解决向上/向下间接约束
     * 子约束/子聚合查询语句初始化 + 父约束/父聚合查询语句初始化
     * @throws NoSuchFieldException
     */
    private void setIndirectConstraint() throws NoSuchFieldException {
        List<Triple<Class, String, DomainSelect>> itemSelectDomains = tableSelectDomainMap.values().stream().distinct().filter((item) -> item.getLeft() != null).collect(Collectors.toList());
        for (Triple<Class, String, DomainSelect> itemSelectDomain : itemSelectDomains) {
            // 父对子的约束
            String joinId = itemSelectDomain.getMiddle();
            String[] split = joinId.split("\\.");
            Field joinTable = itemSelectDomain.getLeft().getDeclaredField(split[0]);
            // 仅构建父子关联表的查询语句
            itemSelectDomain.getRight().clear();
            itemSelectDomain.getRight().selectAll().from(joinTable.getType()).where();
            if (CollectionUtils.isNotEmpty(joinIds)) {
                itemSelectDomain.getRight().isPrepareSelect = true;
                itemSelectDomain.getRight().appendIn(PropertyNamer.toUnderline(split[1]), new ArrayList<>(joinIds));
            }
            // 子自身的约束
            if (CollectionUtils.isNotEmpty(itemSelectDomain.getRight().joinIds)) {
                itemSelectDomain.getRight().isPrepareSelect = true;
                Field item = (Field) itemSelectDomain.getRight().tableFieldMap.get(split[0]);
                JoinOn joinOn = item.getAnnotation(JoinOn.class);
                itemSelectDomain.getRight().appendIn(PropertyNamer.toUnderline(joinOn.joinId()), new ArrayList<>(itemSelectDomain.getRight().joinIds));
            }
        }
        if (parentDomainSelect != null){
            parentDomainSelect.setIndirectConstraint();
        }
    }

    /**
     * 关联查询
     * @param entityClass
     * @return
     * @param <T>
     */
    public <T> List<T> getEntity(Class<T> entityClass) {
        try {
            //读缓存(如果已经有了就不再查询)
            List<T> items = DomainFieldUtil.get(domList, entityClass);
            if (CollectionUtils.isNotEmpty(items)) {
                return items;
            }
            // 获取实体对应的字段, 如果实体在子聚合里则获取对应的子聚合
            Field field = tableFieldMap.get(StringUtils.uncapitalize(entityClass.getSimpleName()));
            JoinOn joinOn = field.getAnnotation(JoinOn.class);
            Triple<Class, String, DomainSelect> itemSelectDomainPair = tableSelectDomainMap.get(StringUtils.uncapitalize(entityClass.getSimpleName()));
            if (itemSelectDomainPair.getLeft() == null) {
                // 本聚合初始化查询
                initSelect();
                // 非嵌套聚合
                items = getItem(entityClass, joinOn);
                // 注入
                setInField(entityClass, field, joinOn.joinId(), items);
            } else {
                //嵌套聚合
                //父聚合初始化select时已经给所有子聚合设置了条件, 这里只需要执行子聚合的select即可, 但是某些情况下可能中间表尚未查询, 所以这里要调一次聚合查询(配合[读缓存]可以避免重复调用)
                initSelect();
                //配合[读缓存]可以避免重复调用
                items = itemSelectDomainPair.getRight().getEntity(entityClass);
                //注入 第一次注入后父类list里引用了子类的list  所以后续无需再次注入到父类 但第一次要先执行中间查询 所以前后都会有一次 initSelect()
//                setInDomain(ReflectionUtil.getGenericType(field), field, joinOn.joinId(), itemSelectDomainPair.getRight().domList);
                initSelect();
            }
            return items;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReflectionException("entityName error");
        }
    }

    /**
     * 本聚合查询初始化, 用于解决自下向上的间接关联
     *
     * @return
     */
    @SneakyThrows
    private List<DomEntity> initSelect() {
        //如果执行了一遍初始化查询还是没有初始化自身约束, 那么就是间接关联, 需要调用子聚合的初始化查询, 以先执行间接约束
        if (isPrepareSelect) {
            selectList(domClass);
        }
        for (DomainSelect domainSelect : itemDomainSelect) {
            if (domainSelect.isInitSelect || hasSetIntoParent) {
                continue;
            }
            hasSetIntoParent = true;
            domainSelect.initSelect();
            Class joinClass = domainSelect.parentJoinClass;
            Field field = tableFieldMap.get(StringUtils.uncapitalize(joinClass.getSimpleName()));
            JoinOn joinOn = field.getAnnotation(JoinOn.class);
            //配合[读缓存]可以避免重复调用
            List items = domainSelect.getEntity(joinClass);
            setInDomain(ReflectionUtil.getGenericType(field), field, joinOn.joinId(), domainSelect.domList);
        }
        return domList;
    }

    /**
     * 非嵌套关联查询
     * @param entityClass
     * @param joinOn
     * @return
     * @param <T>
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private <T> List<T> getItem(Class<T> entityClass, JoinOn joinOn) throws NoSuchFieldException, IllegalAccessException {
        if (joinOn != null) {
            itemSelectBuilder.in(StringUtils.uncapitalize(entityClass.getSimpleName()), joinOn.joinId(), new ArrayList<>(joinIds));
        }
        List<T> selectList = itemSelectBuilder.selectList(entityClass);
        return selectList;
    }

    /**
     * 子聚合关联查询结果注入父聚合
     * @param entityClass
     * @param entityField
     * @param joinId
     * @param selectList
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @SneakyThrows
    private void setInDomain(Class<?> entityClass, Field entityField, String joinId, List<?> selectList) throws NoSuchFieldException, IllegalAccessException {
        if (domList.isEmpty() || domList.isEmpty()){
            return;
        }
        //注入到聚合
        Field joinEntity = entityClass.getDeclaredField(joinId.split("\\.")[0]);
        joinEntity.setAccessible(true);
        Field joinField = joinEntity.getType().getDeclaredField(joinId.split("\\.")[1]);
        joinField.setAccessible(true);
        for (Object entityItem : selectList) {
            Object value = joinField.get(joinEntity.get(entityItem));
            DomEntity dom = domList.get(domIdMap.get(value));
            DomainFieldUtil.setFieldValue(dom, entityItem, entityField);
        }
    }

    /**
     * 关联查询结果注入到结果集
     * @param entityClass
     * @param entityField
     * @param joinId
     * @param selectList
     * @param <T>
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private <T> void setInField(Class<T> entityClass, Field entityField, String joinId, List<T> selectList) throws NoSuchFieldException, IllegalAccessException {
        if (domList.isEmpty() || domList.isEmpty()){
            return;
        }
        //注入到聚合
        Field joinField = entityClass.getDeclaredField(joinId);
        joinField.setAccessible(true);
        for (Object entityItem : selectList) {
            Object value = joinField.get(entityItem);
            DomEntity dom = domList.get(domIdMap.get(value));
            DomainFieldUtil.setFieldValue(dom, entityItem, entityField);
        }
    }

}
