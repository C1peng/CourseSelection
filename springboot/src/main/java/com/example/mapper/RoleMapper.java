package com.example.mapper;

import com.example.entity.Role;
import com.example.entity.VO.RoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoleMapper {
    /**
     * 查询所有角色
     * @return 角色列表
     */
    List<RoleVO> selectAll();
    /**
     * 新增角色
     * @param role 角色信息
     */
    void insert(Role role);

    /**
     * 更新角色
     * @param role 角色信息
     */
    void update(Role role);

    /**
     * 批量删除角色
     * @param id 角色ID列表
     */
    void deleteById(@Param("id") Integer id);
}