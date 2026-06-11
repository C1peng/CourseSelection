package com.example.common.aop;

import com.example.common.enums.Logical;
import com.example.common.annotation.RequiresPermissions;
import com.example.common.annotation.RequiresRoles;
import com.example.common.enums.RoleEnum;
import com.example.common.exception.BusinessErrorEnum;
import com.example.common.exception.BusinessException;
import com.example.utils.UserUtil;
import com.example.utils.PermissionChecker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private PermissionChecker permissionChecker;

    @Around("@annotation(requiresPermissions)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermissions requiresPermissions) throws Throwable {
        // 获取当前用户
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
        }

        // 获取所需权限
        String[] permissions = requiresPermissions.value();
        if (permissions.length == 0) {
            // 如果没有指定权限，则允许访问
            return joinPoint.proceed();
        }

        // 直接使用注解中的Logical枚举，无需转换
        Logical logical = requiresPermissions.logical();

        // 检查权限
        boolean hasPermission = permissionChecker.hasPermissions(
                permissions,
                logical
        );

        if (!hasPermission) {
            throw new BusinessException(BusinessErrorEnum.UNAUTHORIZED);
        }

        return joinPoint.proceed();
    }

    @Around("@annotation(requiresRoles)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequiresRoles requiresRoles) throws Throwable {
        // 获取当前用户
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
        }
        // 获取所需角色
        RoleEnum[] roles = requiresRoles.value();
        if (roles.length == 0) {
            // 如果没有指定角色，则允许访问
            return joinPoint.proceed();
        }
        // 直接使用注解中的Logical枚举，无需转换
        Logical logical = requiresRoles.logical();
        // 检查角色
        boolean hasRole = permissionChecker.hasRoles(
                roles,
                logical
        );
        if (!hasRole) {
            throw new BusinessException(BusinessErrorEnum.VALIDATION_FAILED);
        }
        return joinPoint.proceed();
    }

    /**
     * 拦截类级别的 @RequiresRoles 注解
     */
    @Around("@within(requiresRoles)")
    public Object checkRoleOnClass(ProceedingJoinPoint joinPoint, RequiresRoles requiresRoles) throws Throwable {
        // 获取当前用户
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
        }
        // 获取类上注解的所需角色
        RoleEnum[] roles = requiresRoles.value();
        if (roles.length == 0) {
            // 如果没有指定角色，则允许访问
            return joinPoint.proceed();
        }
        // 直接使用注解中的Logical枚义，无需转换
        Logical logical = requiresRoles.logical();
        // 检查角色
        boolean hasRole = permissionChecker.hasRoles(
                roles,
                logical
        );
        if (!hasRole) {
            throw new BusinessException(BusinessErrorEnum.UNAUTHORIZED);
        }

        return joinPoint.proceed();
    }
}
