package com.example.entity.VO.other;

import com.example.entity.VO.CSelectionVO;
import lombok.Data;

import java.util.List;

@Data
public class StudentCSelectionVO extends CSelectionVO {
    //课程分数
    private Integer credits;
    //详细数据
    private List<StudentCSDetailsVO> csDetails;
}
