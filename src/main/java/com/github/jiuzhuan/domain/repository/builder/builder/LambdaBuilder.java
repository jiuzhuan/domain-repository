package com.github.jiuzhuan.domain.repository.builder.builder;

import com.github.jiuzhuan.domain.repository.builder.help.LambdaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 门店模式-集成增删改查建造者, 只需要记住这一个类就好了
 * 使用静态方法 避免autowire依赖注入
 * @author arrety
 * @date 2022/2/11 19:28
 */
@Component
public class LambdaBuilder {

    private static ApplicationContext applicationContext;

    @Autowired
    public void set(ApplicationContext applicationContext){
        LambdaBuilder.applicationContext = applicationContext;
    }

    /**
     * 创建查询构造器
     * @return 查询结果
     */
    @SafeVarargs
    public static <T> LambdaSelectBuilder select(SFunction<T, ?>... columns) {
        LambdaSelectBuilder selectBuilder = (LambdaSelectBuilder)applicationContext.getBean("lambdaSelectBuilder");
        selectBuilder.select(columns);
        return selectBuilder;
    }

    /**
     * 创建查询构造器
     * @return 查询结果
     */
    public static  LambdaSelectBuilder selectAll() {
        LambdaSelectBuilder selectBuilder = (LambdaSelectBuilder)applicationContext.getBean("lambdaSelectBuilder");
        selectBuilder.selectAll();
        return selectBuilder;
    }

    /**
     * 创建更新构造器
     * @param entityClass DB来源
     * @return 更新影响条数
     */
    public static LambdaUpdateBuilder update(Class<?> entityClass) {
//        LambdaUpdateBuilder updateBuilder = new LambdaUpdateBuilder();
        LambdaUpdateBuilder updateBuilder = (LambdaUpdateBuilder)applicationContext.getBean("lambdaUpdateBuilder");
        updateBuilder.update(entityClass);
        return updateBuilder;
    }

    /**
     * 创建新增构造器
     * @param entityClass DB来源
     * @return 插入影响条数
     */
    public static LambdaInsertBuilder insertInto(Class<?> entityClass) {
        LambdaInsertBuilder insertBuilder = (LambdaInsertBuilder)applicationContext.getBean("lambdaInsertBuilder");
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
