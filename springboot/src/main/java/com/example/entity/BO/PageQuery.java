package com.example.entity.BO;

import lombok.Data;

/**
 * 角色分页查询DTO
 */
@Data
public class PageQuery {
    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;
    /**
     * 状态
     */
    private Integer status = 0;
}