package com.example.entity.VO.other;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class StudentCourseVO {
    //学期(如：2024-2025学年第一学期)
    private String semester;
    //信息
    private List<StudentLineInfo> SLInfo;
    @Data
    public static class StudentLineInfo{
        //课程名称
        private String courseName;
        //学分类型
        private String creditsType;
        //课程学分
        private Integer credits;
        //平时学分
        private BigDecimal ordinaryCredit;
        //考试学分
        private BigDecimal examCredit;
        //总学分
        private BigDecimal totalCredit;
        //状态  通过/未通过（1/0）
        private Integer status;
    }
}
