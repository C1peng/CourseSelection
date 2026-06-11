package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student{
    private Integer id;
    //账号
    private String username;
    //密码
    private String password;
    //盐值
    private String salt;
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

    //学院编号
    private String deptId;
    // 学业相关信息
    private String grade;             // 年级（入学年份）
    private Integer sort;// 排序(1.大一 2.大二 3.大三 4.大四)
    private BigDecimal publicCredits;  // 公共学分
    private BigDecimal foreignLanguageCredits; // 外语学分
    private BigDecimal sportCredits; // 体育学分
    private BigDecimal artCredits; // 艺术类学分
    // 状态信息
    private Integer status;            // 在读状态（0：在读，1：休学，2：离校）
    // 时间信息
//    private LocalDate enrollmentDate;  // 入学时间
//    private LocalDate expectedGraduationDate; // 预计毕业时间

    // 关联信息
//    private String classId;            // 班级编号
//    private Integer advisorId;         // 辅导员ID（关联Teacher类）

}
