package com.example.service.Impl;

import com.example.entity.DTO.DepartmentPageQueryDTO;
import com.example.entity.Department;
import com.example.mapper.DepartmentMapper;
import com.example.service.DepartmentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 添加部门信息
     */
    public void add(Department department) {
        departmentMapper.insert(department);
    }

    /**
     * 分页查询部门信息
     */
    public PageInfo<Department> selectPage(DepartmentPageQueryDTO deptDTO) {
        PageHelper.startPage(deptDTO.getPageNum(), deptDTO.getPageSize());
        List<Department> list = departmentMapper.selectAll(deptDTO);
        return PageInfo.of(list);
    }

    /**
     * 更新部门信息
     */
    public void updateById(Department department) {
        departmentMapper.updateById(department);
    }

    /**
     * 批量删除部门信息
     */
    public void deleteBatch(List<Integer> ids) {
        departmentMapper.deleteBatch(ids);
    }
}
