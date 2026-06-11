package com.example.entity.VO.admin;

import com.example.entity.VO.other.TeacherCSelectionVO;
import lombok.Data;
import java.util.List;

@Data
public class AdminCSelectionVO extends TeacherCSelectionVO {
    private Integer id;
    //课程id
    private Integer courseId;
    //最大选课人数
    private Integer number;
    //详细数据
    private List<AdminCSDetailsVO> csDetails;
}
