package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.RoleEnum;
import com.example.common.exception.BusinessException;
import com.example.common.exception.ValidationException;
import com.example.common.exception.BusinessErrorEnum;
import com.example.entity.BO.Account;
import com.example.entity.DTO.AccountDTO;
import com.example.entity.VO.AccountVO;
import com.example.entity.VO.CourseTimetableVO;
import com.example.service.AdminService;
import com.example.service.StudentService;
import com.example.service.TeacherService;
import com.example.utils.UserUtil;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.example.entity.VO.AccountInfoVO;

import java.util.Map;


@RestController
@Slf4j
public class WebController {

    @Resource
    private AdminService adminService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private StudentService studentService;
    /**
     * 默认请求接口
     */
    @GetMapping("/")
    public Result hello() {
        return Result.success();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody AccountDTO accountDTO) {
        log.info("login: {}", accountDTO.getUsername());
        Account account = null;
        if (RoleEnum.ADMIN.name().equals(accountDTO.getRole())) {
            account = adminService.login(accountDTO);
        }
        else if (RoleEnum.TEACHER.name().equals(accountDTO.getRole())) {
            account = teacherService.login(accountDTO);
        } else if (RoleEnum.STUDENT.name().equals(accountDTO.getRole())) {
            account = studentService.login(accountDTO);
        } else {
            throw new ValidationException("角色错误");
        }
        //TODO 后续改进把权限相关数据存储到redis中
        // 获取用户的所有角色
//        List<Role> userRoles = roleService.getRolesByUserIdAndType(
//                account.getId(), accountDTO.getRole());
//        // 获取用户的所有权限
//        List<Permission> userPermissions = roleService.getPermissionsByUserIdAndType(
//                account.getId(), accountDTO.getRole());
//
//        // 将用户信息存储到ThreadLocal中，供后续业务逻辑使用
//        UserUtil.setCurrentUser(Map.of(
//                "userId", account.getId(),
//                "role", accountDTO.getRole(),
//                "username", account.getUsername(),
//                "roles", userRoles,
//                "permissions", userPermissions
//        ));
        // 生成token
        String token = UserUtil.createToken(account.getId() + "-" + accountDTO.getRole(), account.getPassword());
        AccountVO accountVO = new AccountVO();
        accountVO.setId(account.getId());
        accountVO.setUsername(account.getUsername());
        accountVO.setToken(token);
        accountVO.setAvatar(account.getAvatar());
        return Result.success(accountVO);
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody AccountDTO accountDTO) {
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
        }
        if (RoleEnum.ADMIN.name().equals(currentUser.get("role"))) {
            adminService.updatePassword(accountDTO);
        } else if (RoleEnum.TEACHER.name().equals(currentUser.get("role"))) {
            teacherService.updatePassword(accountDTO);
        } else if (RoleEnum.STUDENT.name().equals(currentUser.get("role"))) {
            studentService.updatePassword(accountDTO);
        } else {
            throw new ValidationException("角色错误");
        }
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result info() {
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
        }
        AccountInfoVO infoVO = new AccountInfoVO();
        if (RoleEnum.ADMIN.name().equals(currentUser.get("role"))) {
            BeanUtils.copyProperties(adminService.selectById((Integer) currentUser.get("userId")), infoVO);
        } else if (RoleEnum.TEACHER.name().equals(currentUser.get("role"))) {
            BeanUtils.copyProperties(teacherService.selectById((Integer) currentUser.get("userId")), infoVO);
        } else if (RoleEnum.STUDENT.name().equals(currentUser.get("role"))) {
            BeanUtils.copyProperties(studentService.selectById((Integer) currentUser.get("userId")), infoVO);
        }
        //Todo
        infoVO.setRole((String) currentUser.get("role"));
        return Result.success(infoVO);
    }

    /**
     * 查询课程时间表
     */
    @GetMapping("/courseTimetable")
    public Result courseTimetable(@RequestParam("semester") String semester) {
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
        }
        log.info("查询课程时间表");
        PageInfo<CourseTimetableVO> pageInfo = null;
        if (RoleEnum.TEACHER.name().equals(currentUser.get("role"))) {
            pageInfo = teacherService.courseTimetable(semester, (Integer) currentUser.get("userId"));
        } else if (RoleEnum.STUDENT.name().equals(currentUser.get("role"))) {
            pageInfo = studentService.courseTimetable(semester, (Integer) currentUser.get("userId"));
        }

        return Result.success(pageInfo);
    }
}
