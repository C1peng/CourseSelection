package com.example.service.Impl;
import com.example.entity.BO.PageQuery;
import com.example.entity.UserRole;
import com.example.entity.VO.UserRoleVO;
import com.example.mapper.UserRoleMapper;
import com.example.service.URoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class URoleServiceImpl implements URoleService {
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public PageInfo<UserRoleVO> getAllUserInfo(PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());
        //查询所有用户信息（管理员、教师、学生）
        List<UserRoleVO> userRoleVOs = userRoleMapper.getAllUserInfo();
        return PageInfo.of(userRoleVOs);
    }

    @Override
    public void addBatch(List<UserRole> userRoles) {
        userRoleMapper.insertList(userRoles);
    }

    @Override
    public void deleteBatch(List<Integer> ids) {
        userRoleMapper.deleteBatch(ids);
    }

     @Override
    public void updateById(UserRole userRole) {
        userRoleMapper.updateById(userRole.getId(),userRole.getRoleId());
    }
}
