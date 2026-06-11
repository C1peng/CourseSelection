package com.example.service;

import com.example.entity.Course;
import com.example.entity.DTO.CoursePageQueryDTO;
import com.example.entity.VO.CourseVO;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface CourseService {
    PageInfo<CourseVO> selectPage(CoursePageQueryDTO courseDTO);

    void add(Course course);

    void updateById(Course course);

    void deleteBatch(List<Integer> ids);
}
