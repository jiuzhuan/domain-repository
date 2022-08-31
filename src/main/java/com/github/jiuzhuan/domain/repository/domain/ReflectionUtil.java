package com.github.jiuzhuan.domain.repository.domain;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 获取list泛型类型
 * @author arrety
 * @date 2022/4/24 21:33
 */
public class ReflectionUtil {

    public static Class<?> getGenericType(Field field) {
        Class<?> type = field.getType();
        if (type.equals(List.class)) {
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            Type actualTypeArgument = genericType.getActualTypeArguments()[0];
            type = (Class<?>) actualTypeArgument;
        }
        return type;
    }

    public static Class<?> getGenericType(Object object) {
        if (object instanceof List) {
            return  ((List) object).get(0).getClass();
        }
        return object.getClass();
    }

    @SneakyThrows
    public static void mergeProperties(Object orig, Object target) {
        for (Field declaredField : target.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            if (declaredField.get(target) == null) {
                declaredField.set(target, declaredField.get(orig));
            }
        }
    }
}
