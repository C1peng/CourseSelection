package com.example.entity.BO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StSType {
    private Integer id;
    //课程状态
    private Integer courseStatus;
    //课程名称
    private String courseName;
    //学期(如：2024-2025学年第一学期)
    private String semester;
    //学分类型
    private String creditsType;
    //课程学分
    private Integer credits;

    //教师姓名
    private String teacherName;
    //学生姓名
    private String studentName;

    //平时学分
    private BigDecimal ordinaryCredit;
    //考试学分
    private BigDecimal examCredit;
    //总学分
    private BigDecimal totalCredit;
    //状态  通过/未通过（1/0）
    private Integer status;
}
