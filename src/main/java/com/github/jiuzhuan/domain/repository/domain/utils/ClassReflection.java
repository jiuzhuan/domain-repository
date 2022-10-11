package com.github.jiuzhuan.domain.repository.domain.utils;

import com.github.jiuzhuan.domain.repository.common.exception.ReflectionException;
import com.google.common.base.Joiner;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 反射工具类
 *
 * @author pengfwang@trip.com
 * @date 2022/8/16 20:10
 */
public class ClassReflection {

    /**
     * 针对list类型赋值的优化:
     * 1. 属性非list 值list  set values[0]
     * 2. 属性list 值list 直接set
     */
    public static <T> void setFieldValues(T obj, Field field, List<Object> values) {
        if (values == null) return;
        if (Objects.equals(field.getType(), List.class)) {
            ClassReflection.setFieldValue(obj, field, values);
        } else {
            ClassReflection.setFieldValue(obj, field, values.get(0));
        }
    }

    public static <T> T newInstance(Class<T> clazz, Object... paramters){
        try {
            Class<?>[] parameterTypes = new Class[paramters.length];
            for (int i = 0; i < paramters.length; i++) {
                parameterTypes[i] = paramters[i].getClass();
            }
            return clazz.getDeclaredConstructor(parameterTypes).newInstance(paramters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * 反射设置对象的属性值
     * 1. 嵌套结构用"."分隔
     * 2. Collection类型: 集合中所有项都会被设置
     * 3. Map类型: key=fieldName的会被设置
     *
     * @param obj 对象
     * @param fieldName 嵌套结构用"."分隔
     * @param value 值
     */
    @SneakyThrows
    public static void setFieldValue(@NonNull Object obj, @NonNull String fieldName, Object value) {
        String[] fieldNameChain = fieldName.split("\\.");
        Field targetField = null;
        Object targetFieldValue = obj;
        Object lastFieldValue = null;
        for (int i = 0; i < fieldNameChain.length; i++) {
            if (targetFieldValue == null) {
                throw new NullPointerException(fieldNameChain[i - 1]);
            }
            String[] subarray = ArrayUtils.subarray(fieldNameChain, i, fieldNameChain.length);
            String subFieldName = Joiner.on(".").join(subarray);
            if (targetFieldValue instanceof Collection){
                Iterator iterator = ((Collection) targetFieldValue).iterator();
                while (iterator.hasNext()){
                    setFieldValue(iterator.next(), subFieldName, value);
                }
                return;
            }
            if (targetFieldValue instanceof Map){
                ((Map) targetFieldValue).put(subFieldName, value);
                return;
            }
            targetField = getField(targetFieldValue, fieldNameChain[i]);
            lastFieldValue = targetFieldValue;
            targetFieldValue = getFieldValue(targetFieldValue, fieldNameChain[i]);
        }
        setFieldValue(lastFieldValue, targetField, value);
    }

    /**
     * 反射获取对象属性值
     * 1. 嵌套结构用"."分隔
     * 2. Collection类型: 返回Collection
     * 3. Map类型: 返回map里的value
     * @param obj
     * @param fieldName
     * @return
     */
    @SneakyThrows
    public static <T> T getFieldValue(@NonNull Object obj, @NonNull String fieldName) {
        String[] fieldNameChain = fieldName.split("\\.");
        Object targetFieldValue = obj;
        for (int i = 0; i < fieldNameChain.length; i++) {
            if (targetFieldValue == null) {
                throw new NullPointerException(fieldNameChain[i - 1]);
            }
            String[] subarray = ArrayUtils.subarray(fieldNameChain, i, fieldNameChain.length);
            String subFieldName = Joiner.on(".").join(subarray);
            if (targetFieldValue instanceof Collection){
                Iterator iterator = ((Collection) targetFieldValue).iterator();
                List<Object> targetFieldValues = new ArrayList<>();
                while (iterator.hasNext()){
                    Object fieldValue = getFieldValue(iterator.next(), subFieldName);
                    if (fieldValue != null) targetFieldValues.add(fieldValue);
                }
                return (T)targetFieldValues;
            }
            if (targetFieldValue instanceof Map){
                targetFieldValue = ((Map) targetFieldValue).get(subFieldName);
                return (T)targetFieldValue;
            }
            Field field = targetFieldValue.getClass().getDeclaredField(fieldNameChain[i]);
            targetFieldValue = getFieldValue(targetFieldValue, field);
        }
        return (T)targetFieldValue;
    }

    @SneakyThrows
    public static Object getFieldValue(@NonNull Object obj, @NonNull Field field) {
        field.setAccessible(true);
        return field.get(obj);
    }

    @SneakyThrows
    public static Field getField(@NonNull Object obj, @NonNull String fieldName) {
        return obj.getClass().getDeclaredField(fieldName);
    }


    @SneakyThrows
    public static void setFieldValue(@NonNull Object obj, @NonNull Field field, Object value) {
        field.setAccessible(true);
        field.set(obj, value);
    }
}
