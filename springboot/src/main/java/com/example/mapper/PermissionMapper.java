package com.example.mapper;

import com.example.entity.Permission;
import com.example.entity.DTO.PermissionPageQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Mapper
@Repository
public interface PermissionMapper {

    /**
     * 查询所有权限
     * @param permissionPageQueryDTO 查询参数
     * @return 权限列表
     */
    List<Permission> selectAll(PermissionPageQueryDTO permissionPageQueryDTO);
    
    /**
     * 新增权限
     * @param permission 权限信息
     */
    void insert(Permission permission);
    
    /**
     * 更新权限
     * @param permission 权限信息
     */
    void update(Permission permission);
    
    /**
     * 批量删除权限
     * @param ids 权限ID列表
     */
    void deleteBatch(@Param("ids") List<Integer> ids);

//操作
Set<Permission> findPermissionsByRoleCodes(@Param("roleCodes") Set<String> roleCodes);

    /**
     * 根据用户ID和用户类型查询权限列表
     * @param userId 用户ID
     * @param userType 用户类型
     * @return 权限列表
     */
//    List<Permission> findByUserIdAndType(@Param("userId") Integer userId, @Param("userType") String userType);

    /**
     * 查询所有权限
     * @return 权限列表
     */
    List<Permission> findAll();

}