package com.example.entity;

import cn.hutool.core.date.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentSelection {
    private Integer id;
    //学生编号
    private Integer studentId;
    //学生名
    private String studentName;
    //课程编号
    private Integer scourseId;
    //平时学分
    private BigDecimal ordinaryCredit;
    //考试学分
    private BigDecimal examCredit;
    //总学分
    private BigDecimal totalCredit;
    //状态  通过/未通过（1/0）
    private Integer status;
    //时间
    private DateTime updateTime;
}
