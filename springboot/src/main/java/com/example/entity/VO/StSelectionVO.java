package com.example.entity.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class StSelectionVO {
    //课程名称
    private String courseName;
    
    //教师姓名
    private String teacherName;
    
    //课程状态
    private Integer courseStatus;

    //信息
    private List<StSInfo> stSInfo;

    @Data
    public static class StSInfo{
        private Integer id;
        //学生姓名
        private String studentName;
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
