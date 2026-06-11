package com.example.mapper;

import com.example.entity.Course;
import com.example.entity.DTO.CoursePageQueryDTO;
import com.example.entity.VO.CourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CourseMapper {
    /**
     * 查询课程列表
     *
     * @param courseDTO
     * @return
     */
    List<CourseVO> selectAll(CoursePageQueryDTO courseDTO);

    /**
     * 新增课程信息
     *
     * @param course
     * @return
     */
    boolean insert(Course course);
    /**
     * 更新课程信息
     *
     * @param course
     * @return
     */
    boolean updateById(Course course);
    /**
     * 批量删除课程信息
     *
     * @param ids
     * @return
     */
    boolean deleteBatch(List<Integer> ids);
//验证判断
    /**
     * 根据课程名称判断课程是否存在
     * @param courseName
     * @return
     */
    boolean existsByCourseName(String courseName);
}
