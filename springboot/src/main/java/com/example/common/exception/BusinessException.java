package com.example.common.exception;

/**
 * 基础业务异常类
 */
public class BusinessException extends RuntimeException {
    private String code;
    private Object[] args; // 用于国际化消息参数

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String code, String message, Object... args) {
        super(message);
        this.code = code;
        this.args = args;
    }

    public BusinessException(IBaseError error) {
        super(error.getMessage());
        this.code = error.getCode();
    }

    public BusinessException(IBaseError error, String message) {
        super(message);
        this.code = error.getCode();
    }

    public BusinessException(IBaseError error, Object... args) {
        super(error.getMessage());
        this.code = error.getCode();
        this.args = args;
    }

    public String getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }
}
