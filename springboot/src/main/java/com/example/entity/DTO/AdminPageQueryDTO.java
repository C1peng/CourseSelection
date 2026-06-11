package com.example.entity.DTO;

import lombok.Data;

@Data
public class AdminPageQueryDTO {
    private Integer pageNum;
    private Integer pageSize;
    private Integer id;
    private String userInfo;
}
