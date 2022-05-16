package com.arrety.domainrepository.domainpersistence.common;

import com.arrety.domainrepository.domainpersistence.builder.SFunction;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 方法引用和字段名映射缓存
 * @author arrety
 * @date 2022/2/11 20:41
 */
public class LambdaColumnMap {

    /**
     * 方法引用对应的表名, 字段名, jdbcType
     */
    private static ConcurrentHashMap<SFunction<?, ?>, Triple<String, String, Integer>> cache = new ConcurrentHashMap<>();

    /**
     * 获取方法引用对应的 表名, 字段名, jdbcType
     * @param column 方法引用
     * @return 三元组: 表名, 字段名, jdbcType
     */
    public static Triple<String, String, Integer> getColumnCache(SFunction<?, ?> column){
        Triple<String, String, Integer> columnAndType = cache.get(column);
        if(columnAndType == null){
            columnAndType = new MutableTriple<>(column.lambdaClass(), column.lambdaFiled(), null);
            cache.put(column, columnAndType);
        }
        return columnAndType;
    }

    public static String getTable(SFunction<?, ?> column){
        return getColumnCache(column).getLeft();
    }

    public static String getColumn(SFunction<?, ?> column){
        return getColumnCache(column).getMiddle();
    }

    public static Integer getType(SFunction<?, ?> column){
        return getColumnCache(column).getRight();
    }
}
