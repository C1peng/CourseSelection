package com.example.service.Impl;

import com.example.entity.DTO.PermissionPageQueryDTO;
import com.example.entity.Permission;
import com.example.mapper.PermissionMapper;
import com.example.service.PermissionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public PageInfo<Permission> selectPage(PermissionPageQueryDTO permissionPageQueryDTO) {
        PageHelper.startPage(permissionPageQueryDTO.getPageNum(), permissionPageQueryDTO.getPageSize());
        List<Permission> list = permissionMapper.selectAll(permissionPageQueryDTO);
        return PageInfo.of(list);
    }

    @Override
    public void insert(Permission permission) {
        permissionMapper.insert(permission);
    }

    @Override
    public void update(Permission permission) {
        permissionMapper.update(permission);
    }

    @Override
    public void deleteBatch(List<Integer> ids) {
        permissionMapper.deleteBatch(ids);
    }
}