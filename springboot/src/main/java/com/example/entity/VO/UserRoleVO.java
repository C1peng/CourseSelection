package com.example.entity.VO;

import lombok.Data;

@Data
public class UserRoleVO {
    //序号
    private Integer id;
    //用户名
    private String username;
    //姓名
    private String name;
    //用户类型
    private String userType;
    //角色
    private String roleName;
}
