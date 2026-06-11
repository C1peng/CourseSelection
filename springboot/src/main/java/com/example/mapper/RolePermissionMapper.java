package com.example.mapper;

import com.example.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RolePermissionMapper {
    /**
     * 根据角色ID查询权限名称列表
     * @param roleId 角色ID
     * @return 权限名称列表
     */
    List<Permission> findPermissionByRoleId(@Param("roleId") Integer roleId);

    /**
     * 为角色分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(@Param("roleId") Integer roleId, @Param("permissionIds") List<Integer> permissionIds);

    /**
     * 清空角色的所有权限
     * @param roleId 角色ID
     */
    void clearPermissions(@Param("roleId") Integer roleId);

}