package com.example.controller;

import com.example.common.Result;
import com.example.common.annotation.RequiresRoles;
import com.example.common.enums.Logical;
import com.example.common.enums.RoleEnum;
import com.example.entity.Permission;
import com.example.entity.DTO.PermissionPageQueryDTO;
import com.example.service.PermissionService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/permission")
@Slf4j
@RequiresRoles(value = {RoleEnum.ADMIN}, logical = Logical.OR)
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 分页查询权限列表
     */
    @PostMapping("/selectPage")
    public Result selectPage(@RequestBody PermissionPageQueryDTO permissionPageQueryDTO) {
        try {
            PageInfo<Permission> pageInfo = permissionService.selectPage(permissionPageQueryDTO);
            return Result.success(pageInfo);
        } catch (Exception e) {
            log.error("查询权限列表失败", e);
            return Result.error("查询权限列表失败");
        }
    }

    /**
     * 新增权限
     */
    @PostMapping("/add")
    public Result add(@RequestBody Permission permission) {
        try {
            permissionService.insert(permission);
            return Result.success();
        } catch (Exception e) {
            log.error("新增权限失败", e);
            return Result.error("新增权限失败");
        }
    }

    /**
     * 更新权限
     */
    @PutMapping("/update")
    public Result update(@RequestBody Permission permission) {
        try {
            permissionService.update(permission);
            return Result.success();
        } catch (Exception e) {
            log.error("更新权限失败", e);
            return Result.error("更新权限失败");
        }
    }

    /**
     * 批量删除权限
     */
    @DeleteMapping("/delete")
    public Result delete(@RequestBody List<Integer> ids) {
        try {
            permissionService.deleteBatch(ids);
            return Result.success();
        } catch (Exception e) {
            log.error("删除权限失败", e);
            return Result.error("删除权限失败");
        }
    }

}