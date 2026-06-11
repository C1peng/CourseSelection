package com.example.entity.VO;

import lombok.Data;
import java.util.Map;

@Data
public class RoleVO {
    private Integer id;
    private String roleName;
    private String roleCode;
    private Map<String,Integer> permissionNames;
}