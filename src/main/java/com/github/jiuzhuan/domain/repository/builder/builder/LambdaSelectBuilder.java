package com.github.jiuzhuan.domain.repository.builder.builder;

import com.github.jiuzhuan.domain.repository.common.utils.CheckArgUtil;
import com.github.jiuzhuan.domain.repository.builder.interfaces.Func;
import com.github.jiuzhuan.domain.repository.common.utils.PropertyNamer;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询构造器
 * @author arrety
 * @date 2022/1/29 14:36
 */
@Component
public class LambdaSelectBuilder extends AbstractWhereLambdaBuilder<LambdaSelectBuilder> implements Func<LambdaSelectBuilder> {

    public LambdaSelectBuilder() {
        super();
        sql.append(" select ");
    }

    @Override
    public void clear(){
        super.clear();
        sql.append(" select ");
    }

    /**
     * 可多次调用
     * todo 别名
     *
     * @param columns 不同实体要分开调用select方法
     * @param <T>
     * @return
     */
    public <T> LambdaSelectBuilder select(SFunction<T, ?>... columns) {
        clear();
        List<String> columnStrings = new ArrayList<>();
        for (SFunction<T, ?> column : columns) {
            String columnName = getColumn(column);
            columnStrings.add(columnName);
        }
        firstSet();
        sql.append(Joiner.on(", ").join(columnStrings));
        return builder;
    }

    public LambdaSelectBuilder selectAll() {
        clear();
        sql.append(" * ");
        firstSet();
        return builder;
    }

    public <T> LambdaSelectBuilder from(Class<T> clazz) {
        sql.append(" from ")
                .append(PropertyNamer.toUnderline(clazz.getSimpleName()));
        adapter.resolveDatabase(clazz);
        return builder;
    }

    public <T> LambdaSelectBuilder from(Class<T> clazz, String as) {
        sql.append(" from ")
                .append(PropertyNamer.toUnderline(clazz.getSimpleName()))
                .append(as);
        return builder;
    }



    @Override
    public <T> LambdaSelectBuilder orderBy(SFunction<T, ?> column, boolean isAsc) {
        String columnName = getColumn(column);
        // FreeSelectSqlBuilder的orderBy方法不能传入别名 所以只能自己拼sql
        sql.append("order by")
                .append(columnName)
                .append(isAsc ? "asc":"desc");
        return builder;
    }


    @Override
    public LambdaSelectBuilder limit(Integer pageIndex, Integer pageSize) {
        CheckArgUtil.checkArguments(pageIndex, pageSize);
        sql.append("limit ?,?");
        values.add(pageIndex);
        values.add(pageSize);
        return builder ;
    }

    public <Entity> List<Entity> selectList(Class<Entity> clazz) {
        return adapter.selectList(sql.toString(), values, clazz);
    }

    public List<Map<String, Object>> selectMapList() {
        return adapter.selectMapList(sql.toString(), values);
    }

    public <Entity> List<Entity> selectListForUpdate(Class<Entity> clazz) {
        sql.append("for update");
        return adapter.selectList(sql.toString(), values, clazz);
    }

    public <Entity> Entity selectFirst(Class<Entity> clazz) {
        limit(1, 1);
        return selectList(clazz).stream().findFirst().orElse(null);
    }

}
