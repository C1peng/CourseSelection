package com.example.entity.VO;

import lombok.Data;

@Data
public class CourseTimetableVO {
    //课程名称
    private String courseName;
    //课程地点
    private String place;
    //周次类型
    private String cycle;
    //时间；星期、节次
    private Integer weekTime;
}
