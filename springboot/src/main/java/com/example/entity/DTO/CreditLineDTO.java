package com.example.entity.DTO;

import com.example.entity.BO.CreditType;
import lombok.Data;

import java.util.List;

@Data
public class CreditLineDTO {
    private String deptId;//学院编号
    private String grade;// 年级
    private List<CreditType> creditTypes;
}
