package com.arrety.domainrepository.domainpersistence.common;

import com.arrety.domainrepository.domainpersistence.exception.ReflectionException;
import com.google.common.base.CaseFormat;

import java.util.Locale;

/**
 * @author arrety
 * @date 2022/2/8 17:37
 */
public final class PropertyNamer {
    private PropertyNamer() {
    }

    /**
     * 下划线 -> 驼峰
     * @return
     */
    public static String toCame(String org){
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, org);
    }

    /**
     * 驼峰转 -> 下划线
     * @return
     */
    public static String toUnderline(String org){
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, org);
    }

    public static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else {
            if (!name.startsWith("get") && !name.startsWith("set")) {
                throw new ReflectionException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
            }

            name = name.substring(3);
        }

        if (name.length() == 1 || name.length() > 1 && !Character.isUpperCase(name.charAt(1))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }
}