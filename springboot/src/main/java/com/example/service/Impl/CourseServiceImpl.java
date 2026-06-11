package com.example.service.Impl;

import com.example.common.exception.BusinessErrorEnum;
import com.example.common.exception.BusinessException;
import com.example.entity.Course;
import com.example.entity.DTO.CoursePageQueryDTO;
import com.example.entity.VO.CourseVO;
import com.example.mapper.CourseMapper;
import com.example.mapper.DepartmentMapper;
import com.example.service.CourseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 分页查询课程信息
     */
    @Override
    public PageInfo<CourseVO> selectPage(CoursePageQueryDTO courseDTO) {
        PageHelper.startPage(courseDTO.getPageNum(), courseDTO.getPageSize());
        List<CourseVO> list = courseMapper.selectAll(courseDTO);
        return PageInfo.of(list);
    }
    /**
     * 添加课程信息，判断学院是否存在
     */
    public void add(Course course) {
        if (!departmentMapper.existsByDeptId(course.getDeptId())) {
            throw new BusinessException(BusinessErrorEnum.COLLEGE_NOT_EXIST);
        }
        if (courseMapper.existsByCourseName(course.getCourseName())) {
            throw new BusinessException(BusinessErrorEnum.DUPLICATE_NOT_DATA);
        }
        courseMapper.insert(course);
    }
    /**
     * 更新课程信息，判断学院是否存在
     */
    public void updateById(Course course) {
        //判断学院是否存在
        if (!departmentMapper.existsByDeptId(course.getDeptId())) {
            throw new BusinessException(BusinessErrorEnum.COLLEGE_NOT_EXIST);
        }
        if (!courseMapper.existsByCourseName(course.getCourseName())) {
            throw new BusinessException(BusinessErrorEnum.DUPLICATE_NOT_DATA);
        }
        courseMapper.updateById(course);
    }
    /**
     * 删除课程信息
     */
    public void deleteBatch(List<Integer> ids) {
        courseMapper.deleteBatch(ids);
    }
}
