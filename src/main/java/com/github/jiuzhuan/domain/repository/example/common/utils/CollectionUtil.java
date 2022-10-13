package com.github.jiuzhuan.domain.repository.example.common.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionUtil {

    @SafeVarargs
    public static <O> Collection<O> union(Collection<O>... collections) {
        Assert.notNull(collections, "can not be null");
        Assert.isTrue(collections.length > 1, "size must ge 1");
        Collection<O> unions = new ArrayList<>(collections[0]);
        for (int i = 1; i < collections.length; i++) {
            unions = CollectionUtils.union(unions, collections[i]);
        }
        return unions;
    }
}
