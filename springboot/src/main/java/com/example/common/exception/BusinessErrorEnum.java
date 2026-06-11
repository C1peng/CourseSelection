package com.example.common.exception;

/**
 * 业务异常枚举
 */
public enum BusinessErrorEnum implements IBaseError {
    // 系统级异常：5xx系列
    SYSTEM_ERROR("500", "系统内部错误"),
    //客户端错误：4xx系列
    REQUEST_PARAM_ERROR("400", "请求参数错误"),
    UNAUTHORIZED("401", "未授权访问"),
    FORBIDDEN("403", "禁止访问"),
    RESOURCE_NOT_FOUND("404", "资源不存在"),

    // 业务级异常
    USER_EXIST("1000", "用户名已存在"),
    USER_NOT_EXIST("10001", "用户不存在"),
    PASSWORD_ERROR("10002", "密码错误"),
    TOKEN_INVALID("10003", "无效token"),
    PARAM_PASSWORD("10004", "原密码错误"),

    DUPLICATE_NOT_DATA("10100", "数据不存在"),
    DUPLICATE_DATA("10101", "数据已存在"),
    COLLEGE_NOT_EXIST("10102", "学院编号不存在"),
    COURSE_EXIST("10103", "存在信息"),
    VALIDATION_FAILED("10104", "数据验证失败"),
    BUSINESS_OPERATION_FAILED("10105", "业务操作失败");

    private final String code;
    private final String message;

    BusinessErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
