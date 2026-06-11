package com.example.service;

import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.entity.BO.PageQuery;
import com.example.entity.VO.RoleVO;
import java.util.Map;
import java.util.List;
import java.util.Set;
import com.github.pagehelper.PageInfo;

public interface RoleService {
    /**
     * 分页查询角色VO列表（包含权限信息）
     * @param pageQuery 查询参数
     * @return 角色VO列表
     */
    PageInfo<RoleVO> selectPage(PageQuery pageQuery);

    /**
     * 获取权限树结构
     * @return 权限树
     */
    List<Map<String, Object>> selectPermissionTree();

    /**
     * 新增角色
     * @param role 角色信息
     */
    void insert(Role role);

    /**
     * 更新角色
     * @param role 角色信息
     */
    void update(Role role);

    /**
     * 修改角色权限
     * @param params 参数Map，包含roleId和permissionIds
     */
    void assignPermissions(Map<String, Object> params);

    /**
     * 批量删除角色
     * @param ids 角色ID列表
     */
    void deleteBatch(List<Integer> ids);



    /**
     * 根据用户ID和用户角色查询角色列表
     * @param userId 用户ID
     * @param role 用户角色
     * @return 角色列表
     */
    List<Role> getRolesByUserIdAndType(Integer userId, String role);

    /**
     * 根据用户ID和用户角色查询权限列表
     * @param userRoles 用户角色列表
     * @param role 用户
     * @return 权限列表
     */
    Set<Permission> getPermissionsByUserIdAndType(List<Role> userRoles, String role);

//    /**
//     * 根据角色ID获取关联的用户列表
//     * @param roleId 角色ID
//     * @return 用户列表
//     */
//    List<Map<String, Object>> getUsersByRoleId(Integer roleId);
//
//    /**
//     * 获取可选分配给角色的用户列表
//     * @param roleId 角色ID
//     * @return 可选用户列表
//     */
//    List<Map<String, Object>> getAvailableUsersByRoleId(Integer roleId);
//
//    /**
//     * 为角色分配用户
//     * @param params 参数Map，包含roleId和userIds
//     */
//    void assignUsers(Map<String, Object> params);
//
//    /**
//     * 从角色中移除用户
//     * @param roleId 角色ID
//     * @param userId 用户ID
//     * @param userType 用户类型
//     */
//    void removeUserFromRole(Integer roleId, Integer userId, String userType);

    /**
     * 获取所有用户-角色关联数据
     * @return 用户-角色关联数据列表
     */
//    PageInfo<UserRoleVO> getAllUserRoleRelations(PageQuery pageQuery);


}
