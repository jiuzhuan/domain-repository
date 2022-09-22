package com.github.jiuzhuan.domain.repository.domain.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 两个实体(表)无论是一对多还是多对多, 当一个领域聚合定义好之后, 必然是从其中一个实体的角度看另一个实体(其中一个为父节点, 一个为子节点), 所以将关系简化为一对一和一对多
 * @author arrety
 * @date 2022/3/29 20:24
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
//无法继承component
@Inherited
//SPRING的request作用域本质也是使用代理类+threadlocal实现， 注入代理类，并在每次调用代理类方法时，创建一个新被代理类执行方法, 必须再基于web的spring容器中才有效
//@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public @interface Dom {
}
