package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditLine {
    private Integer id;
    private String deptId;//学院编号
    private String grade;// 年级
    private Integer sort;// 排序(1.大一 2.大二 3.大三 4.大四)
    private BigDecimal publicCredit; // 公共选修课学分要求
    private BigDecimal foreignLanguageCredit; // 外语选修课学分要求
    private BigDecimal sportCredit; // 体育选修课学分要求
    private BigDecimal artCredit; // 艺术选修课学分要求
}
