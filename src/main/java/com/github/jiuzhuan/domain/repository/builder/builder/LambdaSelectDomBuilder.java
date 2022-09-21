package com.github.jiuzhuan.domain.repository.builder.builder;

import com.github.jiuzhuan.domain.repository.common.utils.CheckArgUtil;
import com.github.jiuzhuan.domain.repository.common.utils.PropertyNamer;
import com.github.jiuzhuan.domain.repository.common.utils.SqlKeyword;
import com.github.jiuzhuan.domain.repository.domain.utils.DomainFieldCache;
import com.github.jiuzhuan.domain.repository.domain.utils.DomainFieldUtil;
import com.github.jiuzhuan.domain.repository.common.exception.ReflectionException;
import com.github.jiuzhuan.domain.repository.builder.interfaces.Func;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 聚合查询构造器
 * 将查询结果映射到嵌套聚合实体中
 * @author arrety
 * @date 2022/4/10 16:10
 */
@Component
public class LambdaSelectDomBuilder extends AbstractWhereLambdaBuilder<LambdaSelectDomBuilder> implements Func<LambdaSelectDomBuilder> {

    public LambdaSelectDomBuilder() {
        super();
        sql.append(" select ");
    }

    private static final String ASFLAG = "vvv";
    private boolean isSelectAll = false;
    /**
     * 表和字段映射缓存
     */
    private Map<String, Object> fieldValueMap = new HashMap<>();
    /**
     * 全部结果
     * key: 实体类
     * value: 该实体对应表的全部结果
     */
    protected Map<Class<?>, List<Object>> data = new HashMap<>();
    /**
     * 表名-实体类
     */
    private Map<String, Class<?>> tables = new HashMap<>();

    @Override
    public <T> LambdaSelectDomBuilder orderBy(SFunction<T, ?> column, boolean isAsc) {
        String columnName = getColumn(column);
        // FreeSelectSqlBuilder的orderBy方法不能传入别名 所以只能自己拼sql
        sql.append("order by")
                .append(columnName)
                .append(isAsc ? "asc":"desc");
        return builder;
    }


    @Override
    public LambdaSelectDomBuilder limit(Integer pageIndex, Integer pageSize) {
        CheckArgUtil.checkArguments(pageIndex, pageSize);
        sql.append("limit ?,?");
        values.add(pageIndex);
        values.add(pageSize);

        return builder ;
    }

    /**
     * // TODO: 2022/4/24 是否可以挪到 SelectDomain里
     * 用于构造聚合嵌套的连接条件
     * @param <T>
     * @param value
     * @return
     */
    public <T> LambdaSelectDomBuilder appendIn(String columnName, Object value) {
        if (!sql.toString().endsWith("( ")) {
            sql.append(SqlKeyword.AND.getSqlSegment());
        }
        sql.append(columnName)
                .append(SqlKeyword.IN.getSqlSegment())
                .append(" (?) ");
        values.add(value);
        return builder;
    }

    /**
     * 定义别名=表名vvv列名
     * 由于不确定聚合实体里会以那几个实体联表查询, 所以无法确定返回类型, 只能用map, 为了后续返回map时映射到实体里, 列名必须加上表名
     */
    public <T> LambdaSelectDomBuilder select(SFunction<T, ?>... columns) {
        List<String> columnStrings = new ArrayList<>();
        for (SFunction<T, ?> column : columns) {
            Triple<String, String, Integer> tableAndColumn = getTableAndColumn(column);
            columnStrings.add(tableAndColumn.getLeft() + ASFLAG + tableAndColumn.getMiddle());
        }
        firstSet();
        sql.append(Joiner.on(", ").join(columnStrings));
        return builder;
    }

    public LambdaSelectDomBuilder selectAll() {
        isSelectAll = true;
        sql.append(" * ");
        firstSet();
        return builder;
    }

    /**
     * 定义别名=表名vvv列名
     * 由于不确定聚合实体里会以那几个实体联表查询, 所以无法确定返回类型, 只能用map, 为了后续返回map时映射到实体里, 列名必须加上表名
     */
    public <T> LambdaSelectDomBuilder from(Class<T> clazz) {
        if (isSelectAll) {
            StringBuilder sel = new StringBuilder();
            String table = clazz.getSimpleName();
            for (Field declaredField : clazz.getDeclaredFields()) {
                sel.append(PropertyNamer.toUnderline(table)).append(".").append(PropertyNamer.toUnderline(declaredField.getName())).append(" ");
                sel.append(StringUtils.uncapitalize(table)).append(ASFLAG).append(declaredField.getName()).append(",");
            }
            String replaceAll = sql.toString().replaceAll("\\*", sel.substring(0, sel.length() - 1));
            sql = new com.github.jiuzhuan.domain.repository.common.utils.StringBuilder(replaceAll);
        }
        sql.append(" from ")
                .append(PropertyNamer.toUnderline(clazz.getSimpleName()));
        tables.put(StringUtils.uncapitalize(clazz.getSimpleName()), clazz);
        adapter.resolveDatabase(clazz);
        return builder;
    }

    @Override
    public <T> LambdaSelectDomBuilder leftJoin(Class<T> clazz) {
        if (isSelectAll) {
            StringBuilder sel = new StringBuilder("select ");
            String table = clazz.getSimpleName();
            for (Field declaredField : clazz.getDeclaredFields()) {
                sel.append(PropertyNamer.toUnderline(table)).append(".").append(PropertyNamer.toUnderline(declaredField.getName())).append(" ");
                sel.append(StringUtils.uncapitalize(table)).append(ASFLAG).append(declaredField.getName()).append(",");
            }
            String replaceAll = sql.toString().replaceAll("select", sel.toString());
            sql = new com.github.jiuzhuan.domain.repository.common.utils.StringBuilder(replaceAll);
            tables.put(StringUtils.uncapitalize(clazz.getSimpleName()), clazz);
        }
        return super.leftJoin(clazz);
    }

    /**
     * 聚合查询(可联表)
     */
    public Map<Class<?>, List<Object>> execute() {
        try {
            List<Map<String, Object>> rows = adapter.selectMapList(sql.toString(), values);
            Map<Class<?>, List<Object>> result = new HashMap<>();
            for (Map<String, Object> row : rows) {
                // 构造这一行的多个不同实体
                Map<Class<?>, Object> tableItemMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String[] tableAndCol = entry.getKey().split(ASFLAG);
                    Class<?> tableClass = tables.get(tableAndCol[0]);
                    if (tableItemMap.get(tableClass) == null) {
                        Object item = tableClass.getDeclaredConstructors()[0].newInstance();
                        tableItemMap.put(tableClass, item);
                    }
                }
                // 赋值
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String[] tableAndCol = entry.getKey().split(ASFLAG);
                    Class<?> tableClass = tables.get(tableAndCol[0]);
                    Object item = tableItemMap.get(tableClass);
                    Field field = item.getClass().getField(tableAndCol[1]);
                    field.set(item, entry.getValue());
                }
                for (Map.Entry<Class<?>, Object> entry : tableItemMap.entrySet()) {
                    List<Object> values = result.get(entry.getKey());
                    if (values == null) {
                        result.put(entry.getKey(), Arrays.asList(entry.getValue()));
                    } else {
                        values.add(entry.getValue());
                    }
                }
            }
            data.putAll(result);
            return result;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            throw new ReflectionException(e);
        }
    }

    public void clear(){
        firstSet = true;
        sql = new com.github.jiuzhuan.domain.repository.common.utils.StringBuilder();
        sql.append(" select ");
        values = new ArrayList<>();
    }


}
