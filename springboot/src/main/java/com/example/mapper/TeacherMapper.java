package com.example.mapper;

import com.example.entity.BO.Account;
import com.example.entity.Teacher;
import com.example.entity.DTO.TeacherPageQueryDTO;
import com.example.entity.VO.TeacherVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TeacherMapper {
    /**
     * 获取教师列表
     * @param teacherDTO
     * @return
     */
    List<TeacherVO> selectAll(TeacherPageQueryDTO teacherDTO);
    /**
     * 根据ID查询教师信息
     * @param id
     * @return
     */
    @Select("select * from `teacher` where id = #{id}")
    Teacher selectById(Integer id);
    /**
     * 根据用户名查询教师信息
     * @param userName
     * @return
     */
    @Select("select id, username, password, salt, avatar from `teacher` where username = #{username}")
    Account select(@Param("username") String userName);
     /**
     * 新增教师信息
     * @param teacher
     */
    void insert(Teacher teacher);
     /**
     * 更新教师信息
     * @param teacher
     * @return
     */
    boolean updateById(Teacher teacher);
     /**
     * 删除教师信息
     * @param ids
     */
    void deleteBatch(List<Integer> ids);

//验证判断
     /**
     * 根据ID判断教师是否存在
     * @param id
     * @return
     */
    boolean existsById(int id);
    /**
     * 根据用户名判断教师是否存在
     * @param userName
     * @return
     */
    boolean existsByUserName(String userName);


}
