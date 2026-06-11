package com.example.entity.VO;

import lombok.Data;

@Data
public class TeacherVO {
    //序号
    private Integer id;
    //账号
    private String username;
    //头像
    private String avatar;
    //姓名
    private String name;
    //性别
    private Integer gender;
    //学院编号
    private String deptId;
    //学院名称
    private String deptName;
    //手机号
    private String phone;
    //邮箱
    private String email;
}
