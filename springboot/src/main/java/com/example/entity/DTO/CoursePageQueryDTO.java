package com.example.entity.DTO;

import lombok.Data;

@Data
public class CoursePageQueryDTO {
    private Integer pageNum;
    private Integer pageSize;
    //课程名称
    private String courseInfo;
    //课程类型
    private Integer courseType;
    //课程状态
    private Integer status;
}
