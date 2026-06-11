package com.example.controller;

import com.example.common.Result;
import com.example.entity.DTO.CSelectionPageQueryDTO;
import com.example.entity.DTO.StSelectionDTO;
import com.example.entity.DTO.StudentPageQueryDTO;
import com.example.entity.Student;
import com.example.entity.VO.StudentVO;
import com.example.entity.VO.other.StudentCSelectionVO;
import com.example.entity.VO.other.StudentCourseVO;
import com.example.entity.VO.other.StudentSCourseVO;
import com.example.service.StudentService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前端请求接口
 */
@RestController
@RequestMapping("/student")
@Slf4j
public class StudentController {

    @Autowired
    private StudentService studentService;
    /**
     * 管理端
     * 分页查询基础列表
     */
    @PostMapping("/selectSCourseInfo")
    public Result selectSCourseInfo(@RequestBody CSelectionPageQueryDTO cSelectionDTO) {
        log.info("查询课程列表{}", cSelectionDTO.toString());
        PageInfo<StudentCSelectionVO> pageInfo = studentService.selectSCourseInfo(cSelectionDTO);
        return Result.success(pageInfo);
    }
    /**
     * 分页查询详细列表
     */
    @GetMapping("/selectSCDetailInfo")
    public Result selectSCDetailInfo(@RequestParam("semester") String semester) {
        log.info("已选课程");
        PageInfo<StudentSCourseVO> pageInfo = studentService.selectSCDetailInfo(semester);
        return Result.success(pageInfo);
    }

    @GetMapping("/SCourseLine")
    public Result selectSCourseLine(@RequestParam("studentId") Integer studentId) {
        log.info("查询课程列表");
        PageInfo<StudentCourseVO> pageInfo = studentService.selectSCourseLine(studentId);
        return Result.success(pageInfo);
    }

    @PostMapping("/selectCourse")
    public Result selectCourse(@RequestBody StSelectionDTO stSelectionDTO) {
        log.info("学生选课{}", stSelectionDTO.toString());
        studentService.addSCourse(stSelectionDTO);
        return Result.success();
    }

    @PostMapping("/dropCourse")
    public Result dropCourse(@RequestBody StSelectionDTO stSelectionDTO) {
        log.info("学生退课{}", stSelectionDTO.toString());
        studentService.dropSCourse(stSelectionDTO.getCourseId());
        return Result.success();
    }

    //TODO 保留旧接口，后续可能删除
    @PostMapping("/addSCourse")
    public Result addSCourse(@RequestBody StSelectionDTO stSelectionDTO) {
        log.info("学生选课{}", stSelectionDTO.toString());
        studentService.addSCourse(stSelectionDTO);
        return Result.success();
    }

    /**
     * 新增
     */
    @PostMapping("/add")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_ADD_CODE})
    public Result add(@RequestBody Student student) {
        studentService.add(student);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_EDIT_CODE, PermissionEnum.STUDENT_EDIT_CODE})
    public Result update(@RequestBody Student student) {
        studentService.updateById(student);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_DELETE_CODE})
    public Result delete(@RequestBody List<Integer> ids) {
        studentService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 分页查询
     */
    @PostMapping("/selectPage")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_VIEW_CODE, PermissionEnum.STUDENT_VIEW_CODE, PermissionEnum.TEACHER_VIEW_CODE})
    public Result selectPage(@RequestBody StudentPageQueryDTO studentDTO) {
        PageInfo<StudentVO> pageInfo = studentService.selectPage(studentDTO);
        return Result.success(pageInfo);
    }
}
