package com.example.entity.DTO;

import lombok.Data;

@Data
public class CSelectionPageQueryDTO {
    private Integer pageNum;
    private Integer pageSize;
    //课程编号
    private Integer courseId;
    //课程名称
    private String courseName;
    //分数类型
    private String creditsType;
    //教师编号
    private Integer teacherId;
    //教师名
    private String teacherName;
    //学期
    private String semester;
    //状态
    private Integer status;
}
