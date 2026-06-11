package com.example.common.exception;

/**
 * 资源不存在异常
 */
public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(BusinessErrorEnum.RESOURCE_NOT_FOUND.getCode(), message);
    }

    public ResourceNotFoundException(IBaseError error) {
        super(error);
    }
}
