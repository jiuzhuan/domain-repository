package com.github.jiuzhuan.domain.repository.builder.builder;

import com.github.jiuzhuan.domain.repository.builder.adapter.orm.AbstractAdapter;
import com.github.jiuzhuan.domain.repository.common.utils.LambdaColumnMap;
import com.github.jiuzhuan.domain.repository.common.utils.StringBuilder;
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

    @Autowired
    protected AbstractAdapter adapter;
    protected StringBuilder sql = new StringBuilder();
    protected List<Object> values = new ArrayList<>();


    public void clear(){
        sql.clear();
        values.clear();
    }

    public String getSql() {
        return sql.toString();
    }

    protected void firstSet(boolean isSet) {
        firstSet(sql, isSet);
    }

    protected void firstSet(StringBuilder fff, boolean isSet) {
        if (firstSet) {
            firstSet = false;
            if (isSet) fff.append("set");
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
