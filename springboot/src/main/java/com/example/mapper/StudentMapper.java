package com.example.mapper;

import com.example.entity.BO.Account;
import com.example.entity.DTO.StudentPageQueryDTO;
import com.example.entity.Student;
import com.example.entity.VO.StudentVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StudentMapper {
    /**
     * 查询所有学生信息
     * @param
     * @return
     */
    List<StudentVO> selectAll(StudentPageQueryDTO studentDTO);

    /**
     * 根据ID查询学生信息
     * @param id
     * @return
     */
    @Select("select * from `student` where id = #{id}")
    Student selectById(Integer id);

    /**
     * 根据用户名查询学生信息
     * @param userName
     * @return
     */
    @Select("select id, username, password, salt, avatar from `student` where username = #{username}")
    Account select(String userName);

    /**
     * 新增学生信息
     * @param student
     * @return
     */
    Integer insert(Student student);
    /**
     * 更新学生信息
     * @param student
     */
    void updateById(Student student);
    /**
     * 删除学生信息
     * @param ids
     */
    void deleteBatch(List<Integer> ids);

//验证判断
    /**
     * 根据用户名判断管理员是否存在
     * @param userName
     * @return
     */
    boolean existsByUserName(String userName);

    /**
     * 获取所有学生ID
     * @return
     */
    @Select("select id from `student`")
    List<Integer> selectAllIds();

}
