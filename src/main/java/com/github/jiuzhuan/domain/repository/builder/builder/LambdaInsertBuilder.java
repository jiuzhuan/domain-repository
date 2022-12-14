package com.github.jiuzhuan.domain.repository.builder.builder;

import com.github.jiuzhuan.domain.repository.builder.interfaces.SetCase;
import com.github.jiuzhuan.domain.repository.common.utils.PropertyNamer;
import com.github.jiuzhuan.domain.repository.common.utils.SqlKeyword;
import com.github.jiuzhuan.domain.repository.common.utils.StringBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * 新增构造器
 * @author arrety
 * @date 2022/1/29 14:36
 */
@Component
@Scope("prototype")
public class LambdaInsertBuilder extends AbstractLambdaBuilder<LambdaInsertBuilder> implements SetCase<LambdaInsertBuilder> {

    private StringBuilder nestedSql = new StringBuilder();

    public LambdaInsertBuilder() {
        super();
    }


    /**
     * 不可多次调用
     * todo 别名
     *
     * @param clazz 表名
     * @param <T>
     * @return
     */
    public final <T> LambdaInsertBuilder insertInto(Class<T> clazz) {
        String tableName = PropertyNamer.toUnderline(clazz.getSimpleName());
        sql.append("insert into").append(tableName);
        adapter.resolveDatabase(clazz);
        return this;
    }


    @Override
    public LambdaInsertBuilder appendSet(String fieldName, Object value) {
        // 保存update语句
        firstSet(nestedSql, true);
        nestedSql.append(fieldName)
                .append(SqlKeyword.EQ.getSqlSegment())
                .append(" ? ");
        values.add(value);
        return this;
    }

    /**
     * 必须设置唯一索引或主键才能达到[重复时更新], 其原理是插入失败时改为更新
     * 和replace拓展语句不同点在于, replace在唯一索引或主键冲突时会先删除再插入(慎用物理删除)
     * TODO: 2022/2/9 关键字维护到SqlKeyword枚举类中
     * {@inheritDoc}
     *
     * @return
     * @throws SQLException
     */
    public <T> T insertOrUpdate() {
        sql.append(nestedSql.toString())
                .append(" on duplicate key update ")
                .append(nestedSql.toString());
        values.addAll(values);
        return (T)adapter.update(sql.toString(), values);
    }

    public <T> T insert() {
        sql.append(nestedSql.toString());
        return (T)adapter.update(sql.toString(), values);
    }

}
