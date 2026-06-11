package com.example.controller;

import com.example.common.Result;
import com.example.common.annotation.RequiresRoles;
import com.example.common.enums.Logical;
import com.example.common.enums.RoleEnum;
import com.example.entity.DTO.DepartmentPageQueryDTO;
import com.example.entity.Department;
import com.example.service.DepartmentService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dept")
@Slf4j
@RequiresRoles(value = {RoleEnum.ADMIN}, logical = Logical.OR)
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 分页查询
     */
    @PostMapping("/selectPage")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_VIEW_CODE})
    public Result selectPage(@RequestBody DepartmentPageQueryDTO deptDTO) {
        log.info("查询课程列表{}", deptDTO.toString());
        PageInfo<Department> pageInfo = departmentService.selectPage(deptDTO);
        return Result.success(pageInfo);
    }

    @PutMapping("/update")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_EDIT_CODE})
    public Result update(@RequestBody Department department) {
        log.info("更新学院信息,学院id:{}", department.toString());
        departmentService.updateById(department);
        return Result.success();
    }

    @PostMapping("/add")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_ADD_CODE})
    public Result addCourse(@RequestBody Department department) {
        log.info("添加学院信息");
        departmentService.add(department);
        return Result.success();
    }


    @DeleteMapping("/delete")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_DELETE_CODE})
    public Result delete(@RequestBody List<Integer> ids) {
        log.info("删除学院信息");
        departmentService.deleteBatch(ids);
        return Result.success();
    }
}
