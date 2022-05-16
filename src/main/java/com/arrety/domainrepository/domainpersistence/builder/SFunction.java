package com.arrety.domainrepository.domainpersistence.builder;


import com.arrety.domainrepository.domainpersistence.common.PropertyNamer;
import com.google.common.base.CaseFormat;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @author arrety
 * @date 2022/1/29 15:33
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {

    /**
     * 获取SerializedLambda实例
     * 凡是继承了Serializable的函数式接口的实例会存在一个名字为writeReplace的方法，可以获取一个属于它的SerializedLambda实例，并且通过它获取到方法名、类名、参数、方法签名等
     *
     * @return SerializedLambda
     */
    default SerializedLambda getSerializedLambda() {
        try {
            Method method = this.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            return (SerializedLambda) method.invoke(this);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 通过方法引用获取方法名, substring截取"get", 获取属性名
     *
     * @return 方法对应表的字段名
     */
    default String lambdaFiled() {
        String filedName = PropertyNamer.methodToProperty(this.getSerializedLambda().getImplMethodName());
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, filedName);
    }

    /**
     * 通过方法引用获取类的全限定名, substring截取, 获取类名
     *
     * @return 实体类对应的表名
     */
    default String lambdaClass() {
        String className = this.getSerializedLambda().getImplClass();
        className = className.substring(className.lastIndexOf('/') + 1);
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
    }

}
