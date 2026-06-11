package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin{
    //序号
    private Integer id;
    //账号
    private String username;
    //密码
    private String password;
    //盐值
    private String salt;
    //角色
//    private String role;
    //姓名
    private String name;
    //头像
    private String avatar;
    //手机号
    private String phone;
    //邮箱
    private String email;
}
