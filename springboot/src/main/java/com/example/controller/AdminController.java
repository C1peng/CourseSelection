package com.example.controller;

import com.example.common.Result;
import com.example.common.annotation.RequiresRoles;
import com.example.common.enums.Logical;
import com.example.common.enums.RoleEnum;
import com.example.entity.Admin;
import com.example.entity.DTO.AdminPageQueryDTO;
import com.example.entity.VO.AdminVO;
import com.example.service.AdminService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 前端请求接口
 */
@RestController
@RequestMapping("/admin")
@RequiresRoles(value = {RoleEnum.ADMIN}, logical = Logical.OR)
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 新增
     */
    @PostMapping("/add")
//    @RequiresPermissions(value = {"ALL:ADD"})
    public Result add(@RequestBody Admin admin) {
        adminService.add(admin);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_EDIT_CODE})
    public Result update(@RequestBody Admin admin) {
        adminService.updateById(admin);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_DELETE_CODE})
    public Result delete(@RequestBody List<Integer> ids) {
        adminService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 分页查询
     */
    @PostMapping("/selectPage")
//    @RequiresPermissions(value = {"ALL:SELECT"})
    public Result selectPage(@RequestBody AdminPageQueryDTO adminDTO) {
        PageInfo<AdminVO> pageInfo = adminService.selectPage(adminDTO);
        return Result.success(pageInfo);
    }

}
