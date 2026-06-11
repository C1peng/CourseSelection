package com.example.service.Impl;

import com.example.common.exception.BusinessErrorEnum;
import com.example.common.exception.BusinessException;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.entity.BO.PageQuery;
import com.example.entity.VO.RoleVO;
import com.example.mapper.PermissionMapper;
import com.example.mapper.RoleMapper;
import com.example.mapper.RolePermissionMapper;
import com.example.mapper.UserRoleMapper;
import com.example.service.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;


    @Override
    public List<Role> getRolesByUserIdAndType(Integer userId, String role) {
        return userRoleMapper.findByUserIdAndType(userId, role);
    }

    @Override
    public Set<Permission> getPermissionsByUserIdAndType(List<Role> roles, String userRole) {
        Set<String> roleCodes = new HashSet<>();
        // 添加初始角色代码
        roleCodes.add(userRole);
        // 添加Roles列表中的角色代码
        for (Role role : roles) {
            if (role != null && role.getRoleCode() != null) {
                roleCodes.add(role.getRoleCode());
            }
        }
        // 批量查询权限
        return permissionMapper.findPermissionsByRoleCodes(roleCodes);
    }

    @Override
    public PageInfo<RoleVO> selectPage(PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());
        //获取所有角色
        List<RoleVO> roles = roleMapper.selectAll();
        if (pageQuery.getStatus() == 1) {
            //根据角色id获取权限id和权限名称的映射关系
            Map<Integer, Map<String, Integer>> permissionIdMap = roles.stream()
                    .collect(Collectors.toMap(RoleVO::getId, role -> {
                        List<Permission> permissions = rolePermissionMapper.findPermissionByRoleId(role.getId());
                        return permissions.stream()
                                .collect(Collectors.toMap(Permission::getPermissionName, Permission::getId));
                    }));
            // 遍历角色对应权限
            for (RoleVO role : roles) {
                role.setPermissionNames(permissionIdMap.getOrDefault(role.getId(), Collections.emptyMap()));
            }
            return PageInfo.of(roles);
        }
        return PageInfo.of(roles);
    }

//    @Override
//    public PageInfo<UserRoleVO> getAllUserRoleRelations(PageQuery pageQuery) {
//        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());
//        List<UserRoleVO> userRoleVOs = userRoleMapper.findAllUserRoleRelations();
//
//        return PageInfo.of(userRoleVOs);
//    }



    @Override
    public List<Map<String, Object>> selectPermissionTree() {
        List<Permission> permissions = permissionMapper.findAll();

        // 使用LinkedHashMap保持模块插入顺序（如果需要顺序的话）
        Map<String, List<Permission>> moduleMap = new LinkedHashMap<>();

        // 按模块分组（使用传统for循环替代stream，减少开销）
        for (Permission permission : permissions) {
            moduleMap.computeIfAbsent(permission.getModule(),
                    k -> new ArrayList<>()).add(permission);
        }

        // 构建树结构
        List<Map<String, Object>> tree = new ArrayList<>(moduleMap.size()); // 预分配容量
        for (Map.Entry<String, List<Permission>> entry : moduleMap.entrySet()) {
            String module = entry.getKey();
            List<Permission> modulePermissions = entry.getValue();

            // 构建子节点列表
            List<Map<String, Object>> children = new ArrayList<>(modulePermissions.size()); // 预分配容量
            for (Permission permission : modulePermissions) {
                // 使用HashMap替代Map.of()以减少对象创建开销（Map.of()会创建额外的不可变Map对象）
                Map<String, Object> childNode = new HashMap<>();
                childNode.put("id", permission.getId());
                childNode.put("action", permission.getAction());
                children.add(childNode);
            }

            // 构建模块节点
            Map<String, Object> moduleNode = new HashMap<>();
            moduleNode.put("module", module);
            moduleNode.put("children", children);
            tree.add(moduleNode);
        }

        return tree;
    }


    @Override
    public void insert(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public void update(Role role) {
        roleMapper.update(role);
    }

    @Override
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            //判断是否有用户关联
            if (userRoleMapper.existsByRoleId(id)) {
                throw new BusinessException(BusinessErrorEnum.COURSE_EXIST);
            }
            //删除角色、权限
            roleMapper.deleteById(id);
            rolePermissionMapper.clearPermissions(id);
        }

    }

    @Override
    public void assignPermissions(Map<String, Object> params) {
        // 从params中获取roleId和permissionIds
        Integer roleId = (Integer) params.get("roleId");
        List<Integer> permissionIds = (List<Integer>) params.get("permissionIds");
        // 先清空角色原有权限
        rolePermissionMapper.clearPermissions(roleId);
        // 如果权限列表不为空，则分配新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.assignPermissions(roleId, permissionIds);
        }
    }


}