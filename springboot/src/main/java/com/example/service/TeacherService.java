package com.example.service;

import com.example.entity.BO.Account;
import com.example.entity.DTO.AccountDTO;
import com.example.entity.DTO.CSelectionPageQueryDTO;
import com.example.entity.Teacher;
import com.example.entity.DTO.TeacherPageQueryDTO;
import com.example.entity.VO.CourseTimetableVO;
import com.example.entity.VO.TeacherVO;
import com.example.entity.VO.other.TeacherCSDetailsVO;
import com.example.entity.VO.other.TeacherCSelectionVO;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface TeacherService {
    PageInfo<TeacherVO> selectPage(TeacherPageQueryDTO teacherDTO);

    PageInfo<TeacherCSelectionVO> selectTCourseInfo(CSelectionPageQueryDTO cSelectionDTO);

    PageInfo<TeacherCSDetailsVO> selectTCDetailInfo(CSelectionPageQueryDTO cSelectionDTO);

    PageInfo<CourseTimetableVO> courseTimetable(String semester, Integer teacherId);

    Teacher selectById(Integer id);

    void add(Teacher teacher);

    void updateById(Teacher teacher);

    void updatePassword(AccountDTO accountDTO);

    void deleteBatch(List<Integer> ids);

    Account login(AccountDTO accountDTO);
}