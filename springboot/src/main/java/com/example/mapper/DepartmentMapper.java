package com.example.mapper;
import com.example.entity.DTO.DepartmentPageQueryDTO;
import com.example.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;

@Mapper
@Repository
public interface DepartmentMapper {
    /**
     * 查询所有部门信息
     * @param deptDTO
     * @return
     */
    List<Department> selectAll(DepartmentPageQueryDTO deptDTO);
    /**
     * 根据部门编号列表查询部门信息
     * @param deptIds
     * @return
     */
    List<Department> selectByDeptIds(Set<String> deptIds);

    /**
     * 新增部门信息
     * @param department
     * @return
     */
    boolean insert(Department department);
    /**
     * 修改部门信息
     * @param department
     * @return
     */
    boolean updateById(Department department);
    /**
     * 批量删除部门信息
     * @param ids
     * @return
     */
    boolean deleteBatch(List<Integer> ids);

//验证判断
    /**
     * 根据部门编号判断部门是否存在
     * @param deptId
     * @return
     */
    boolean existsByDeptId(String deptId);
}
