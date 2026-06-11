package com.example.entity.VO;

import lombok.Data;

@Data
public class AdminVO {
    //序号
    private Integer id;
    //账号
    private String username;
    //姓名
    private String name;
    //头像
    private String avatar;
    //手机号
    private String phone;
    //邮箱
    private String email;
}
