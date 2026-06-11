package com.example.service;

import com.example.entity.Permission;
import com.example.entity.DTO.PermissionPageQueryDTO;
import java.util.List;
import com.github.pagehelper.PageInfo;

public interface PermissionService {
    
    /**
     * 分页查询权限列表
     * @param permissionPageQueryDTO 查询参数
     * @return 权限列表
     */
    PageInfo<Permission> selectPage(PermissionPageQueryDTO permissionPageQueryDTO);
    
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
    void deleteBatch(List<Integer> ids);
}