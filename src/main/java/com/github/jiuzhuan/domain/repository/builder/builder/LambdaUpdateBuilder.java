package com.github.jiuzhuan.domain.repository.builder.builder;

import com.github.jiuzhuan.domain.repository.builder.interfaces.SetCase;
import com.github.jiuzhuan.domain.repository.common.utils.SqlKeyword;
import com.github.jiuzhuan.domain.repository.common.utils.PropertyNamer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 更新构造器
 * @author arrety
 * @date 2022/1/29 14:36
 */
@Component
@Scope("prototype")
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
        sql.append("update").append(tableName);
        adapter.resolveDatabase(clazz);
        return this;
    }


    @Override
    public LambdaUpdateBuilder appendSet(String filed, Object value) {
        firstSet(true);
        sql.append(filed)
                .append(SqlKeyword.EQ.getSqlSegment())
                .append("?");
        values.add(value);
        return this;
    }


    public <T> T update() {
        return (T)adapter.update(sql.toString(), values);
    }

}
