package com.example.controller;

import com.example.common.Result;
import com.example.entity.DTO.CSelectionPageQueryDTO;
import com.example.entity.Teacher;
import com.example.entity.DTO.TeacherPageQueryDTO;
import com.example.entity.VO.TeacherVO;
import com.example.entity.VO.other.TeacherCSDetailsVO;
import com.example.entity.VO.other.TeacherCSelectionVO;
import com.example.service.TeacherService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前端请求接口
 */
@RestController
@RequestMapping("/teacher")
@Slf4j
public class TeacherController {

    @Autowired
    private TeacherService teacherService;
    /**
     * 其它端
     * 分页查询基础列表
     */
    @PostMapping("/selectInfo")
    public Result selectInfo(@RequestBody CSelectionPageQueryDTO cSelectionDTO) {
        log.info("查询课程列表{}", cSelectionDTO.toString());
        PageInfo<TeacherCSelectionVO> pageInfo = teacherService.selectTCourseInfo(cSelectionDTO);
        return Result.success(pageInfo);
    }
    /**
     * 其它端
     * 分页查询详细列表
     */
    @PostMapping("/selectDetailInfo")
    public Result selectDetailInfo(@RequestBody CSelectionPageQueryDTO cSelectionDTO) {
        log.info("查询详细课程列表{}", cSelectionDTO.toString());
        PageInfo<TeacherCSDetailsVO> pageInfo = teacherService.selectTCDetailInfo(cSelectionDTO);
        return Result.success(pageInfo);
    }
    /**
     * 新增
     */
    @PostMapping("/add")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_ADD_CODE})
    public Result add(@RequestBody Teacher teacher) {
        log.info("teacher:{}", teacher.toString());
        teacherService.add(teacher);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_EDIT_CODE, PermissionEnum.TEACHER_EDIT_CODE})
    public Result update(@RequestBody Teacher teacher) {
        log.info("更新教师信息,教师id:{}", teacher.toString());
        teacherService.updateById(teacher);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_DELETE_CODE})
    public Result delete(@RequestBody List<Integer> ids) {
        teacherService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 分页查询
     */
    @PostMapping("/selectPage")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_VIEW_CODE, PermissionEnum.TEACHER_VIEW_CODE, PermissionEnum.STUDENT_VIEW_CODE})
    public Result selectPage(@RequestBody TeacherPageQueryDTO teacherDTO) {
        log.info("查询参数：{}", teacherDTO.toString());
        PageInfo<TeacherVO> pageInfo = teacherService.selectPage(teacherDTO);
        return Result.success(pageInfo);
    }
}