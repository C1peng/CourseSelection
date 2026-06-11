package com.example.entity.VO.admin;

import com.example.entity.VO.CSDetailsVO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminCSDetailsVO extends CSDetailsVO {
    //课程ID
    private Integer courseId;
    //授课教师ID
    private Integer teacherId;
    //教师名称
    private String teacherName;
    //状态(0未开始，1进行中，2已结束，3特殊状态)
    private Integer status;
    //学期(如：2024-2025学年第一学期)
    private String semester;
    //学分类型
    private String creditsType;
    //创建时间
    private LocalDateTime createdTime;
}
