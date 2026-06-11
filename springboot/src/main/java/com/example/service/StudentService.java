package com.example.service;

import com.example.entity.BO.Account;
import com.example.entity.DTO.AccountDTO;
import com.example.entity.DTO.CSelectionPageQueryDTO;
import com.example.entity.DTO.StudentPageQueryDTO;
import com.example.entity.Student;
import com.example.entity.VO.CourseTimetableVO;
import com.example.entity.VO.StudentVO;
import com.example.entity.VO.other.StudentCSelectionVO;
import com.example.entity.DTO.StSelectionDTO;
import com.example.entity.VO.other.StudentCourseVO;
import com.example.entity.VO.other.StudentSCourseVO;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface StudentService {
    PageInfo<StudentVO> selectPage(StudentPageQueryDTO studentDTO);

    PageInfo<StudentCSelectionVO> selectSCourseInfo(CSelectionPageQueryDTO cSelectionDTO);

    PageInfo<StudentSCourseVO> selectSCDetailInfo(String semester);

    PageInfo<StudentCourseVO> selectSCourseLine(Integer studentId);

    PageInfo<CourseTimetableVO> courseTimetable(String semester, Integer studentId);

    void addSCourse(StSelectionDTO stSelectionDTO);

    void dropSCourse(Integer courseId);

    Student selectById(Integer id);

    void add(Student student);

    void updateById(Student student);

    void updatePassword(AccountDTO accountDTO);

    void deleteBatch(List<Integer> ids);

    Account login(AccountDTO accountDTO);

    List<Integer> getAllStudentIds();

    String calculateCreditGap(Integer studentId);

}
