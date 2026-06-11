package com.example.common.exception;

/**
 * 参数验证异常
 */
public class ValidationException extends BusinessException {
    public ValidationException(String message) {
        super(BusinessErrorEnum.REQUEST_PARAM_ERROR.getCode(), message);
    }

    public ValidationException(IBaseError error) {
        super(error);
    }

    public ValidationException(IBaseError error, Object... args) {
        super(error, args);
    }
}


