package com.example.service;

import com.example.entity.Admin;
import com.example.entity.BO.Account;
import com.example.entity.DTO.AccountDTO;
import com.example.entity.DTO.AdminPageQueryDTO;
import com.example.entity.VO.AdminVO;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface AdminService {
    PageInfo<AdminVO> selectPage(AdminPageQueryDTO adminDTO);

    Admin selectById(Integer id);

    void add(Admin admin);

    void updateById(Admin admin);

    void updatePassword(AccountDTO accountDTO);

    void deleteBatch(List<Integer> ids);

    Account login(AccountDTO accountDTO);

}
