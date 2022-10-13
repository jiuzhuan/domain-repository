package com.github.jiuzhuan.domain.repository.example.common.transaction;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * @author pengfwang@trip.com
 * @date 2022/10/13 10:33
 */
@Configuration
@EnableAspectJAutoProxy
@Aspect
public class RollbackAop {

    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;
    @Autowired
    TransactionDefinition transactionDefinition;

    /**
     * 注解作用在类上用@within,  作用在方法上用@annotation
     */
    @Around("@annotation(com.github.jiuzhuan.domain.repository.example.common.transaction.Rollback)")
    public Object proceed(ProceedingJoinPoint point) throws Throwable {
        return rollbackProceed(point);
    }

    /**
     * 注解作用在类上用@within,  作用在方法上用@annotation
     */
    @Around("@within(com.github.jiuzhuan.domain.repository.example.common.transaction.Rollback)")
    public Object within(ProceedingJoinPoint point) throws Throwable {
        return rollbackProceed(point);
    }

    private Object rollbackProceed(ProceedingJoinPoint point) throws Throwable {
        // 开启事务
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            // 执行
            return point.proceed();
        } finally {
            // 回滚
            dataSourceTransactionManager.rollback(transactionStatus);
        }
    }
}
