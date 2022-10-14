package com.github.jiuzhuan.domain.repository.domain.utils;

import com.github.jiuzhuan.domain.repository.domain.annotation.Dom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 领域对象表字段映射缓存
 * @author arrety
 * @date 2022/4/24 18:26
 */
@Component
public class DomainFieldCache {

    static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext){
        DomainFieldCache.applicationContext = applicationContext;
    }

    /**
     * 聚合实体中字段和表的映射缓存
     */
    private static ConcurrentHashMap<String, Map<String, Map<String, List<Field>>>> classTableColumnMap = new ConcurrentHashMap<>();

    public static void init(Class<?> clazz) {
        Map<String, Map<String, List<Field>>> tableColumnMap = classTableColumnMap.get(clazz.getSimpleName());
        if (tableColumnMap == null) {
            tableColumnMap = new HashMap<>();
            classTableColumnMap.put(clazz.getSimpleName(), tableColumnMap);
            List<Field> fieldChain = new ArrayList<>();
            resolve(clazz.getDeclaredFields(), tableColumnMap, fieldChain);
        }
    }

    private static void resolve(Field[] fields, Map<String, Map<String, List<Field>>> tableColumnMap, List<Field> fieldChain) {
        //初始化聚合类的字段缓存
        for (Field table : fields) {
            table.setAccessible(true);
            Class<?> tableType = ReflectionUtil.getGenericType(table);
            List<Field> fieldChainTable = new ArrayList<>(fieldChain);
            fieldChainTable.add(table);
            if (tableType.isAnnotationPresent(Dom.class)) {
                Map<String, List<Field>> columnMap = new HashMap<>();
                List<Field> fieldChainColumn = new ArrayList<>(fieldChainTable);
                fieldChainColumn.add(table);
                columnMap.put(table.getName(), fieldChainColumn);
                tableColumnMap.putIfAbsent(table.getName(), columnMap);
                resolve(tableType.getDeclaredFields(), tableColumnMap, fieldChainTable);
            } else {
                Map<String, List<Field>> columnMap = new HashMap<>();
                for (Field column : tableType.getDeclaredFields()) {
                    column.setAccessible(true);
                    List<Field> fieldChainColumn = new ArrayList<>(fieldChainTable);
                    fieldChainColumn.add(column);
                    columnMap.put(column.getName(), fieldChainColumn);
                }
                tableColumnMap.putIfAbsent(table.getName(), columnMap);
            }
        }
    }

    /**
     * 获取表列对应的聚合类的属性链, 用于聚合类结果集映射
     * @param className 聚合类
     * @param tableName 表名
     * @param fieldName 列名
     * @return
     */
    public static List<Field> getFields(String className, String tableName, String fieldName)  {
        return classTableColumnMap.get(className).get(tableName).get(fieldName);
    }

    /**
     * 获取表对应的聚合类的属性链, 用于聚合结果的分类结果
     * @param className
     * @param tableName
     * @return
     */
    public static List<Field> getFields(String className, String tableName)  {
        List<Field> fields = classTableColumnMap.get(className).get(tableName).values().stream().findFirst().get();
        fields = fields.subList(0, fields.size() - 1);
        return fields;
    }

}
