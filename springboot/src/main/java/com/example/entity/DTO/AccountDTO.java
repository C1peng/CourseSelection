package com.example.entity.DTO;

import lombok.Data;

@Data
public class AccountDTO {
    private Integer id;
    private String role;
    private String username;
    private String password;
    private String newPassword;
}
