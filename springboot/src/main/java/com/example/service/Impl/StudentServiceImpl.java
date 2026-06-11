package com.example.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.exception.BusinessErrorEnum;
import com.example.common.exception.BusinessException;
import com.example.entity.BO.Account;
import com.example.entity.BO.CSType;
import com.example.entity.BO.StSType;
import com.example.entity.CourseSelection;
import com.example.entity.CreditLine;
import com.example.entity.DTO.AccountDTO;
import com.example.entity.DTO.CSelectionPageQueryDTO;
import com.example.entity.DTO.StSelectionDTO;
import com.example.entity.DTO.StudentPageQueryDTO;
import com.example.entity.Student;
import com.example.entity.VO.CourseTimetableVO;
import com.example.entity.VO.StudentVO;
import com.example.entity.VO.other.StudentCSDetailsVO;
import com.example.entity.VO.other.StudentCSelectionVO;
import com.example.entity.VO.other.StudentCourseVO;
import com.example.entity.VO.other.StudentSCourseVO;
import com.example.mapper.CSelectionMapper;
import com.example.mapper.CreditLineMapper;
import com.example.mapper.StudentCreditMapper;
import com.example.mapper.StudentMapper;
import com.example.mapper.UserRoleMapper;
import com.example.service.StudentService;
import com.example.service.SemesterTimeRangeService;
import com.example.utils.CourseUtils;
import com.example.utils.PasswordUtil;
import com.example.utils.UserUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 业务层方法
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Autowired
    private CSelectionMapper cSelectionMapper;
    @Autowired
    private StudentCreditMapper studentCreditMapper;
    @Autowired
    private CreditLineMapper creditLineMapper;
    @Autowired
    private SemesterTimeRangeService semesterTimeRangeService;
    @Override
    public PageInfo<StudentCSelectionVO> selectSCourseInfo(CSelectionPageQueryDTO cSelectionDTO) {
        PageHelper.startPage(cSelectionDTO.getPageNum(), cSelectionDTO.getPageSize());
        List<StudentCSelectionVO> totallist = cSelectionMapper.selectSCourseInfo(cSelectionDTO);
        // 获取详细课程详细列表
        List<CSType> list = cSelectionMapper.selectDetail(cSelectionDTO);
        List<StudentCSDetailsVO> detailsList = list.stream()
                .map(item -> {
                    StudentCSDetailsVO vo = new StudentCSDetailsVO();
                    BeanUtils.copyProperties(item, vo);
                    vo.setWeekTime(CourseUtils.analysisTime(item.getCourseTime()));
                    vo.setCycle(CourseUtils.analysisWeek(item.getCourseNumber(), item.getCourseTotal()));
                    return vo;
                })
                .collect(Collectors.toCollection(() -> new ArrayList<>(list.size())));
        // 使用Stream API按courseId分组CSDetailsVO对象
        Map<String, List<StudentCSDetailsVO>> detailMap = detailsList.stream()
                .collect(Collectors.groupingBy(StudentCSDetailsVO::getCourseName));

        // 将分组后的CSDetailsVO列表填充到对应的CSelectionVO对象的csDetails字段中
        totallist.forEach(vo -> vo.setCsDetails(detailMap.getOrDefault(vo.getCourseName(), Collections.emptyList())));
        return PageInfo.of(totallist);
    }

    @Override
    public PageInfo<StudentSCourseVO> selectSCDetailInfo(String semester) {
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
        }
        List<CSType> list = cSelectionMapper.selectSDetailInfo(semester,
                (Integer) currentUser.get("userId"));
        List<StudentSCourseVO> voList = list.stream()
                .map(item -> {
                    StudentSCourseVO vo = new StudentSCourseVO();
                    BeanUtils.copyProperties(item, vo);
                    vo.setWeekTime(CourseUtils.analysisTime(item.getCourseTime()));
                    vo.setCycle(CourseUtils.analysisWeek(item.getCourseNumber(), item.getCourseTotal()));
                    return vo;
                })
                .collect(Collectors.toCollection(() -> new ArrayList<>(list.size())));
        return PageInfo.of(voList);
    }

    @Override
    public PageInfo<StudentCourseVO> selectSCourseLine(Integer studentId) {
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
        }
        List<StSType> list = studentCreditMapper.selectSCourseLine(studentId);
        //根据semester完成分组
        Map<String, List<StSType>> groupedMap = list.stream()
                .collect(Collectors.groupingBy(StSType::getSemester));
        
        List<StudentCourseVO> voList = groupedMap.entrySet().stream()
                .map(entry -> {
                    StudentCourseVO vo = new StudentCourseVO();
                    vo.setSemester(entry.getKey());
                    List<StudentCourseVO.StudentLineInfo> slInfoList = entry.getValue().stream()
                            .map(item -> {
                                StudentCourseVO.StudentLineInfo slInfo = new StudentCourseVO.StudentLineInfo();
                                BeanUtils.copyProperties(item, slInfo);
                                return slInfo;
                            })
                            .collect(Collectors.toList());
                    vo.setSLInfo(slInfoList);
                    return vo;
                })
                .collect(Collectors.toList());
        return PageInfo.of(voList);
    }

    @Override
    public PageInfo<CourseTimetableVO> courseTimetable(String semester, Integer studentId) {
        List<CSType> list = cSelectionMapper.selectStudentTimetable(semester, studentId);
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

    @Override
    public void addSCourse(StSelectionDTO stSelectionDTO) {
        // 获取当前用户信息
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
        }
        Integer studentId = (Integer) currentUser.get("userId");
        Integer courseId = stSelectionDTO.getCourseId();

        // 1. 验证课程是否存在
        boolean courseExists = cSelectionMapper.checkCourseExists(courseId);
        if (!courseExists) {
            throw new BusinessException(BusinessErrorEnum.REQUEST_PARAM_ERROR, "课程不存在");
        }
        
        // 2. 检查选课时间范围
        // 获取课程信息
        CourseSelection courseSelection = cSelectionMapper.selectById(courseId);
        if (courseSelection != null && !semesterTimeRangeService.isInCourseSelectTimeRange(courseSelection.getSemester())) {
            throw new BusinessException(BusinessErrorEnum.REQUEST_PARAM_ERROR, "当前不在选课时间范围内");
        }

        // 2. 验证课程是否已满
        boolean hasCapacity = cSelectionMapper.checkCourseCapacity(courseId);
        if (!hasCapacity) {
            throw new BusinessException(BusinessErrorEnum.REQUEST_PARAM_ERROR, "课程人数已满");
        }



        // 3. 验证学生是否已选该课程
        boolean alreadySelected = cSelectionMapper.checkStudentCourse(studentId, courseId);
        if (alreadySelected) {
            throw new BusinessException(BusinessErrorEnum.REQUEST_PARAM_ERROR, "您已选该课程");
        }

        // 4. 验证是否重复选同一课程名的课程（除体育选修课外）
        if (courseSelection != null) {
            // 获取课程信息
            String creditsType = courseSelection.getCreditsType();
            // 除了体育选修课学分外，不允许重复选同一课程名的课程
            if (!"sportCredit".equals(creditsType)) {
                // 检查学生是否已选过相同课程名的课程
                boolean hasSameCourseName = cSelectionMapper.checkSameCourseName(studentId, courseId);
                if (hasSameCourseName) {
                    throw new BusinessException(BusinessErrorEnum.REQUEST_PARAM_ERROR, "您已选过同名课程，不能重复选");
                }
            }
        }

        // 5. 验证时间冲突
        if (courseSelection != null) {
            Integer conflictCourseId = cSelectionMapper.checkTimeConflict(
                    studentId, 
                    courseSelection.getCourseTime(), 
                    courseSelection.getCourseNumber(), 
                    courseSelection.getSemester()
            );
            if (conflictCourseId != null) {
                throw new BusinessException(BusinessErrorEnum.REQUEST_PARAM_ERROR, "课程时间冲突");
            }
        }

        // 5. 执行选课操作
        // 获取学生姓名
        Student student = studentMapper.selectById(studentId);
        String studentName = student != null ? student.getName() : "";
        cSelectionMapper.insertStudentSelection(studentId, courseId, studentName);

        // 6. 更新课程已选人数
        cSelectionMapper.updateCourseSelectionCount(courseId, 1);
    }

    @Override
    public void dropSCourse(Integer courseId) {
        // 获取当前用户信息
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
        }
        Integer studentId = (Integer) currentUser.get("userId");

        // 1. 验证课程是否存在
        boolean courseExists = cSelectionMapper.checkCourseExists(courseId);
        if (!courseExists) {
            throw new BusinessException(BusinessErrorEnum.REQUEST_PARAM_ERROR, "课程不存在");
        }
        
        // 2. 检查退课时间范围
        CourseSelection courseSelection = cSelectionMapper.selectById(courseId);
        if (courseSelection != null && !semesterTimeRangeService.isInCourseSelectTimeRange(courseSelection.getSemester())) {
            throw new BusinessException(BusinessErrorEnum.REQUEST_PARAM_ERROR, "当前不在退课时间范围内");
        }

        // 2. 验证学生是否已选该课程
        boolean alreadySelected = cSelectionMapper.checkStudentCourse(studentId, courseId);
        if (!alreadySelected) {
            throw new BusinessException(BusinessErrorEnum.REQUEST_PARAM_ERROR, "您未选该课程");
        }

        // 3. 执行退课操作
        cSelectionMapper.deleteStudentSelection(studentId, courseId);

        // 4. 更新课程已选人数
        cSelectionMapper.updateCourseSelectionCount(courseId, -1);
    }


    /**
     * 新增学生信息
     */
    public void add(Student student) {
        if (studentMapper.existsByUserName(student.getUsername())) {
            throw new BusinessException(BusinessErrorEnum.USER_EXIST);
        }
        if (ObjectUtil.isEmpty(student.getPassword())) {
            student.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(student.getName())) {
            student.setName(student.getUsername());
        }
        if (ObjectUtil.isEmpty(student.getSort())) {
            LocalDate now = LocalDate.now();
            student.setSort(now.getYear()  - Integer.parseInt(student.getGrade()));
        }
        // 生成盐值并加密密码
        String salt = PasswordUtil.generateSalt();
        student.setSalt(salt);
        student.setPassword(PasswordUtil.encryptPassword(student.getPassword(), salt));

        studentMapper.insert(student);
    }
    /**
     * 更新学生信息
     */

    public void updateById(Student student) {
        studentMapper.updateById(student);
    }
    /**
     * 删除学生信息
     */
    @Override
    public void deleteBatch(List<Integer> ids) {
        studentMapper.deleteBatch(ids);
    }
    /**
     * 根据ID查询学生信息
     */
    @Override
    public Student selectById(Integer id) {
        return studentMapper.selectById(id);
    }
    /**
     * 分页查询学生信息
     */
    public PageInfo<StudentVO> selectPage(StudentPageQueryDTO studentDTO) {
        PageHelper.startPage(studentDTO.getPageNum(), studentDTO.getPageSize());
        List<StudentVO> list = studentMapper.selectAll(studentDTO);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */

    public Account login(AccountDTO accountDTO) {
        Account account = studentMapper.select(accountDTO.getUsername());
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
        Student student = studentMapper.selectById(accountDTO.getId());
        if (ObjectUtil.isNull(student)) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_EXIST);
        }
        if (!PasswordUtil.verifyPassword(accountDTO.getPassword(), student.getSalt(), student.getPassword())) {
            throw new BusinessException(BusinessErrorEnum.PARAM_PASSWORD);
        }
        // 生成新的盐值并加密新密码
        String salt = PasswordUtil.generateSalt();
        student.setSalt(salt);
        student.setPassword(PasswordUtil.encryptPassword(accountDTO.getNewPassword(), salt));
        studentMapper.updateById(student);
    }

    @Override
    public List<Integer> getAllStudentIds() {
        return studentMapper.selectAllIds();
    }

    @Override
    public String calculateCreditGap(Integer studentId) {
        // 获取学生信息
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            return "学生信息不存在";
        }

        // 获取对应的学分要求
        CreditLine creditLine = creditLineMapper.selectByDeptAndGradeAndSort(
                student.getDeptId(),
                student.getGrade(),
                student.getSort()
        );

        if (creditLine == null) {
            return "未找到对应的学分要求";
        }

        // 计算各类型学分差距
        StringBuilder gapInfo = new StringBuilder();

        // 公共学分差距
        BigDecimal publicGap = creditLine.getPublicCredit().subtract(student.getPublicCredits() != null ? student.getPublicCredits() : BigDecimal.ZERO);
        if (publicGap.compareTo(BigDecimal.ZERO) > 0) {
            gapInfo.append("- 公共学分：还差").append(publicGap).append("分\n");
        }

        // 外语学分差距
        BigDecimal foreignLanguageGap = creditLine.getForeignLanguageCredit().subtract(student.getForeignLanguageCredits() != null ? student.getForeignLanguageCredits() : BigDecimal.ZERO);
        if (foreignLanguageGap.compareTo(BigDecimal.ZERO) > 0) {
            gapInfo.append("- 外语学分：还差").append(foreignLanguageGap).append("分\n");
        }

        // 体育学分差距
        BigDecimal sportGap = creditLine.getSportCredit().subtract(student.getSportCredits() != null ? student.getSportCredits() : BigDecimal.ZERO);
        if (sportGap.compareTo(BigDecimal.ZERO) > 0) {
            gapInfo.append("- 体育学分：还差").append(sportGap).append("分\n");
        }

        // 艺术学分差距
        BigDecimal artGap = creditLine.getArtCredit().subtract(student.getArtCredits() != null ? student.getArtCredits() : BigDecimal.ZERO);
        if (artGap.compareTo(BigDecimal.ZERO) > 0) {
            gapInfo.append("- 艺术学分：还差").append(artGap).append("分\n");
        }

        if (gapInfo.length() == 0) {
            gapInfo.append("所有学分已达标，无需担心！");
        }

        return gapInfo.toString();
    }
}
