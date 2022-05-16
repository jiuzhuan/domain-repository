package com.arrety.domainrepository.domainpersistence.builder;

import com.arrety.domainrepository.domainpersistence.common.CheckArgUtil;
import com.arrety.domainrepository.domainpersistence.common.SqlKeyword;
import com.arrety.domainrepository.domainpersistence.interfaces.JoinCase;
import com.arrety.domainrepository.domainpersistence.interfaces.WhereCase;
import org.springframework.core.annotation.AnnotationUtils;

import com.arrety.domainrepository.domainpersistence.common.PropertyNamer;
import java.util.List;
import java.util.function.Consumer;

/**
 * 抽象父类, 条件构造器, 执行器
 *
 * @author arrety
 * @date 2022/2/7 14:47
 */
public abstract class AbstractWhereLambdaBuilder<Builder> extends AbstractLambdaBuilder<Builder> implements JoinCase<Builder>, WhereCase<Builder>{

    public AbstractWhereLambdaBuilder() {
        super();
    }

    protected void nestedAppendSql(SqlKeyword sqlKeyword, Consumer<Builder> consumer) {
        sql.append(sqlKeyword.getSqlSegment()).append(" (");
        consumer.accept(builder);
        sql.append(" ) ");
    }

    protected  <T> void appendSql(SFunction<T, ?> column, SqlKeyword sqlKeyword, Object value) {
        String columnName = getColumn(column);
        if (!sql.toString().endsWith("( ")) {
            sql.append(SqlKeyword.AND.getSqlSegment());
        }
        sql.append(columnName)
                .append(sqlKeyword.getSqlSegment())
                .append(" ? ");
        values.add(value);
    }

    public Builder where() {
        sql.append(" where 1=1 ");
        return builder;
    }

    @Override
    public <T> Builder leftJoin(Class<T> clazz) {
        sql.append(" left join ")
                .append(PropertyNamer.toUnderline(clazz.getSimpleName()));
        return builder;
    }


    @Override
    public <T, F> Builder on(SFunction<T, ?> column1, SFunction<F, ?> column2) {
        String columnName1 = getColumn(column1);
        String columnName2 = getColumn(column2);
        sql.append(" on ")
                .append(columnName1)
                .append(" = ")
                .append(columnName2);
        return builder;
    }


    @Override
    public Builder and(Consumer<Builder> consumer) {
        nestedAppendSql(SqlKeyword.AND, consumer);
        return builder;
    }

    @Override
    public Builder or(Consumer<Builder> consumer) {
        nestedAppendSql(SqlKeyword.OR, consumer);
        return builder;
    }

    @Override
    public <T> Builder eq(SFunction<T, ?> column, Object value) {
        CheckArgUtil.checkArguments(value);
        appendSql(column, SqlKeyword.EQ, value);
        return builder;
    }

    private <T> void appendIn(SFunction<T, ?> column, SqlKeyword sqlKeyword, Object value) {
        String columnName = getColumn(column);
        sql.append(SqlKeyword.AND.getSqlSegment())
                .append(columnName)
                .append(sqlKeyword.getSqlSegment())
                .append(" (?) ");
        values.add(value);
    }

    @Override
    public <T> Builder ne(SFunction<T, ?> column, Object value) {
        CheckArgUtil.checkArguments(value);
        appendSql(column, SqlKeyword.NE, value);
        return builder;
    }


    @Override
    public <T> Builder gt(SFunction<T, ?> column, Object value) {
        CheckArgUtil.checkArguments(value);
        appendSql(column, SqlKeyword.GT, value);
        return builder;
    }


    @Override
    public <T> Builder ge(SFunction<T, ?> column, Object value) {
        CheckArgUtil.checkArguments(value);
        appendSql(column, SqlKeyword.GE, value);
        return builder;
    }


    @Override
    public <T> Builder lt(SFunction<T, ?> column, Object value) {
        CheckArgUtil.checkArguments(value);
        appendSql(column, SqlKeyword.LT, value);
        return builder;
    }


    @Override
    public <T> Builder le(SFunction<T, ?> column, Object value) {
        CheckArgUtil.checkArguments(value);
        appendSql(column, SqlKeyword.LE, value);
        return builder;
    }


    @Override
    public <T> Builder like(SFunction<T, ?> column, String value) {
        CheckArgUtil.checkArguments(value);
        appendSql(column, SqlKeyword.LIKE, value);
        return builder;
    }


    /**
     * @param column 列名lambda
     * @param values 集合，不支持List.of()返回的不可变集合
     * @return
     */
    @Override
    public <T> Builder in(SFunction<T, ?> column, List<?> values) {
        CheckArgUtil.checkArguments(values);
        appendIn(column, SqlKeyword.IN, values);
        return builder;
    }



}
