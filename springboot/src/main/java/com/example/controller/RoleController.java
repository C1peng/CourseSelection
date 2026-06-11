package com.example.controller;

import com.example.common.Result;
import com.example.common.annotation.RequiresRoles;
import com.example.common.enums.Logical;
import com.example.common.enums.RoleEnum;
import com.example.entity.Role;
import com.example.entity.BO.PageQuery;
import com.example.entity.VO.RoleVO;
import com.example.service.RoleService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
@Slf4j
@RequiresRoles(value = {RoleEnum.ADMIN}, logical = Logical.OR)
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询角色列表（包含权限信息）
     */
    @PostMapping("/selectPage")
    public Result selectPage(@RequestBody PageQuery pageQuery) {
        try {
            PageInfo<RoleVO> roleInfo = roleService.selectPage(pageQuery);
            return Result.success(roleInfo);
        } catch (Exception e) {
            log.error("查询角色列表（包含权限信息）失败", e);
            return Result.error("查询角色列表失败");
        }
    }



    /**
     * 新增角色
     */
    @PostMapping("/add")
    public Result add(@RequestBody Role role) {
        try {
            roleService.insert(role);
            return Result.success();
        } catch (Exception e) {
            log.error("新增角色失败", e);
            return Result.error("新增角色失败");
        }
    }

    /**
     * 更新角色
     */
    @PutMapping("/update")
    public Result update(@RequestBody Role role) {
        try {
            roleService.update(role);
            return Result.success();
        } catch (Exception e) {
            log.error("更新角色失败", e);
            return Result.error("更新角色失败");
        }
    }

    /**
     * 批量删除角色
     */
    @DeleteMapping("/delete")
    public Result delete(@RequestBody List<Integer> ids) {
        try {
            roleService.deleteBatch(ids);
            return Result.success();
        } catch (Exception e) {
            log.error("删除角色失败", e);
            return Result.error("删除角色失败");
        }
    }
    /**
     * 获取权限树数据
     */
    @GetMapping("/tree")
    public Result selectPermissionTree() {
        try {
            List<Map<String, Object>> tree = roleService.selectPermissionTree();
            return Result.success(tree);
        } catch (Exception e) {
            log.error("获取权限树数据失败", e);
            return Result.error("获取权限树数据失败");
        }
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/assignPermissions")
    public Result assignPermissions(@RequestBody Map<String, Object> params) {
        try {
              roleService.assignPermissions(params);
            return Result.success();
        } catch (Exception e) {
            log.error("为角色分配权限失败", e);
            return Result.error("为角色分配权限失败");
        }
    }


}