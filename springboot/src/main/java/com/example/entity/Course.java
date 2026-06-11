package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Course {
    private Integer id;
    //课程编号
    private String courseId;
    //课程名称
    private String courseName;
    //课程学时
    private Integer hours;
    //课程学分
    private BigDecimal credits;
    //课程类型
    private Integer courseType;
    //学院编号
    private String deptId;
    //课程状态
    private Integer status;
}
