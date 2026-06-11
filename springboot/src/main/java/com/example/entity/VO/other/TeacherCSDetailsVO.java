package com.example.entity.VO.other;

import com.example.entity.VO.CSDetailsVO;
import lombok.Data;

@Data
public class TeacherCSDetailsVO extends CSDetailsVO {
    private Integer courseId;
    //状态(0未开始，1进行中，2已结束，3特殊状态)
    private Integer status;
    //学期(如：2024-2025学年第一学期)
    private String semester;
}
