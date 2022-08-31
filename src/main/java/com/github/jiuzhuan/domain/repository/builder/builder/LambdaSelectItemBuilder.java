package com.github.jiuzhuan.domain.repository.builder.builder;

import com.github.jiuzhuan.domain.repository.common.utils.PropertyNamer;
import com.github.jiuzhuan.domain.repository.common.utils.SqlKeyword;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

/**
 * 实体查询构造器
 * 可以同时构造多个实体的查询， 且多个实体查询互不干扰, 用于替换联表查询
 *
 * @author arrety
 * @date 2022/4/6 14:36
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LambdaSelectItemBuilder extends LambdaSelectBuilder {

    /**
     * 查询和条件涉及的表名集合
     */
    public Set<String> tableParams = new HashSet<>();

    /**
     * 查询:表名 - 列名
     */
    public Map<String, List<String>> selectParams = new HashMap<>();
    /**
     * 条件:表名 - 列名 - 值
     */
    private Map<String, List<Triple<String, SqlKeyword, Object>>> whereParams = new HashMap<>();

    /**
     * 每个实体单独的sql
     */
//    protected com.ctrip.car.commodity.store.svc.lambdabuilder.common.StringBuilder sqlItem = new com.ctrip.car.commodity.store.svc.lambdabuilder.common.StringBuilder();

    /**
     * 未指明列的表会查询全部
     * @param columns 不同实体要分开调用select方法
     * @param <T>
     * @return
     */
    @Override
    public <T> LambdaSelectBuilder select(SFunction<T, ?>... columns) {
        for (SFunction<T, ?> column : columns) {
            Triple<String, String, Integer> tableAndColumn = getTableAndColumn(column);
            String tableName = tableAndColumn.getLeft();
            String columnName = tableAndColumn.getMiddle();
            List<String> tableSelect = selectParams.get(tableName);
            if (tableSelect == null) {
                selectParams.put(tableName, Lists.newArrayList(columnName));
            } else {
                tableSelect.add(columnName);
            }
            tableParams.add(tableName);
        }
        return builder;
    }

    @Override
    protected <T> void appendSql(SFunction<T, ?> column, SqlKeyword sqlKeyword, Object value) {
        Triple<String, String, Integer> tableAndColumn = getTableAndColumn(column);
        String tableName = tableAndColumn.getLeft();
        String columnName = tableAndColumn.getMiddle();
        Object columnValue = tableAndColumn.getRight();
        List<Triple<String, SqlKeyword, Object>> tableWhere = whereParams.get(tableName);
        MutableTriple<String, SqlKeyword, Object> triple = MutableTriple.of(columnName, sqlKeyword, columnValue);
        if (tableWhere == null) {
            whereParams.put(tableName, Lists.newArrayList(triple));
        } else {
            tableWhere.add(triple);
        }
        tableParams.add(tableName);
    }

    /**
     * 关联用
     */
    public void in(String tableName, String columnName, Object values) {
        MutableTriple<String, SqlKeyword, Object> triple = MutableTriple.of(tableName + "." + columnName, SqlKeyword.IN, values);
        whereParams.put(tableName, Lists.newArrayList(triple));
        tableParams.add(tableName);
    }

    @Override
    public <Entity> List<Entity> selectList(Class<Entity> clazz) {
        clear();
        String tableName = StringUtils.uncapitalize(clazz.getSimpleName());
        List<String> selects = selectParams.get(tableName);
        if (CollectionUtils.isEmpty(selects)) {
            sql.append("*");
        } else {
            for (String columnName : selects) {
                firstSet(sql);
                sql.append(columnName);
            }
        }
        from(clazz);
        sql.append("where 1=1");
        List<Triple<String, SqlKeyword, Object>> triples = whereParams.get(tableName);
        if (triples != null) {
            for (Triple<String, SqlKeyword, Object> columnNameAndValue : triples) {
                sql.append("and").append(PropertyNamer.toUnderline(columnNameAndValue.getLeft())).append(columnNameAndValue.getMiddle().getSqlSegment())
                        .append("(").append("?").append(")");
                values.add(columnNameAndValue.getRight());
            }
        }
        return adapter.selectList(sql.toString(), values, clazz);
    }
}
