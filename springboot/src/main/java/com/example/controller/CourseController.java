package com.example.controller;

import com.example.common.Result;

import com.example.entity.Course;
import com.example.entity.DTO.CoursePageQueryDTO;
import com.example.entity.VO.CourseVO;
import com.example.service.CourseService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
@Slf4j
public class CourseController {
    @Autowired
    private CourseService courseService;

    /**
     * 分页查询
     */
    @PostMapping("/selectPage")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_VIEW_CODE, PermissionEnum.TEACHER_VIEW_CODE})
    public Result selectPage(@RequestBody CoursePageQueryDTO courseDTO) {
        log.info("查询课程列表{}", courseDTO.toString());

        PageInfo<CourseVO> pageInfo = courseService.selectPage(courseDTO);
        return Result.success(pageInfo);
    }

    @PostMapping("/add")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_ADD_CODE})
    public Result addCourse(@RequestBody Course course) {
        log.info("添加课程信息");
        courseService.add(course);
        return Result.success();
    }

    @PutMapping("/update")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_EDIT_CODE})
    public Result update(@RequestBody Course course) {
        log.info("更新课程信息,课程id:{}", course.toString());
        courseService.updateById(course);
        return Result.success();
    }

    @DeleteMapping("/delete")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_DELETE_CODE})
    public Result delete(@RequestBody List<Integer> ids) {
        log.info("删除课程信息");
        courseService.deleteBatch(ids);
        return Result.success();
    }
}
