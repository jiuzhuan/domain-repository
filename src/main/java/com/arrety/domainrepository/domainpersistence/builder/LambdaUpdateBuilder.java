package com.arrety.domainrepository.domainpersistence.builder;

import com.arrety.domainrepository.domainpersistence.common.SqlKeyword;
import com.arrety.domainrepository.domainpersistence.interfaces.SetCase;
import org.springframework.core.annotation.AnnotationUtils;

import com.arrety.domainrepository.domainpersistence.common.PropertyNamer;

/**
 * 更新构造器
 * @author arrety
 * @date 2022/1/29 14:36
 */
public class LambdaUpdateBuilder extends AbstractWhereLambdaBuilder<LambdaUpdateBuilder> implements SetCase<LambdaUpdateBuilder> {



    /**
     * 不可多次调用
     * todo 别名
     *
     * @param clazz 表名
     * @param <T>
     * @return
     */
    public final <T> LambdaUpdateBuilder update(Class<T> clazz) {
        String tableName = PropertyNamer.toUnderline(clazz.getSimpleName());
        sql.append("update").append(tableName).append("set");
        adapter.resolveDatabase(clazz);
        return this;
    }


    @Override
    public LambdaUpdateBuilder appendSet(String filed, Object value) {
        firstSet();
        sql.append(filed)
                .append(SqlKeyword.EQ.getSqlSegment())
                .append("?");
        values.add(value);
        return this;
    }


    public int update() {
        return adapter.update(sql.toString(), values);
    }

}