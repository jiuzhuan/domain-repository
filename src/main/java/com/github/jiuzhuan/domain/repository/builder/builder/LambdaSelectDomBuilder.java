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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        }
        return super.leftJoin(clazz);
    }

    /**
     * 聚合查询(可联表)
     */
    public <T> List<T> selectList(Class<T> domClass) {
        try {
            DomainFieldCache.init(domClass);
            List<Map<String, Object>> list = adapter.selectMapList(sql.toString(), values);
            List<T> domList =new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                T domObject = (T)domClass.getDeclaredConstructors()[0].newInstance();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    //注入聚合实体
                    String[] tableAndCol = entry.getKey().split(ASFLAG);
                    // TODO: 2022/4/25 map整体注入 处理list
                    DomainFieldUtil.set(domObject, tableAndCol[0], tableAndCol[1], entry.getValue());
                    this.mapToObject(tableAndCol, i, entry.getValue());
                }
                domList.add(domObject);
            }
            return domList;
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new ReflectionException("@JoinOn error ", e);
        }
    }

    /**
     * 结果集聚合映射拓展方法
     * @param tableAndCol
     * @param i
     * @param value
     */
    protected void mapToObject(String[] tableAndCol, Integer i, Object value) {
    }

    public void clear(){
        firstSet = true;
        sql = new com.github.jiuzhuan.domain.repository.common.utils.StringBuilder();
        sql.append(" select ");
        values = new ArrayList<>();
    }


}
