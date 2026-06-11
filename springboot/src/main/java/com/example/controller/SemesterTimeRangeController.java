package com.example.controller;

import com.example.common.Result;
import com.example.entity.SemesterTimeRange;
import com.example.service.SemesterTimeRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/semester/time")
public class SemesterTimeRangeController {
    @Autowired
    private SemesterTimeRangeService semesterTimeRangeService;
    
    @GetMapping("/current")
    public Result getCurrentTimeRange() {
        return Result.success(semesterTimeRangeService.getCurrentSemesterTimeRange());
    }
    
    @GetMapping("/{semester}")
    public Result getTimeRange(@PathVariable String semester) {
        return Result.success(semesterTimeRangeService.getSemesterTimeRange(semester));
    }
    
    @PostMapping("/set")
    public Result setTimeRange(@RequestBody SemesterTimeRange timeRange) {
        semesterTimeRangeService.setSemesterTimeRange(timeRange);
        return Result.success();
    }
    
    @GetMapping("/check/create/{semester}")
    public Result checkCreateTimeRange(@PathVariable String semester) {
        return Result.success(semesterTimeRangeService.isInCourseCreateTimeRange(semester));
    }
    
    @GetMapping("/check/select/{semester}")
    public Result checkSelectTimeRange(@PathVariable String semester) {
        return Result.success(semesterTimeRangeService.isInCourseSelectTimeRange(semester));
    }
    
    @GetMapping("/list")
    public Result getTimeRangeList() {
        return Result.success(semesterTimeRangeService.getTimeRangeList());
    }
}
