package com.example.entity.VO.other;

import com.example.entity.VO.CSelectionVO;
import lombok.Data;

@Data
public class TeacherCSelectionVO extends CSelectionVO {
    private Integer id;
    //课程id
    private Integer courseId;
    //最大选课人数
    private Integer number;
}
