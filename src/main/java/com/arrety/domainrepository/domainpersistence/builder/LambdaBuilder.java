package com.arrety.domainrepository.domainpersistence.builder;

import com.arrety.domainrepository.domainpersistence.help.LambdaHelper;

/**
 * 门店模式-集成增删改查建造者, 只需要记住这一个类就好了
 * @author arrety
 * @date 2022/2/11 19:28
 */
public class LambdaBuilder {

    /**
     * 创建查询构造器
     * @return 查询结果
     */
    @SafeVarargs
    public static <T> LambdaSelectBuilder select(SFunction<T, ?>... columns) {
        LambdaSelectBuilder selectBuilder = new LambdaSelectBuilder();
        selectBuilder.select(columns);
        return selectBuilder;
    }

    /**
     * 创建查询构造器
     * @return 查询结果
     */
    public static  LambdaSelectBuilder selectAll() {
        LambdaSelectBuilder selectBuilder = new LambdaSelectBuilder();
        selectBuilder.selectAll();
        return selectBuilder;
    }

    /**
     * 创建更新构造器
     * @param entityClass DB来源
     * @return 更新影响条数
     */
    public static LambdaUpdateBuilder update(Class<?> entityClass) {
        LambdaUpdateBuilder updateBuilder = new LambdaUpdateBuilder();
        updateBuilder.update(entityClass);
        return updateBuilder;
    }

    /**
     * 创建新增构造器
     * @param entityClass DB来源
     * @return 插入影响条数
     */
    public static LambdaInsertBuilder insertInto(Class<?> entityClass) {
        LambdaInsertBuilder insertBuilder = new LambdaInsertBuilder();
        insertBuilder.insertInto(entityClass);
        return insertBuilder;
    }

    /**
     * 帮助文档 - 使用示例
     * @see LambdaHelper#help()
     */
    public static void help(){

    }
}
