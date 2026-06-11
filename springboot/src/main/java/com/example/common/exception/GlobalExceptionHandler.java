package com.example.common.exception;

import com.example.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result businessExceptionHandler(HttpServletRequest request, BusinessException e) {
        logException(request, e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常 - 处理 @Valid 注解验证失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result methodArgumentNotValidExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException e) {
        logException(request, e);
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(BusinessErrorEnum.VALIDATION_FAILED.getCode(), message);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result bindExceptionHandler(HttpServletRequest request, BindException e) {
        logException(request, e);
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(BusinessErrorEnum.REQUEST_PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result missingServletRequestParameterExceptionHandler(HttpServletRequest request, MissingServletRequestParameterException e) {
        logException(request, e);
        String message = String.format("缺少必需参数: %s, 类型: %s", e.getParameterName(), e.getParameterType());
        return Result.error(BusinessErrorEnum.REQUEST_PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理请求体不可读异常（如JSON格式错误）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result httpMessageNotReadableExceptionHandler(HttpServletRequest request, HttpMessageNotReadableException e) {
        logException(request, e);
        return Result.error(BusinessErrorEnum.REQUEST_PARAM_ERROR.getCode(), "请求体格式错误");
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result runtimeExceptionHandler(HttpServletRequest request, RuntimeException e) {
        logException(request, e);
        return Result.error(BusinessErrorEnum.SYSTEM_ERROR.getCode(), "系统运行时错误");
    }

    /**
     * 处理一般异常
     */
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(HttpServletRequest request, Exception e) {
        logException(request, e);
        logger.error("系统未知异常", e);
        return Result.error(BusinessErrorEnum.SYSTEM_ERROR.getCode(), "系统内部错误");
    }

    /**
     * 记录异常日志
     */
    private void logException(HttpServletRequest request, Exception e) {
        logger.error("请求路径: {}, HTTP方法: {}, 异常类型: {}, 异常信息: {}",
                request.getRequestURI(),
                request.getMethod(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                e);
    }
}
