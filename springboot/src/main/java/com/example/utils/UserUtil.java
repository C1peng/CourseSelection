package com.example.utils;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.entity.Permission;
import com.example.entity.Role;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class UserUtil {

    /**
     * 生成JWT token
     */
    public static String createToken(String data, String sign) {
        // audience是存储数据的一个媒介  存储用户ID和用户的角色  1-ADMIN
        return JWT.create().withAudience(data)
                .withExpiresAt(DateUtil.offsetDay(new Date(), 1)) // 设置过期时间1天后
                .sign(Algorithm.HMAC256(sign));
    }

    /**
     * 设置当前线程的用户信息
     */
    public static void setCurrentUser(Map<String, Object> map) {
        CurrentUserHolder.setCurrentUser(map);
    }

    /**
     * 获取当前线程的用户信息
     */
    public static Map<String, Object> getCurrentUser() {
        Map<String, Object> userMap = CurrentUserHolder.getCurrentUser();
        if (userMap == null) {
            return null;
        }
        return userMap;
    }

    /**
     * 获取当前用户ID
     */
    public static Integer getCurrentUserId() {
        Map<String, Object> userMap = getCurrentUser();
        if (userMap == null) {
            return null;
        }
        Object userId = userMap.get("userId");
        if (userId == null) {
            return null;
        }
        return Integer.valueOf(userId.toString());
    }

    /**
     * 获取当前线程的权限信息
     */
    public static Set<Permission> getCurrentUserPermissions() {
        Map<String, Object> userMap = CurrentUserHolder.getCurrentUser();
        if (userMap == null) {
            return null;
        }
        return (Set<Permission>) userMap.get("permissions");
    }
    /**
     * 获取当前线程的角色信息
     */
    public static List<Role> getCurrentUserRoles() {
        Map<String, Object> userMap = CurrentUserHolder.getCurrentUser();
        if (userMap == null) {
            return null;
        }
        return (List<Role>) userMap.get("roles");
    }
//    /**
//     * 判断用户是否有指定权限
//     */
//    public static boolean hasPermission(String permissionCode) {
//        List<Permission> permissions = getCurrentUserPermissions();
//        if (permissions == null || permissions.isEmpty()) {
//            return false;
//        }
//        return permissions.stream().anyMatch(p -> p.getPermissionCode().equals(permissionCode));
//    }

    /**
     * 清除当前线程的用户信息
     */
    public static void removeCurrentUser() {
        CurrentUserHolder.clear();
    }

    /**
     * 内部持有者类，用于存储当前用户信息
     */
    private static class CurrentUserHolder {
        private static final ThreadLocal<Map<String, Object>> USER_HOLDER = new ThreadLocal<>();
        /**
         * 获取当前线程的用户信息
         */
        public static Map<String, Object> getCurrentUser() {
            return USER_HOLDER.get();
        }
        /**
         * 设置当前线程的用户信息
         */
        public static void setCurrentUser(Map<String, Object> userMap) {
            USER_HOLDER.set(userMap);
        }
        /**
         * 清除当前线程的用户信息
         */
        public static void clear() {
            USER_HOLDER.remove();
        }
    }
}
