package com.example.service;

import com.example.entity.BO.PageQuery;
import com.example.entity.UserRole;
import com.example.entity.VO.UserRoleVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface URoleService {
    /**
     * 获取所有用户-角色关联数据
     * @return 用户-角色关联数据列表
     */
    PageInfo<UserRoleVO> getAllUserInfo(PageQuery pageQuery);

     /**
     * 添加用户-角色关联数据
     * @param userRoles 用户-角色关联数据列表
     */
    void addBatch(List<UserRole> userRoles);

     /**
     * 删除用户-角色关联数据
     * @param ids 用户-角色关联数据ID列表
     */
    void deleteBatch(List<Integer> ids);

     /**
     * 更新用户-角色关联数据
     * @param userRole 用户-角色关联数据
     */
    void updateById(UserRole userRole);
}
