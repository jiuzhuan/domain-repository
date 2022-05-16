package com.arrety.domainrepository.domainpersistence.interfaces;

import com.arrety.domainrepository.domainpersistence.builder.SFunction;
import com.arrety.domainrepository.domainpersistence.common.ConditionDo;
import com.arrety.domainrepository.domainpersistence.common.LambdaColumnMap;
import com.arrety.domainrepository.domainpersistence.common.PropertyNamer;
import org.apache.commons.lang3.tuple.Triple;
import org.reflections.ReflectionUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author arrety
 * @date 2022/2/15 19:26
 */
public interface SetCase<Builder> extends Serializable {

    default <T> Builder set(SFunction<T, ?> column, Object val){
        Triple<String, String, Integer> columnCache = LambdaColumnMap.getColumnCache(column);
        return appendSet(columnCache.getMiddle(), val);
    }
    default <T> Builder set(boolean condition, SFunction<T, ?> column, Object val) {
        return ConditionDo.conditionDo(condition, () -> set(column, val));
    }

    // TODO: 2022/2/9 支持条件表达式, 支持跳过空值, 支持缓存
    default  <T> Builder set(T entity) {
        Assert.notNull(entity);
        Builder builder = null;
        Set<Field> allFields = ReflectionUtils.getAllFields(entity.getClass());
        for (Field field : allFields) {
            org.springframework.util.ReflectionUtils.makeAccessible(field);
            Object value = org.springframework.util.ReflectionUtils.getField(field, entity);
            if (value == null) {
                continue;
            }
            String fieldName = PropertyNamer.toUnderline(field.getName());
            builder = appendSet(fieldName, value);
        }
        return builder;
    }

    Builder appendSet(String fieldName, Object value);
}
