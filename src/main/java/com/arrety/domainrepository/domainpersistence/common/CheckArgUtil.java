package com.arrety.domainrepository.domainpersistence.common;

import com.google.common.collect.Lists;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * @author arrety
 * @date 2022/2/10 14:46
 */
public class CheckArgUtil {

    @SafeVarargs
    public static <T extends Collection> void checkArguments(T... ts) {
        Assert.notEmpty(ts);
        for (T t : ts) {
            Assert.notEmpty(t);
        }
    }

    @SafeVarargs
    public static <T> void checkArguments(T... ts) {
        Assert.notEmpty(ts);
        for (T t : ts) {
            Assert.notNull(t);
        }
    }

    public static void main(String[] args) {
        String a = null;
        checkArguments(a);
        checkArguments();
        checkArguments(Lists.newArrayList());
    }
}
