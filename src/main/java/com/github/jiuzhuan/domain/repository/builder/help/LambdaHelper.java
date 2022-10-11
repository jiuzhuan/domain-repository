package com.github.jiuzhuan.domain.repository.builder.help;

import com.github.jiuzhuan.domain.repository.builder.builder.AbstractLambdaBuilder;
import com.github.jiuzhuan.domain.repository.builder.builder.LambdaBuilder;
import com.github.jiuzhuan.domain.repository.builder.help.entity.UserRoleEntity;
import com.github.jiuzhuan.domain.repository.builder.help.entity.RoleEntity;
import com.github.jiuzhuan.domain.repository.builder.help.entity.UserEntity;
import com.google.common.collect.Lists;

import java.sql.SQLException;
import java.util.List;

/**
 * LambdaBuilder帮助类, 包含使用示例
 *
 * @author arrety
 * @date 2022/2/10 13:57
 */
public class LambdaHelper {


    /**
     * LambdaBuilder简介
     * 功能: 简化dal框架使用, 增强dal框架功能 todo:完善方法注释
     * 0.封装dal框架的基础标准API: 简单安全稳定(使用的基础API不大会经常变更, 且如果变更, 那么业务中直接使用基础API的代码也要更改), 屏蔽繁杂的SqlBuilder, DalQueryDao, StatementParameters交互
     * 1.支持lambda表达式和方法引用解析字段名和表名: 使用起来更优雅，不易拼错字段名或表名 fixme:完善缓存提高性能 fixme:解决debug模式lambda表达式解析错误问题
     * 2.自动获取并设置jdbcType: 减少重复劳动
     * 3.支持按条件表达式拼接的联表查询: 不用手写sql, dal原生的FreeSelectSqlBuilder不支持条件表达式, SelectSqlBuilder支持条件表达式但又不支持自定义sql
     * 4.支持按条件表达式拼接的update和insert语句: 省去很多if-else判断(dal原生的FreeUpdateSqlBuilder自定义sql不支持set和where条件表达式)
     * 5.自动别名: 不用手写别名(别名在联表时尤为重要)
     * 6.灵活的结果集映射: 增强联表查询到聚合类的结果映射 todo:可取代@Column注解, 自动由驼峰转下划线
     * 7.预编译的sql: 防止sql注入攻击 fixme:sql关键字定义到枚举SqlKeyword里
     * 8.封装常用的标准sql的mysql拓展: todo:补充常用的sql语句
     *      如insertOrUpdate()方法封装了语句insert...on duplicate key update...常用于没有则新增有就更新的业务场景(除了操作简单以外, 在并发情况下比先查后更新更安全)
     * 9.采用门面模式、建造者模式、中介模式、适配器模式: 易用(只需要记住一个类{@link LambdaBuilder}), 易读, 无侵入, 可扩展, 高度复用
     * 10.对debug友好: 提供方法查看预编译的sql{@link AbstractLambdaBuilder#getSql()}
     * 11.MP风格的API: 主流持久层框架风格API, 极低的学习成本, 文档直接参考MP官网-条件构造器一节-https://baomidou.com/pages/10c804/#abstractwrapper
     * 12.捕获{@link SQLException}: 一般认为这种异常应该在开发测试阶段解决(属于bug), 不应该出现在线上(不需要返回包装信息), 所以对该异常捕获并打印堆栈输入完整的预编译sql fixme:异常治理
     *
     * 只需要记住一个类, idea会提示三个入口方法
     * @see LambdaBuilder#selectAll() ()  查询
     * @see LambdaBuilder#update(Class) ()  更新
     * @see LambdaBuilder#insertInto(Class) ()  插入
     *
     * 以下是使用示例
     */
    public static void help() {

        // 创建实体
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(0L);
        userEntity.setName("");
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(0L);
        roleEntity.setTitle("");

        // 查询, 泛型为库名来源, 且是映射的结果集类型, 可以和表名不同, 但只会映射查询到的字段
        List<UserRoleEntity> userRoleEntities = LambdaBuilder
                // 查询全部
                // .selectAll()
                .select(UserEntity::getUserId, UserEntity::getName)
                // 相同实体的字段可以在同一个select方法内 不同实体的字段需要分开调用select方法(可多次调用)
                .select(RoleEntity::getTitle)
                // 表名
                .from(UserEntity.class)
                .leftJoin(RoleEntity.class)
                .on(UserEntity::getUserId, RoleEntity::getUserId)
                // 和MP不同的是必须调用where方法, 这是为了让开发尽可能接近原始sql语法
                .where()
                // 调用没有condition参数的重载方法默认condition=true
                // .eq(Entity1::getId, entity1.getId())
                .eq(true, UserEntity::getUserId, userEntity.getUserId())
                // 没有likeLeft()方法, 左右模糊全模糊需要用户自己拼接"%"
                .like(userEntity.getName() != null, UserEntity::getName, "%" + userEntity.getName() + "%")
                // list参数不能使用JDK11的List.of()方法创建, 可以使用JDK的Lists.newArrayList() 或guava的Lists.newArrayList()
                .or(true, i -> i.in(true, UserEntity::getPhoneNumber, Lists.newArrayList(userEntity.getPhoneNumber()))
                        .gt(RoleEntity::getUserId, roleEntity.getUserId()))
                .orderBy(UserEntity::getUserId, true)
                // 查一条
                // .selectFirst()
                .limit(1, 10)
                .selectList(UserRoleEntity.class);

        // 更新, 泛型为库名来源, 可以和表名不同
        Long update = LambdaBuilder
                .update(UserEntity.class)
                .set(UserEntity::getName, userEntity.getName())
                .set(UserEntity::getPhoneNumber, userEntity.getPhoneNumber())
                // 或者使用重载方法set实体的全部不为空的字段
                // .set(entity1)
                .where()
                .eq(UserEntity::getUserId, userEntity.getUserId())
                // 执行更新
                .update();

        // 插入, 泛型为库名来源, 可以和表名不同
        Long insertOrUpdate = LambdaBuilder
                .insertInto(UserEntity.class)
                .set(userEntity)
                // 或者使用重载方法set单个字段, 可重复调用
                // .set(Entity1::getName, entity1.getName())
                // 插入或更新, mysql拓展语法,插入-当发送唯一索引冲突时更新
                .insertOrUpdate();

        List<UserRoleEntity> objects = LambdaBuilder
                .selectAll().from(UserEntity.class).leftJoin(RoleEntity.class).on(UserEntity::getUserId, RoleEntity::getUserId)
                .eq(UserEntity::getUserId, userEntity.getUserId())
                .in(true, RoleEntity::getRoleId, Lists.newArrayList(1L))
                .orderBy(true, UserEntity::getUserId, true).orderBy(false, UserEntity::getPhoneNumber, true)
                .selectList(UserRoleEntity.class);
    }

}
