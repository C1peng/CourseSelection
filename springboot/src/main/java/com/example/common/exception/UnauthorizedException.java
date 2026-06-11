package com.example.common.exception;

/**
 * 权限异常
 */
public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(BusinessErrorEnum.UNAUTHORIZED.getCode(), message);
    }

    public UnauthorizedException(IBaseError error) {
        super(error);
    }
}
