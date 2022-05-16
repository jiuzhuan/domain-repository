package com.arrety.domainrepository.domainpersistence.builder;

import com.arrety.domainrepository.domainpersistence.adapter.orm.AbstractAdapter;
import com.arrety.domainrepository.domainpersistence.adapter.orm.JdbcTemplateAdapter;
import com.arrety.domainrepository.domainpersistence.common.LambdaColumnMap;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象父类, 条件构造器, 执行器
 *
 * @author arrety
 * @date 2022/2/7 14:47
 */
public abstract class AbstractLambdaBuilder<Builder> {

    protected final Builder builder = (Builder) this;

    protected boolean firstSet = true;

    protected AbstractAdapter adapter;
    protected com.arrety.domainrepository.domainpersistence.common.StringBuilder sql = new com.arrety.domainrepository.domainpersistence.common.StringBuilder();
    protected List<Object> values = new ArrayList<>();

    public AbstractLambdaBuilder() {
        //适配器
        this.adapter = new JdbcTemplateAdapter();
    }

    @Autowired
    public final void setAdapter(JdbcTemplateAdapter jdbcTemplateAdapter){
        this.adapter = jdbcTemplateAdapter;
    }

    public void clear(){
        sql.clear();
        values.clear();
    }

    public String getSql() {
        return sql.toString();
    }

    protected void firstSet() {
        firstSet(sql);
    }

    protected void firstSet(com.arrety.domainrepository.domainpersistence.common.StringBuilder fff) {
        if (firstSet) {
            firstSet = false;
        } else {
            fff.append(" , ");
        }
    }
    
    protected <T> String getColumn(SFunction<T, ?> column){
        Triple<String, String, Integer> columnCache = LambdaColumnMap.getColumnCache(column);
        return columnCache.getLeft() + "." + columnCache.getMiddle();
    }

    protected <T> Triple<String, String, Integer> getTableAndColumn(SFunction<T, ?> column){
        Triple<String, String, Integer> triple = LambdaColumnMap.getColumnCache(column);
        return triple;
    }



}
