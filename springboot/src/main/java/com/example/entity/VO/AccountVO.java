package com.example.entity.VO;

import lombok.Data;

@Data
public class AccountVO {
    private Integer id;
    //头像
    private String avatar;
    //用户名
    private String username;
    //token
    private String token;
}
