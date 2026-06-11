package com.example.entity.DTO;

import lombok.Data;

@Data
public class DepartmentPageQueryDTO {
    private Integer pageNum;
    private Integer pageSize;
    //学院编号
    private String deptId;
    //学院名称
    private String deptName;
}
