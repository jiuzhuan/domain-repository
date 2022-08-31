package com.github.jiuzhuan.domain.repository.builder.interfaces;


import com.github.jiuzhuan.domain.repository.builder.builder.SFunction;
import com.github.jiuzhuan.domain.repository.common.utils.ConditionDo;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

/**
 * 统一构造含条件表达式的where case方法
 * @author arrety
 * @date 2022/2/10 16:16
 */
public interface WhereCase<Builder> extends Serializable {

    Builder and(Consumer<Builder> consumer);
    default <T> Builder and(boolean condition, Consumer<Builder> consumer) {
        return ConditionDo.conditionDo(condition, () -> and(consumer));
    }

    Builder or(Consumer<Builder> consumer);
    default <T> Builder or(boolean condition, Consumer<Builder> consumer) {
        return ConditionDo.conditionDo(condition, () -> or(consumer));
    }

    <T> Builder eq(SFunction<T, ?> column, Object val);
    default <T> Builder eq(boolean condition, SFunction<T, ?> column, Object val) {
        return ConditionDo.conditionDo(condition, () -> eq(column, val));
    }

    <T> Builder ne(SFunction<T, ?> column, Object val);
    default <T> Builder ne(boolean condition, SFunction<T, ?> column, Object val) {
        return ConditionDo.conditionDo(condition, () -> ne(column, val));
    }


    <T> Builder gt(SFunction<T, ?> column, Object val);
    default <T> Builder gt(boolean condition, SFunction<T, ?> column, Object val) {
        return ConditionDo.conditionDo(condition, () -> gt(column, val));
    }

    <T> Builder ge(SFunction<T, ?> column, Object val);
    default <T> Builder ge(boolean condition, SFunction<T, ?> column, Object val) {
        return ConditionDo.conditionDo(condition, () -> ge(column, val));
    }


    <T> Builder lt(SFunction<T, ?> column, Object val);
    default <T> Builder lt(boolean condition, SFunction<T, ?> column, Object val) {
        return ConditionDo.conditionDo(condition, () -> lt(column, val));
    }


    <T> Builder le(SFunction<T, ?> column, Object val);
    default <T> Builder le(boolean condition, SFunction<T, ?> column, Object val) {
        return ConditionDo.conditionDo(condition, () -> le(column, val));
    }

    <T> Builder like(SFunction<T, ?> column, String val);
    default <T> Builder like(boolean condition, SFunction<T, ?> column, String val) {
        return ConditionDo.conditionDo(condition, () -> like(column, val));
    }


    <T> Builder in(SFunction<T, ?> column, List<?> val);
    default <T> Builder in(boolean condition, SFunction<T, ?> column, List<?> val) {
        return ConditionDo.conditionDo(condition, () -> in(column, val));
    }


}
