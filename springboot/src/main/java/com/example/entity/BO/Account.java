package com.example.entity.BO;

import lombok.Data;

@Data
public class Account {
    //序号
    private Integer id;
    //头像
    private String avatar;
    //账号
    private String username;
    //密码
    private String password;
    //盐值
    private String salt;
    //角色
    private String role;
}
