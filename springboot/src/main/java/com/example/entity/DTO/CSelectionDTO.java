package com.example.entity.DTO;

import lombok.Data;

@Data
//基础的选课信息
public class CSelectionDTO {
    //课程编号
    private Integer courseId;
    //分数类型
    private String creditsType;
    //最大选课人数
    private Integer number;
    //状态（默认值：3）
    private Integer status;
}
