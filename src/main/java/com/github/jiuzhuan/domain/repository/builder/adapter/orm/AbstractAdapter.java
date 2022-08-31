package com.github.jiuzhuan.domain.repository.builder.adapter.orm;

import com.github.jiuzhuan.domain.repository.common.exception.LambdaBuilderException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author arrety
 * @date 2022/2/15 15:10
 */
public abstract class AbstractAdapter {

    public <Entity> List<Entity> selectList(String sql, List<Object> values, Class<Entity> entityClass){
        try {
            return sel(sql, values, entityClass);
        }catch (Exception e){
            throw new LambdaBuilderException(sql, e);
        }
    }

    protected abstract <Entity> List<Entity> sel(String sql, List<Object> values, Class<Entity> entityClass) throws SQLException;

    public int update(String sql, List<Object> values){
        try {
            return upd(sql, values);
        }catch (Exception e){
            throw new LambdaBuilderException(sql, e);
        }
    }

    public abstract void resolveDatabase(Class<?> entityClass);

    protected abstract int upd(String sql, List<Object> values) throws SQLException;

    public <Entity> List<Map<String, Object>> selectMapList(String sql, List<Object> values){
        try {
            return selMapList(sql, values);
        }catch (Exception e){
            throw new LambdaBuilderException(sql, e);
        }
    }

    protected abstract <Entity> List<Map<String, Object>> selMapList(String sql, List<Object> values) throws SQLException;
}
