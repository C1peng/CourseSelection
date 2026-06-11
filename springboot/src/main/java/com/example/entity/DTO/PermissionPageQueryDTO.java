package com.example.entity.DTO;

import lombok.Data;

/**
 * 权限分页查询DTO
 */
@Data
public class PermissionPageQueryDTO {
    /**
     * 页码
     */
    private Integer pageNum;
    
    /**
     * 每页条数
     */
    private Integer pageSize;
    
    /**
     * 权限名称
     */
    private String name;
    
    /**
     * 权限编码
     */
    private String code;
    
    /**
     * 模块名称
     */
    private String module;
}