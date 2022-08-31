package com.github.jiuzhuan.domain.repository.builder.interfaces;

import com.github.jiuzhuan.domain.repository.builder.builder.SFunction;
import com.github.jiuzhuan.domain.repository.common.utils.ConditionDo;

import java.io.Serializable;

/**
 * 统一构造含条件表达式的联表方法
 * @author arrety
 * @date 2022/2/10 16:30
 */
public interface JoinCase<E> extends Serializable {

    <T> E leftJoin(Class<T> clazz);

    default <T> E leftJoin(boolean condition, Class<T> clazz) {
        return ConditionDo.conditionDo(condition, () -> leftJoin(clazz));
    }

    <T, F> E on(SFunction<T, ?> column1, SFunction<F, ?> column2);

    default <T, F> E on(boolean condition, SFunction<T, ?> column1, SFunction<F, ?> column2) {
        return ConditionDo.conditionDo(condition, () -> on(column1, column2));
    }
}
