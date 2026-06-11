package com.example.entity.VO;

import com.example.entity.BO.CreditType;
import lombok.Data;
import java.util.List;

@Data
public class CreditLineVO {
    private String deptId;//学院编号
    private String deptName;//学院名称
    private String grade;// 年级
    private List<CreditType> creditTypes;
}
