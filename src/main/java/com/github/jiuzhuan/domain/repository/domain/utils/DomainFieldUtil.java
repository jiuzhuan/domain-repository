package com.github.jiuzhuan.domain.repository.domain.utils;

import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import com.github.jiuzhuan.domain.repository.domain.selecter.tree.DomainTree;
import com.github.jiuzhuan.domain.repository.domain.selecter.tree.DomainTreeNode;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 领域对象表列字段赋值
 * @author arrety
 * @date 2022/4/24 19:07
 */
public class DomainFieldUtil {

    public static void set(Object target, String tableName, String columnName, Object value) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Field> fieldChain = DomainFieldCache.getFields(target.getClass().getSimpleName(), tableName, columnName);
        set(target, value, fieldChain);
    }

    private static void set(Object target, Object value, List<Field> fieldChain) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object object = target;
        for (int i = 0; i < fieldChain.size(); i++) {
            Field field = fieldChain.get(i);
            if (i == fieldChain.size() - 1){
                setField(object, value, field);
                break;
            }
            Object fieldValue = field.get(object);
            if (field.getType() == List.class){
                if (fieldValue == null) {
                    fieldValue = new ArrayList<>();
                    field.set(object, fieldValue);
                }
                // TODO: 2022/5/11 有必要?
//                field.set(target, Lists.newArrayList(value));
                Class<?> genericType = ReflectionUtil.getGenericType(field);
                object = genericType.getDeclaredConstructors()[0].newInstance();
                ((List)fieldValue).add(object);
            } else {
                if (fieldValue == null) {
                    fieldValue = field.getType().getDeclaredConstructors()[0].newInstance();
                    field.set(object, fieldValue);
                }
                object = fieldValue;
            }
        }
    }

    public static <T, DomEntity> List<T> get(List<DomEntity> domList, Class<T> entityClass) throws IllegalAccessException {
        if (CollectionUtils.isEmpty(domList)) {
            return new ArrayList<>();
        }
        Class<?> domClass = ReflectionUtil.getGenericType(domList);
        if (ObjectUtils.equals(domClass, entityClass)){
            return (List<T>)domList;
        }
        List<Field> fieldChain = DomainFieldCache.getFields(domClass.getSimpleName(), StringUtils.uncapitalize(entityClass.getSimpleName()));
        List<Object> result = new ArrayList<>();
        for (DomEntity domEntity : domList) {
            Object item = domEntity;
            for (int i = 0; i < fieldChain.size(); i++) {
                if (item == null){
                    continue;
                }
                Field field = fieldChain.get(i);
                if (i == fieldChain.size() - 1) {
                    List itemValue = getFieldValue4List(item, field);
                    result.addAll(itemValue);
                    break;
                }
                item = field.get(item);
            }
        }
        return (List<T>)result;
    }

    @SneakyThrows
    public static List getFieldValue4List(Object object, Field field){
        List list = new ArrayList();
        if (object instanceof List){
            for (Object item : ((List<?>) object)) {
                Object itemFieldValue = field.get(item);
                if (itemFieldValue != null) {
                    list.add(itemFieldValue);
                }
            }
        } else {
            list.add(field.get(object));
        }
        list.remove(null);
        return list;
    }

    @SneakyThrows
    public static void setFieldValue(Object target, Object value, Field field) {
        List<Field> fieldChain = DomainFieldCache.getFields(target.getClass().getSimpleName(), field.getName());
        set(target, value, fieldChain);
    }

    @SneakyThrows
    public static void setField(Object target, Object value, Field field) {
        Object targetValue = field.get(target);
        if (ObjectUtils.equals(field.getType(), List.class)) {
            List list = (List) targetValue;
            if (list != null) {
                list.add(value);
            } else {
                field.set(target, Lists.newArrayList(value));
            }
        } else {
            if (targetValue != null) {
                ReflectionUtil.mergeProperties(targetValue, value);
            }
            field.set(target, value);
            // TODO: 2022/4/24 require one but found two
        }
    }
}
