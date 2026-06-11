package com.example.mapper;

import com.example.entity.Admin;
import com.example.entity.BO.Account;
import com.example.entity.DTO.AdminPageQueryDTO;
import com.example.entity.VO.AdminVO;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AdminMapper {
    /**
     * 查询管理员信息
     * @param adminDTO
     * @return
     */
    List<AdminVO> selectAll(AdminPageQueryDTO adminDTO);

    /**
     * 根据ID查询管理员信息
     * @param id
     * @return
     */
    @Select("select * from `admin` where id = #{id}")
    Admin selectById(Integer id);

    /**
     * 根据用户名查询管理员信息
     * @param userName
     * @return
     */
    @Select("select id, username, password, salt, avatar from `admin` where username = #{username}")
    Account select(String userName);

    /**
     * 新增管理员信息
     * @param admin
     * @return
     */
    int insert(Admin admin);
    /**
     * 更新管理员信息
     * @param admin
     */
    void updateById(Admin admin);
    /**
     * 删除管理员信息
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
     * 更新管理员头像
     * @param id
     * @param avatar
     */
     @Update("update `admin` set avatar = #{avatar} where id = #{id}")
    void updateAvatar(Integer id,String avatar);



}
