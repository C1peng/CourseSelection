package com.example.entity.DTO;

import lombok.Data;

@Data
public class TeacherPageQueryDTO {
    private Integer pageNum;
    private Integer pageSize;
    //序号
    private Integer id;
    //教师信息
    private String userInfo;
    //部门信息
    private String deptInfo;
}
