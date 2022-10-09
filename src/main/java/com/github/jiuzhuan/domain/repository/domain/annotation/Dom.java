package com.github.jiuzhuan.domain.repository.domain.annotation;

import org.springframework.stereotype.Component;
import com.github.jiuzhuan.domain.repository.domain.selecter.tree.DomainTree;

import java.lang.annotation.*;

/**
 * 两个实体(表)无论是一对多还是多对多, 当一个领域聚合定义好之后, 必然是从其中一个实体的角度看另一个实体(其中一个为父节点, 一个为子节点), 所以将关系简化为一对一和一对多(仅List)
 * 如果两个实体是多对多关系, 那么可以单独定义两个不同聚合, 分别从不同角度出发
 * 后记: Spring Data JDBC 也是这么处理的~ https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.entity-persistence.types
 * 详细 {@link DomainTree}
 * @author arrety
 * @date 2022/3/29 20:24
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Inherited
public @interface Dom {
}
