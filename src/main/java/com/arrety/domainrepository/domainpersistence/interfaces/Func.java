package com.arrety.domainrepository.domainpersistence.interfaces;


import com.arrety.domainrepository.domainpersistence.builder.SFunction;
import com.arrety.domainrepository.domainpersistence.common.ConditionDo;

import java.io.Serializable;

/**
 * 统一构造含条件表达式的sql函数方法
 * @author arrety
 * @date 2022/2/10 16:31
 */
public interface Func<Builder> extends Serializable {

    <T> Builder orderBy(SFunction<T, ?> column, boolean isAsc);

    default <T> Builder orderBy(boolean condition, SFunction<T, ?> column, boolean isAsc) {
        return ConditionDo.conditionDo(condition, () -> orderBy(column, isAsc));
    }

    Builder limit(Integer pageIndex, Integer pageSize);

    default Builder limit(boolean condition, Integer pageIndex, Integer pageSize){
        return ConditionDo.conditionDo(condition, () -> limit(pageIndex, pageSize));
    }
}
