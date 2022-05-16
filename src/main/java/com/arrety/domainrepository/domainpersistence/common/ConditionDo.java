package com.arrety.domainrepository.domainpersistence.common;

/**
 * @author arrety
 * @date 2022/2/10 14:41
 */
public class ConditionDo {

    @FunctionalInterface
    public interface DoFunc<E> {
        E doo();
    }

    public static <E> E conditionDo(boolean condition, DoFunc<E> doFunc) {
        if (condition) {
            return doFunc.doo();
        }
        return null;
    }
}
