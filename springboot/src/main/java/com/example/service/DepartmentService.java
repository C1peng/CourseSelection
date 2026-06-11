package com.example.service;

import com.example.entity.DTO.DepartmentPageQueryDTO;
import com.example.entity.Department;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface DepartmentService {

    PageInfo<Department> selectPage(DepartmentPageQueryDTO deptDTO);

    void add(Department department);

    void updateById(Department department);

    void deleteBatch(List<Integer> ids);
}
