package com.arrety.domainrepository.domainpersistence.adapter.orm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author arrety
 * @date 2022/5/15 21:40
 */
@Component
public class JdbcTemplateAdapter extends AbstractAdapter{

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    protected <Entity> List<Entity> sel(String sql, List<Object> values, Class<Entity> entityClass) throws SQLException {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        for (int i = 0; i < values.size(); i++) {
            String param = "param" + i;
            sql = sql.replaceFirst("\\?", ":" + param);
            parameterSource.addValue(param, values.get(i));
        }
        return namedParameterJdbcTemplate.query(sql, parameterSource, new BeanPropertyRowMapper<>(entityClass));
    }

    @Override
    public void resolveDatabase(Class<?> entityClass) {

    }

    @Override
    protected int upd(String sql, List<Object> values) throws SQLException {
        return 0;
    }

    @Override
    protected <Entity> List<Map<String, Object>> selMapList(String sql, List<Object> values) throws SQLException {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        for (int i = 0; i < values.size(); i++) {
            String param = "param" + i;
            sql = sql.replaceFirst("\\?", ":" + param);
            parameterSource.addValue(param, values.get(i));
        }
        return namedParameterJdbcTemplate.queryForList(sql, parameterSource);
    }
}
