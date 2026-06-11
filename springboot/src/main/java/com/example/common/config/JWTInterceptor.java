package com.example.common.config;

import cn.hutool.core.util.ObjectUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.common.Constants;
import com.example.common.enums.RoleEnum;
import com.example.common.exception.BusinessErrorEnum;
import com.example.common.exception.BusinessException;
import com.example.common.exception.ValidationException;
import com.example.entity.BO.Account;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.service.AdminService;
import com.example.service.RoleService;
import com.example.service.StudentService;
import com.example.service.TeacherService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.example.utils.UserUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * JWT拦截器
 */
@Component
public class JWTInterceptor implements HandlerInterceptor {

    @Resource
    private AdminService adminService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private StudentService studentService;
    @Resource
    private RoleService roleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        // 1. 从http请求标头里面拿到token
        String token = request.getHeader(Constants.TOKEN);
        if (ObjectUtil.isNull(token)) {
            throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
        }
        // 2. 开始执行认证
        Account account = new Account();
        try {
            String audience = JWT.decode(token).getAudience().get(0);
            String userId = audience.split("-")[0];
            String role = audience.split("-")[1];
            // 根据用户角色判断用户属于哪个数据库表 然后查询用户数据
            switch (RoleEnum.valueOf(role)) {
                case ADMIN ->{
                    BeanUtils.copyProperties(adminService.selectById(Integer.valueOf(userId)), account);
                    account.setRole(RoleEnum.ADMIN.name());
                }
                case TEACHER ->{
                    BeanUtils.copyProperties(teacherService.selectById(Integer.valueOf(userId)), account);
                    account.setRole(RoleEnum.TEACHER.name());
                }
                case STUDENT -> {
                    BeanUtils.copyProperties(studentService.selectById(Integer.valueOf(userId)), account);
                    account.setRole(RoleEnum.STUDENT.name());
                }
                default -> throw new ValidationException("用户角色不存在");
            }
        } catch (Exception e) {
            throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
        }
        if (ObjectUtil.isNull(account)) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_EXIST);
        }
        try {
            // 通过用户的密码作为密钥再次验证token的合法性
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token);  // 验证token
        } catch (JWTVerificationException e) {
            throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
        }
//        if(UserUtil.getCurrentUser() == null) {
            //TODO 后续改进把权限相关数据存储到readme中
            // 获取用户的所有角色
            List<Role> userRoles = roleService.getRolesByUserIdAndType(
                    account.getId(), account.getRole());

            // 获取用户的所有权限
            Set<Permission> userPermissions =
                    roleService.getPermissionsByUserIdAndType(userRoles, account.getRole());
            // 将用户信息存储到ThreadLocal中，供后续业务逻辑使用
            UserUtil.setCurrentUser(Map.of(
                    "userId", account.getId(),
                    "role", account.getRole(),
                    "username", account.getUsername(),
                    "roles", userRoles,
                    "permissions", userPermissions
            ));
//        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        // 请求完成后清理ThreadLocal，防止内存泄漏
        UserUtil.removeCurrentUser();
    }
}
