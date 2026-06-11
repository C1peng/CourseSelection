package com.example.entity.VO;

import lombok.Data;

@Data
public class CSDetailsVO {
    private Integer id;
    //课程名称
    private String courseName;
    //课程地点
    private String place;
    //最大选课人数
    private Integer number;
    //当前选课人数
    private Integer currentStudents;
    //课程次数
    private Integer courseTotal;
    //周次类型
    private String cycle;
    //时间；星期、节次
    private Integer weekTime;
}
