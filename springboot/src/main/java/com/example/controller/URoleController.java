package com.example.controller;

import com.example.common.Result;
import com.example.entity.BO.PageQuery;
import com.example.entity.UserRole;
import com.example.entity.VO.UserRoleVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.service.URoleService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/u_role")
public class URoleController {
    @Autowired
    private URoleService roleService;

    /**
     * 获取所有用户-角色关联数据
     */
    @PostMapping("/userInfo")
    public Result selectUserInfo(@RequestBody PageQuery pageQuery) {
        try {
            PageInfo<UserRoleVO> pageInfo = roleService.getAllUserInfo(pageQuery);
            return Result.success(pageInfo);
        } catch (Exception e) {
            log.error("获取用户-角色关联数据失败", e);
            return Result.error("获取用户-角色关联数据失败");
        }
    }

    @PutMapping("/update")
    public Result update(@RequestBody UserRole userRole) {
        roleService.updateById(userRole);
        return Result.success();
    }

    @PostMapping("/add")
    public Result add(@RequestBody  List<UserRole> userRoles) {
        roleService.addBatch(userRoles);
        return Result.success();
    }
    @DeleteMapping("/delete")
    public Result delete(@RequestBody List<Integer> ids) {
        roleService.deleteBatch(ids);
        return Result.success();
    }


}
