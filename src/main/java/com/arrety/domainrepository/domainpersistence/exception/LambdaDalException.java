package com.arrety.domainrepository.domainpersistence.exception;

/**
 * @author arrety
 * @date 2022/2/8 17:42
 */
public class LambdaDalException extends RuntimeException{

    public LambdaDalException(String message) {
        super(message);
    }

    public LambdaDalException(String message, Throwable cause) {
        super(message, cause);
    }

    public LambdaDalException(Throwable cause) {
        super(cause);
    }
}
