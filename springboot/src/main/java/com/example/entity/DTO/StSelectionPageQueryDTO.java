package com.example.entity.DTO;

import lombok.Data;

@Data
public class StSelectionPageQueryDTO {
    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 教师id
     */
    private Integer teacherId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 学生姓名
     */
    private String studentName;
}
