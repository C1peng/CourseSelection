package com.example.entity.DTO;

import lombok.Data;

@Data
public class StSelectionDTO {
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 课程id
     */
    private Integer courseId;
    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 学生姓名
     */
    private String studentName;
}
