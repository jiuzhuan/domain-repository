package com.github.jiuzhuan.domain.repository.domain.annotation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 约束, 等同于 sql 中的 join on 字段
 * // TODO: 2022/9/30 一个表通过两个不同字段关联两个不同表
 * @author arrety
 * @date 2022/3/29 20:24
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface JoinOn {

    String joinField();

    Class<?> joinEntity();
}
