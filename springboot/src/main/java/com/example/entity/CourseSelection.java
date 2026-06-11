package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSelection {
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
    //状态(0未开始，1进行中，2已结课，3特殊状态)
    private Integer status;
    //学期(如：2024-2025学年第一学期)
    private String semester;
    //二进制：5位表星期，12位表课次
    private Long courseTime;
    //课程总计
    private Integer courseTotal;
    //二进制：20位表周次
    private Long courseNumber;
    //创建时间
    private LocalDateTime createdTime;
}
