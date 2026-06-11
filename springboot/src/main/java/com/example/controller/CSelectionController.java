package com.example.controller;

import com.example.common.Result;
import com.example.entity.BO.StSType;
import com.example.entity.DTO.CSDetailsDTO;
import com.example.entity.DTO.CSelectionPageQueryDTO;
import com.example.entity.DTO.CSelectionDTO;
import com.example.entity.DTO.StSelectionPageQueryDTO;
import com.example.entity.VO.admin.AdminCSDetailsVO;
import com.example.entity.VO.StSelectionVO;
import com.example.entity.VO.admin.AdminCSelectionVO;
import com.example.service.CSelectionService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/c_select")
@Slf4j
public class CSelectionController {
    @Autowired
    private CSelectionService cSelectionService;
    /**
     * 管理端
     * 分页查询基础列表
     */
    @PostMapping("/selectPage")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_VIEW_CODE, PermissionEnum.TEACHER_VIEW_CODE})
    public Result selectPage(@RequestBody CSelectionPageQueryDTO cSelectionDTO) {
        log.info("查询课程列表{}", cSelectionDTO.toString());
        PageInfo<AdminCSelectionVO> pageInfo = cSelectionService.selectPage(cSelectionDTO);
        return Result.success(pageInfo);
    }
    /**
     * 管理端
     * 分页查询详细列表
     */
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_VIEW_CODE, PermissionEnum.TEACHER_VIEW_CODE, PermissionEnum.STUDENT_VIEW_CODE})
    @PostMapping("/selectDetailPage")
    public Result selectDetailPage(@RequestBody CSelectionPageQueryDTO cSelectionDTO) {
        log.info("查询详细课程列表{}", cSelectionDTO.toString());
        PageInfo<AdminCSDetailsVO> pageInfo = cSelectionService.selectDetailPage(cSelectionDTO);
        return Result.success(pageInfo);
    }

//    /**
//     * 管理端
//     * 查询课程时间表
//     */
////    @RequiresPermissions(value = {PermissionEnum.ADMIN_VIEW_CODE, PermissionEnum.TEACHER_VIEW_CODE, PermissionEnum.STUDENT_VIEW_CODE})
//    @PostMapping("/courseTimetable")
//    public Result courseTimetable(@RequestBody CSelectionPageQueryDTO cSelectionDTO) {
//        log.info("查询课程时间表{}", cSelectionDTO.toString());
//        PageInfo<CourseTimetableVO> pageInfo = cSelectionService.courseTimetable(cSelectionDTO);
//        return Result.success(pageInfo);
//    }


    /**
     * 删除课程选择信息
     */
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_DELETE_CODE})
    @DeleteMapping("/delete")
    public Result delete(@RequestBody List<Integer> ids) {
        log.info("删除课程选择信息");
        cSelectionService.deleteBatch(ids);
        return Result.success();
    }
//基础信息
     /**
     * 添加课程选择信息
     */
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_ADD_CODE})
    @PostMapping("/addBasic")
    public Result addBasic(@RequestBody CSelectionDTO courseSectionDTO) {
        log.info("添加课程选择信息");
        cSelectionService.addBasic(courseSectionDTO);
        return Result.success();
    }
    /**
     * 更新课程选择信息
     */
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_EDIT_CODE})
    @PostMapping("/updateBasic")
    public Result updateBasic(@RequestBody CSelectionDTO courseSectionDTO) {
        log.info("更新课程选择信息");
        cSelectionService.updateBasic(courseSectionDTO);
        return Result.success();
    }
//详细信息
    @PostMapping("/addDetails")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_ADD_CODE, PermissionEnum.TEACHER_ADD_CODE})
    public Result addDetails(@RequestBody CSDetailsDTO csDetailsDTO) {
        log.info("添加课程详细选择信息");
        log.info(csDetailsDTO.toString());
        cSelectionService.addDetails(csDetailsDTO);
        return Result.success();
    }

    @PostMapping("/updateDetails")
//    @RequiresPermissions(value = {PermissionEnum.ADMIN_EDIT_CODE, PermissionEnum.TEACHER_EDIT_CODE})
    public Result updateDetails(@RequestBody CSDetailsDTO csDetailsDTO) {
        log.info("更新课程详细选择信息");
        log.info(csDetailsDTO.toString());
        cSelectionService.updateDetails(csDetailsDTO);
        return Result.success();
    }

//  学生 -- 课程
@PostMapping("/selectStudent")
public Result selectStudent(@RequestBody StSelectionPageQueryDTO sSelectionDTO) {
    log.info("查询学生选课分数列表");
    PageInfo<StSelectionVO> pageInfo = cSelectionService.selectStudent(sSelectionDTO);
    return Result.success(pageInfo);
}

@PostMapping("/updateStudentCredit")
public Result updateStudentCredit(@RequestBody List<StSType> stSTypeList) {
    log.info("更新学生学分，共{}条记录", stSTypeList.size());
    cSelectionService.updateStudentCredit(stSTypeList);
    return Result.success();
}

@DeleteMapping("/deleteStudent")
public Result deleteStudent(@RequestBody List<Integer> ids) {
    log.info("删除学生选课记录{}", ids);
    cSelectionService.deleteStudent(ids);
    return Result.success();
}

/**
 * 更新课程状态
 */
@PostMapping("/updateStatus")
public Result updateStatus(@RequestBody Map<String, Object> params) {
    Integer id = (Integer) params.get("id");
    Integer status = (Integer) params.get("status");
    log.info("更新课程状态: id={}, status={}", id, status);
    cSelectionService.updateStatus(id, status);
    return Result.success();
}
}
