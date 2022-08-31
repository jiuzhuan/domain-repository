package com.github.jiuzhuan.domain.repository.common.exception;

/**
 * @author arrety
 * @date 2022/2/8 17:42
 */
public class ReflectionException extends RuntimeException{

    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }
}
