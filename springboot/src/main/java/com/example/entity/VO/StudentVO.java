package com.example.entity.VO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StudentVO {
    private Integer id;
    //账号
    private String username;
    //头像
    private String avatar;
    //姓名
    private String name;
    //性别
    private Integer gender;
    //手机号
    private String phone;
    //邮箱
    private String email;
    // 所属部门编号
    private String deptId;
    // 所属部门
    private String deptName;
    // 学业相关信息
    private String grade;             // 年级（入学年份）
    private Integer sort;// 排序(1.大一 2.大二 3.大三 4.大四)
    private BigDecimal publicCredits;  // 公共学分
    private BigDecimal foreignLanguageCredits; // 外语学分
    private BigDecimal sportCredits; // 体育学分
    private BigDecimal artCredits; // 艺术类学分
    // 状态信息
    private Integer status;           // 在读状态（0：在读，1：休学，2：离校）
}
