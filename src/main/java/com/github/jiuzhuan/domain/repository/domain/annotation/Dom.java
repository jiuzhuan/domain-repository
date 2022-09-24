package com.github.jiuzhuan.domain.repository.domain.annotation;

import org.springframework.stereotype.Component;
import com.github.jiuzhuan.domain.repository.domain.selecter.tree.DomainTree;

import java.lang.annotation.*;

/**
 * 两个实体(表)无论是一对多还是多对多, 当一个领域聚合定义好之后, 必然是从其中一个实体的角度看另一个实体(其中一个为父节点, 一个为子节点), 所以将关系简化为一对一和一对多
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
