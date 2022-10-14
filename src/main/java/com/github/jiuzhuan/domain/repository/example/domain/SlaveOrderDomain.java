package com.github.jiuzhuan.domain.repository.example.domain;

import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import com.github.jiuzhuan.domain.repository.domain.scope.ThreadScopeDomain;
import com.github.jiuzhuan.domain.repository.example.domain.aggregation.SlaveOrder;

/**
 * 子单聚合
 */
@Dom
public class SlaveOrderDomain extends ThreadScopeDomain<SlaveOrder> {

}
