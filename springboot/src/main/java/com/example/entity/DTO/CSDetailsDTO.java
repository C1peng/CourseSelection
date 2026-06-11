package com.example.entity.DTO;

import lombok.Data;

@Data
//基础的选课信息
public class CSDetailsDTO {
    private Integer id;
    //课程编号
    private Integer courseId;
    //授课教师编号
    private Integer teacherId;
    //学分类型
    private String creditsType;
    //课程地点
    private String place;
    //最大选课人数
    private Integer number;
    //当前选课人数
    private Integer currentStudents;
    //状态(0未开始，1进行中，2已结束，3特殊状态)
    private Integer status;
    //学期(如：2024-2025学年第一学期)
    private String semester;
    //课程次数
    private Integer courseTotal;
    //周次类型
    private String cycle;
    //时间；星期、节次
    private Integer weekTime;
}
