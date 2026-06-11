package com.example.entity.DTO;

import lombok.Data;

@Data
public class CreditLinePageQueryDTO {
    private Integer pageNum;
    private Integer pageSize;
    private String deptInfo;//学院编号
    private String grade;// 年级
}
