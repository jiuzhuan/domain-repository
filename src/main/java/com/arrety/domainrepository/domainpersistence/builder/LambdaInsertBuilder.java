package com.arrety.domainrepository.domainpersistence.builder;

import com.arrety.domainrepository.domainpersistence.common.PropertyNamer;
import com.arrety.domainrepository.domainpersistence.common.SqlKeyword;
import com.arrety.domainrepository.domainpersistence.interfaces.SetCase;
import org.springframework.core.annotation.AnnotationUtils;

import com.arrety.domainrepository.domainpersistence.common.PropertyNamer;
import java.sql.SQLException;

/**
 * 新增构造器
 * @author arrety
 * @date 2022/1/29 14:36
 */
public class LambdaInsertBuilder extends AbstractLambdaBuilder<LambdaInsertBuilder> implements SetCase<LambdaInsertBuilder> {

    private com.arrety.domainrepository.domainpersistence.common.StringBuilder nestedSql = new com.arrety.domainrepository.domainpersistence.common.StringBuilder();

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
        sql.append("insert into").append(tableName).append("set");
        adapter.resolveDatabase(clazz);
        return this;
    }


    @Override
    public LambdaInsertBuilder appendSet(String fieldName, Object value) {
        // 保存update语句
        firstSet(nestedSql);
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
    public int insertOrUpdate() {
        sql.append(nestedSql.toString())
                .append(" on duplicate key update ")
                .append(nestedSql.toString());
        values.addAll(values);
        return adapter.update(sql.toString(), values);
    }

}
