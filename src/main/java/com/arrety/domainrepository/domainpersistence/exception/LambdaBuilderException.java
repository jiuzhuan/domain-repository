package com.arrety.domainrepository.domainpersistence.exception;

/**
 * @author arrety
 * @date 2022/2/8 17:42
 */
public class LambdaBuilderException extends RuntimeException{

    public LambdaBuilderException(String message) {
        super(message);
    }

    public LambdaBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public LambdaBuilderException(Throwable cause) {
        super(cause);
    }
}
