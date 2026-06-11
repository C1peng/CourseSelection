package com.example.utils;

import com.example.common.enums.Logical;
import com.example.common.enums.RoleEnum;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限检查工具类 - 基于数据库存储的权限控制
 */
@Component
public class PermissionChecker {

    @Autowired
    private RoleService roleService;

    /**
     * 检查当前用户是否具有指定权限
     * @param requiredPermissions 需要的权限编码列表
     * @param logical 逻辑运算符（AND/OR）
     * @return 是否具有权限
     */
    public boolean hasPermissions(String[] requiredPermissions, Logical logical) {
        // 管理员直接放行
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser != null && "ADMIN".equals(currentUser.get("role"))) {
            return true;
        }

        // 获取当前用户信息
        Set<Permission> userPermissions = UserUtil.getCurrentUserPermissions();

        if (userPermissions == null || userPermissions.isEmpty()) {
            if (currentUser == null) {
                return false;
            }
            // 获取用户的所有权限
            userPermissions = roleService.getPermissionsByUserIdAndType((List<Role>) currentUser.get("roles"), (String) currentUser.get("role"));
            currentUser.put("permissions", userPermissions);
        }


        if (userPermissions == null || userPermissions.isEmpty()) {
            return false;
        }

        // 获取用户的权限编码集合
        List<String> userPermissionCodes = userPermissions.stream()
                .map(Permission::getPermissionCode)
                .toList();

        // 根据逻辑运算符检查权限
        if (logical == Logical.AND) {
            // 必须同时具有所有权限
            for (String requiredPermission : requiredPermissions) {
                if (!userPermissionCodes.contains(requiredPermission)) {
                    return false;
                }
            }
            return true;
        } else { // Logical.OR
            // 具有任何一个权限即可
            for (String requiredPermission : requiredPermissions) {
                if (userPermissionCodes.contains(requiredPermission)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 检查当前用户是否具有指定角色
     * @param requiredRoles 需要的角色编码列表
     * @param logical 逻辑运算符（AND/OR）
     * @return 是否具有角色
     */
    public boolean hasRoles(RoleEnum[] requiredRoles, Logical logical) {
        // 管理员直接放行
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser != null && "ADMIN".equals(currentUser.get("role"))) {
            return true;
        }

        // 获取当前用户信息
        List<Role> userRoles = UserUtil.getCurrentUserRoles();

        if (userRoles == null || userRoles.isEmpty()) {
            if (currentUser == null) {
                return false;
            }
            // 获取用户的所有角色
            userRoles = roleService.getRolesByUserIdAndType((Integer) currentUser.get("userId"), (String) currentUser.get("role"));
            currentUser.put("roles", userRoles);
        }
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }
        // 获取用户的角色编码集合
        List<String> userRoleCodes = userRoles.stream()
                .map(Role::getRoleCode)
                .toList();

        // 根据逻辑运算符检查角色
        if (logical == Logical.AND) {
            // 必须同时具有所有角色
            for (RoleEnum requiredRole : requiredRoles) {
                if (!userRoleCodes.contains(requiredRole.name())) {
                    return false;
                }
            }
            return true;
        } else { // Logical.OR
            // 具有任何一个角色即可
            for (RoleEnum requiredRole : requiredRoles) {
                if (userRoleCodes.contains(requiredRole.name())) {
                    return true;
                }
            }
            return false;
        }
    }
}