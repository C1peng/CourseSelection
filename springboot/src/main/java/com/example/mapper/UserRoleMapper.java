package com.example.mapper;

import com.example.entity.Role;
import com.example.entity.UserRole;
import com.example.entity.VO.UserRoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Mapper
@Repository
public interface UserRoleMapper {
//    /**
//     * 根据用户ID和用户类型查询用户角色关联列表
//     * @param userId 用户ID
//     * @param userType 用户类型
//     * @return 用户角色关联列表
//     */
//    List<UserRole> findByUserIdAndType(@Param("userId") Integer userId, @Param("userType") String userType);
//
//    /**
//     * 根据角色ID查询用户角色关联列表
//     * @param roleId 角色ID
//     * @return 用户角色关联列表
//     */
//    List<UserRole> findByRoleId(@Param("roleId") Integer roleId);
//
//    /**
//     * 插入用户角色关联
//     * @param userRole 用户角色关联
//     * @return 影响行数
//     */
//    int insert(UserRole userRole);
//
//    /**
//     * 根据用户ID、用户类型和角色ID删除用户角色关联
//     * @param userId 用户ID
//     * @param userType 用户类型
//     * @param roleId 角色ID
//     * @return 影响行数
//     */
//    int deleteByUserIdAndTypeAndRoleId(@Param("userId") Integer userId,
//                                       @Param("userType") String userType,
//                                       @Param("roleId") Integer roleId);


    /**
     * 根据用户ID和用户类型查询角色列表
     * @param userId 用户ID
     * @param userType 用户类型
     * @return 角色列表
     */
    List<Role> findByUserIdAndType(@Param("userId") Integer userId, @Param("userType") String userType);

    /**
     * 查询所有用户-角色关联数据
     * @return 用户-角色关联数据列表
     */
//     List<UserRoleVO> findAllUserRoleRelations();

    /**
     * 查询所有用户信息（管理员、教师、学生）
     * @return 用户信息列表
     */
     List<UserRoleVO> getAllUserInfo();

//验证判断
    /**
     * 根据角色ID查询用户角色关联是否存在
     * @param roleId 角色ID
     * @return 用户角色关联是否存在
     */
    boolean existsByRoleId(@Param("roleId") Integer roleId);

     /**
     * 添加用户-角色关联数据
     * @param userRoles 用户-角色关联数据列表
     */
    void insertList(List<UserRole> userRoles);

     /**
     * 根据用户-角色关联数据ID列表删除用户-角色关联数据
     * @param ids 用户-角色关联数据ID列表
     */
    void deleteBatch(List<Integer> ids);

     /**
     * 更新用户-角色关联数据
     */
    void updateById(@Param("id") Integer id, @Param("roleId") Integer roleId);
}