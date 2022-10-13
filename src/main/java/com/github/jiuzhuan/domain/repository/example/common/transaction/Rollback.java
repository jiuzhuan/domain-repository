package com.github.jiuzhuan.domain.repository.example.common.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 无论成功与否 都会回滚
 * 用于希望真实调用接口 验证测试用例 并且不污染数据
 * @author pengfwang@trip.com
 * @date 2022/10/13 10:25
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Rollback {
}
