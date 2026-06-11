package com.example.entity.VO.other;

import lombok.Data;

@Data
public class StudentSCourseVO {
    private Integer id;
    //教师名称
    private String teacherName;
    //课程名称
    private String courseName;
    //学分类型
    private String creditsType;
    //课程学分
    private Integer credits;
    //课程地点
    private String place;
    //课程次数
    private Integer courseTotal;
    //周次类型
    private String cycle;
    //时间；星期、节次
    private Integer weekTime;
}
