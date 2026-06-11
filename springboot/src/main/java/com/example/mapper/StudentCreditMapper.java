package com.example.mapper;


import com.example.entity.BO.StSType;
import com.example.entity.DTO.CSelectionPageQueryDTO;
import com.example.entity.DTO.StSelectionPageQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StudentCreditMapper {

    List<StSType> selectSCourseLine(Integer studentId);

    /**
     * 查询学生学分信息
     */
    List<StSType> selectStudent(StSelectionPageQueryDTO sSelectionDTO);

    /**
     * 更新学生学分信息
     */
    void updateStudentCredit(List<StSType> stSTypeList);

     /**
      * 删除学生学分信息
      */
    void deleteStudent(List<Integer> ids);

    /**
     * 根据学生选课记录ID获取对应的课程ID
     */
    List<Integer> selectCourseIdsByIds(List<Integer> ids);
}
