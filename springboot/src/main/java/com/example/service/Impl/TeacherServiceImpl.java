package com.example.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.exception.BusinessErrorEnum;
import com.example.common.exception.BusinessException;
import com.example.entity.BO.Account;
import com.example.entity.BO.CSType;
import com.example.entity.DTO.AccountDTO;
import com.example.entity.DTO.CSelectionPageQueryDTO;
import com.example.entity.Teacher;
import com.example.entity.DTO.TeacherPageQueryDTO;
import com.example.entity.VO.CourseTimetableVO;
import com.example.entity.VO.TeacherVO;
import com.example.entity.VO.other.TeacherCSDetailsVO;
import com.example.entity.VO.other.TeacherCSelectionVO;
import com.example.mapper.CSelectionMapper;
import com.example.mapper.DepartmentMapper;
import com.example.mapper.TeacherMapper;
import com.example.service.TeacherService;
import com.example.utils.CourseUtils;
import com.example.utils.PasswordUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务层方法
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private DepartmentMapper departmentMapper;
    @Autowired
    private CSelectionMapper cSelectionMapper;

    /**
     * 其它端
     * 分页查询课程选择信息详情
     */
    public PageInfo<TeacherCSelectionVO> selectTCourseInfo(CSelectionPageQueryDTO cSelectionDTO) {
        PageHelper.startPage(cSelectionDTO.getPageNum(), cSelectionDTO.getPageSize());
        List<TeacherCSelectionVO> list = cSelectionMapper.selectTCourseInfo(cSelectionDTO);
        return PageInfo.of(list);
    }

    /**
     * 其它端
     * 分页查询课程选择详情信息
     */
    public PageInfo<TeacherCSDetailsVO> selectTCDetailInfo(CSelectionPageQueryDTO cSelectionDTO) {
        PageHelper.startPage(cSelectionDTO.getPageNum(), cSelectionDTO.getPageSize());
        List<CSType> list = cSelectionMapper.selectDetail(cSelectionDTO);
        List<TeacherCSDetailsVO> detailsList = list.stream()
                .map(item -> {
                    TeacherCSDetailsVO vo = new TeacherCSDetailsVO();
                    BeanUtils.copyProperties(item, vo);
                    vo.setWeekTime(CourseUtils.analysisTime(item.getCourseTime()));
                    vo.setCycle(CourseUtils.analysisWeek(item.getCourseNumber(), item.getCourseTotal()));
                    return vo;
                })
                .collect(Collectors.toCollection(() -> new ArrayList<>(list.size())));
        PageInfo<TeacherCSDetailsVO> pageInfo = new PageInfo<>(detailsList);
        pageInfo.setTotal(((com.github.pagehelper.Page<?>) list).getTotal());
        return pageInfo;
    }

    @Override
    public PageInfo<CourseTimetableVO> courseTimetable(String semester, Integer teacherId) {
        List<CSType> list = cSelectionMapper.selectTeacherTimetable(semester, teacherId);
        List<CourseTimetableVO> voList = list.stream()
                .map(item -> {
                    CourseTimetableVO vo = new CourseTimetableVO();
                    BeanUtils.copyProperties(item, vo);
                    vo.setWeekTime(CourseUtils.analysisTime(item.getCourseTime()));
                    vo.setCycle(CourseUtils.analysisWeek(item.getCourseNumber(), item.getCourseTotal()));
                    return vo;
                })
                .collect(Collectors.toCollection(() -> new ArrayList<>(list.size())));
        return PageInfo.of(voList);
    }


    /**
     * 添加教师信息
     */
    public void add(Teacher teacher) {
        //判断用户名是否存在
        if (teacherMapper.existsByUserName(teacher.getUsername())) {
            throw new BusinessException(BusinessErrorEnum.USER_EXIST);
        }
        //判断学院是否存在
        if (!departmentMapper.existsByDeptId(teacher.getDeptId())) {
            throw new BusinessException(BusinessErrorEnum.COLLEGE_NOT_EXIST);
        }
        if (ObjectUtil.isEmpty(teacher.getPassword())) {
            teacher.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(teacher.getName())) {
            teacher.setName(teacher.getUsername());
        }
        // 生成盐值并加密密码
        String salt = PasswordUtil.generateSalt();
        teacher.setSalt(salt);
        teacher.setPassword(PasswordUtil.encryptPassword(teacher.getPassword(), salt));
//        teacher.setRole(RoleEnum.TEACHER.name());

        teacherMapper.insert(teacher);
    }

    /**
     * 更新教师信息
     */
    public void updateById(Teacher teacher) {
        //判断学院是否存在
        if (!departmentMapper.existsByDeptId(teacher.getDeptId())) {
            throw new BusinessException(BusinessErrorEnum.COLLEGE_NOT_EXIST);
        }
        teacherMapper.updateById(teacher);
    }

    /**
     * 批量删除教师信息
     */
    public void deleteBatch(List<Integer> ids) {
        teacherMapper.deleteBatch(ids);
    }

    /**
     * 根据序号查询教师信息
     */
    public Teacher selectById(Integer id) {
        return teacherMapper.selectById(id);
    }

    /**
     * 分页查询教师信息
     */
    public PageInfo<TeacherVO> selectPage(TeacherPageQueryDTO teacherDTO) {
        PageHelper.startPage(teacherDTO.getPageNum(), teacherDTO.getPageSize());
        List<TeacherVO> list = teacherMapper.selectAll(teacherDTO);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Account login(AccountDTO accountDTO) {
        Account account = teacherMapper.select(accountDTO.getUsername());
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
    public void updatePassword(AccountDTO accountDTO) {
        Teacher dbTeacher = teacherMapper.selectById(accountDTO.getId());
        if (ObjectUtil.isNull(dbTeacher)) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_EXIST);
        }
        if (!PasswordUtil.verifyPassword(accountDTO.getPassword(), dbTeacher.getSalt(), dbTeacher.getPassword())) {
            throw new BusinessException(BusinessErrorEnum.PARAM_PASSWORD);
        }
        // 生成新的盐值并加密新密码
        String salt = PasswordUtil.generateSalt();
        dbTeacher.setSalt(salt);
        dbTeacher.setPassword(PasswordUtil.encryptPassword(accountDTO.getNewPassword(), salt));
        teacherMapper.updateById(dbTeacher);
    }
}