package com.example.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.exception.BusinessErrorEnum;
import com.example.common.exception.BusinessException;
import com.example.entity.Admin;
import com.example.entity.BO.Account;
import com.example.entity.DTO.AccountDTO;
import com.example.entity.DTO.AdminPageQueryDTO;
import com.example.entity.VO.AdminVO;
import com.example.mapper.AdminMapper;
import com.example.service.AdminService;
import com.example.utils.PasswordUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务层方法
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    /**
     * 添加管理员
     */
    @Override
    public void add(Admin admin) {
        if (adminMapper.existsByUserName(admin.getUsername())) {
            throw new BusinessException(BusinessErrorEnum.USER_EXIST);
        }
        if (ObjectUtil.isEmpty(admin.getPassword())) {
            admin.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(admin.getName())) {
            admin.setName(admin.getUsername());
        }
        // 生成盐值并加密密码
        String salt = PasswordUtil.generateSalt();
        admin.setSalt(salt);
        admin.setPassword(PasswordUtil.encryptPassword(admin.getPassword(), salt));
//        admin.setRole(RoleEnum.ADMIN.name());
        adminMapper.insert(admin);
    }
    /**
     * 修改管理员
     */
    @Override
    public void updateById(Admin admin) {
        adminMapper.updateById(admin);
    }
    /**
     * 删除管理员
     */
    @Override
    public void deleteBatch(List<Integer> ids) {
        adminMapper.deleteBatch(ids);
    }
    /**
     * 根据ID查询管理员信息
     */
    @Override
    public Admin selectById(Integer id) {
        return adminMapper.selectById(id);
    }
    /**
     * 分页查询管理员信息
     */
    @Override
    public PageInfo<AdminVO> selectPage(AdminPageQueryDTO adminDTO) {
        PageHelper.startPage(adminDTO.getPageNum(), adminDTO.getPageSize());
        List<AdminVO> list = adminMapper.selectAll(adminDTO);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    @Override
    public Account login(AccountDTO accountDTO) {
        Account account = adminMapper.select(accountDTO.getUsername());
        if (ObjectUtil.isNull(account)) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_EXIST);
        }
        if (!PasswordUtil.verifyPassword(accountDTO.getPassword(), account.getSalt(), account.getPassword())) {
            throw new BusinessException(BusinessErrorEnum.PASSWORD_ERROR);
        }
        return account;
    }

    /**
     * 修改密码
     */
    @Override
    public void updatePassword(AccountDTO accountDTO) {
        Admin dbAdmin = adminMapper.selectById(accountDTO.getId());
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_EXIST);
        }
        if (!PasswordUtil.verifyPassword(accountDTO.getPassword(), dbAdmin.getSalt(), dbAdmin.getPassword())) {
            throw new BusinessException(BusinessErrorEnum.PARAM_PASSWORD);
        }
        // 生成新的盐值并加密新密码
        String salt = PasswordUtil.generateSalt();
        dbAdmin.setSalt(salt);
        dbAdmin.setPassword(PasswordUtil.encryptPassword(accountDTO.getNewPassword(), salt));
        adminMapper.updateById(dbAdmin);
    }
}
