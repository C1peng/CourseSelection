package com.example.mapper;

import com.example.entity.BO.CSType;
import com.example.entity.CourseSelection;
import com.example.entity.DTO.CSelectionPageQueryDTO;
import com.example.entity.DTO.CSelectionDTO;
import com.example.entity.VO.other.StudentCSelectionVO;
import com.example.entity.VO.other.TeacherCSelectionVO;
import com.example.entity.VO.admin.AdminCSelectionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface CSelectionMapper {
    /**
     * 查询基础课程列表
     */
    List<AdminCSelectionVO> selectAll(CSelectionPageQueryDTO cSelectionDTO);
    
    /**
     * 查询基础课程列表
     */
    List<TeacherCSelectionVO> selectTCourseInfo(CSelectionPageQueryDTO cSelectionDTO);
    /**
     * 查询基础课程列表
     */
    List<StudentCSelectionVO> selectSCourseInfo(CSelectionPageQueryDTO cSelectionDTO);

    /**
     * 查询详细课程列表
     */
    List<CSType> selectDetail(CSelectionPageQueryDTO cSelectionDTO);

    /**
     * 查询学生详细课程列表
     */
    List<CSType> selectSDetailInfo(@Param("semester") String semester, @Param("studentId") Integer studentId);

    /**
     * 查询教师课程表
     */
    List<CSType> selectTeacherTimetable(@Param("semester") String semester, @Param("teacherId") Integer teacherId);

    /**
     * 查询学生课程表
     */
    List<CSType> selectStudentTimetable(@Param("semester") String semester, @Param("studentId") Integer studentId);

    /**
     * 查询课程详情
     */
    @Select("select * from course_section where id = #{id}")
    CourseSelection selectById(Integer id);
    /**
     * 新增课程选择
     */
    void addInfo(CSelectionDTO cSelectionDTO);
    /**
     * 修改课程选择
     */
    void updateInfo(CSelectionDTO cSelectionDTO);
    /**
     * 新增详细课程
     */
    void addDetails(CourseSelection courseSelection);
    /**
     * 修改详细课程
     */
    void updateDetails(CourseSelection courseSelection);
    /**
     * 删除课程选择
     */
    void deleteBatch(List<Integer> ids);

//验证判断
    /**
     * 查询课程是否存在
     */
    boolean ExistsById(Integer courseId);
    /**
     * 检查教师是否与课程部门匹配
     */
    boolean contrastTC(@Param("teacherId") Integer teacherId, @Param("courseId") Integer courseId);
    /**
     * 检查选课人数是否超过最大人数
     */
    Boolean checkNumber(@Param("courseId") Integer courseId, @Param("number") Integer number);
    /**
     * 检查教师是否与时间冲突
     */
     Integer selectId(@Param("teacherId") Integer teacherId, @Param("courseTime") Long courseTime, @Param("courseNumber") Long courseNumber,@Param("semester") String semester);

    // 学生选课相关方法
    /**
     * 检查课程是否存在
     */
    boolean checkCourseExists(Integer courseId);
    /**
     * 检查课程人数限制
     */
    boolean checkCourseCapacity(Integer courseId);
    /**
     * 检查学生是否已选该课程
     */
    boolean checkStudentCourse(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);
    /**
     * 检查时间冲突
     */
    Integer checkTimeConflict(@Param("studentId") Integer studentId, @Param("courseTime") Long courseTime, @Param("courseNumber") Long courseNumber, @Param("semester") String semester);

    /**
     * 检查学生是否已选过相同课程名的课程
     */
    boolean checkSameCourseName(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);
    /**
     * 插入学生选课记录
     */
    void insertStudentSelection(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId, @Param("studentName") String studentName);
    /**
     * 删除学生选课记录
     */
    void deleteStudentSelection(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);
    /**
     * 更新课程已选人数
     */
    void updateCourseSelectionCount(@Param("courseId") Integer courseId, @Param("count") Integer count);

    /**
     * 更新课程状态
     */
    void updateStatus(@Param("id") Integer id, @Param("status") Integer status);


}
