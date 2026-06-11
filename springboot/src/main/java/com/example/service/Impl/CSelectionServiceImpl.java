package com.example.service.Impl;

import com.example.common.exception.BusinessErrorEnum;
import com.example.common.exception.BusinessException;
import com.example.common.exception.ValidationException;
import com.example.entity.BO.CSType;
import com.example.entity.BO.StSType;
import com.example.entity.CourseSelection;
import com.example.entity.DTO.CSDetailsDTO;
import com.example.entity.DTO.CSelectionPageQueryDTO;
import com.example.entity.DTO.CSelectionDTO;
import com.example.entity.DTO.StSelectionPageQueryDTO;
import com.example.entity.VO.admin.AdminCSDetailsVO;
import com.example.entity.VO.admin.AdminCSelectionVO;
import com.example.entity.VO.StSelectionVO;
import com.example.mapper.CSelectionMapper;
import com.example.mapper.StudentCreditMapper;
import com.example.mapper.TeacherMapper;
import com.example.service.CSelectionService;
import com.example.service.SemesterTimeRangeService;
import com.example.utils.CourseUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CSelectionServiceImpl implements CSelectionService {
    @Autowired
    private CSelectionMapper cSelectionMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private StudentCreditMapper sSelectionMapper;
    @Autowired
    private SemesterTimeRangeService semesterTimeRangeService;

    /**
     * 管理端
     * 分页查询课程选择信息
     */
    public PageInfo<AdminCSelectionVO> selectPage(CSelectionPageQueryDTO cSelectionDTO) {
        PageHelper.startPage(cSelectionDTO.getPageNum(), cSelectionDTO.getPageSize());

        List<AdminCSelectionVO> totallist = cSelectionMapper.selectAll(cSelectionDTO);
        // 获取详细课程详细列表
        List<CSType> list = cSelectionMapper.selectDetail(cSelectionDTO);
        List<AdminCSDetailsVO> detailsList = list.stream()
                .map(item -> {
                    AdminCSDetailsVO vo = new AdminCSDetailsVO();
                    BeanUtils.copyProperties(item, vo);
                    vo.setWeekTime(CourseUtils.analysisTime(item.getCourseTime()));
                    vo.setCycle(CourseUtils.analysisWeek(item.getCourseNumber(), item.getCourseTotal()));
                    return vo;
                })
                .collect(Collectors.toCollection(() -> new ArrayList<>(list.size())));
        // 使用Stream API按courseId分组CSDetailsVO对象
        Map<Integer, List<AdminCSDetailsVO>> detailMap = detailsList.stream()
                .collect(Collectors.groupingBy(AdminCSDetailsVO::getCourseId));

        // 将分组后的CSDetailsVO列表填充到对应的CSelectionVO对象的csDetails字段中
        totallist.forEach(vo -> vo.setCsDetails(detailMap.getOrDefault(vo.getCourseId(), Collections.emptyList())));

        return PageInfo.of(totallist);
    }

    /**
     * 管理端
     * 分页查询课程选择详情信息
     */
    public PageInfo<AdminCSDetailsVO> selectDetailPage(CSelectionPageQueryDTO cSelectionDTO) {
        PageHelper.startPage(cSelectionDTO.getPageNum(), cSelectionDTO.getPageSize());
        List<CSType> list = cSelectionMapper.selectDetail(cSelectionDTO);
        List<AdminCSDetailsVO> detailsList = list.stream()
                .map(item -> {
                    AdminCSDetailsVO vo = new AdminCSDetailsVO();
                    BeanUtils.copyProperties(item, vo);
                    vo.setWeekTime(CourseUtils.analysisTime(item.getCourseTime()));
                    vo.setCycle(CourseUtils.analysisWeek(item.getCourseNumber(), item.getCourseTotal()));
                    return vo;
                })
                .collect(Collectors.toCollection(() -> new ArrayList<>(list.size())));
        PageInfo<AdminCSDetailsVO> pageInfo = new PageInfo<>(detailsList);
        pageInfo.setTotal(((com.github.pagehelper.Page<?>) list).getTotal());
        return pageInfo;
    }

    /**
     * 删除课程选择信息
     */
    @Transactional
    public void deleteBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ValidationException("删除ID列表不能为空");
        }
        // 检查单个ID的情况
        if (ids.size() == 1) {
            CourseSelection courseSelection = cSelectionMapper.selectById(ids.get(0));
            if (courseSelection != null && courseSelection.getStatus() == 3) {
                if (cSelectionMapper.ExistsById(courseSelection.getCourseId())) {
                    throw new BusinessException(BusinessErrorEnum.COURSE_EXIST);
                }
            }
        }
        cSelectionMapper.deleteBatch(ids);
    }

    /**
     * 添加课程选择信息
     */
    @Transactional
    public void addBasic(CSelectionDTO courseSectionDTO) {
        if (cSelectionMapper.ExistsById(courseSectionDTO.getCourseId())) {
            throw new BusinessException(BusinessErrorEnum.COURSE_EXIST);
        }
        courseSectionDTO.setStatus(3);
        cSelectionMapper.addInfo(courseSectionDTO);
    }

    /**
     * 更新课程选择信息
     */
    @Transactional
    public void updateBasic(CSelectionDTO courseSectionDTO) {
        cSelectionMapper.updateInfo(courseSectionDTO);
    }

    /**
     * 添加课程选择详情信息
     */
    @Transactional
    public void addDetails(CSDetailsDTO csDetailsDTO) {
        // 检查课程创建时间范围
        String semester = csDetailsDTO.getSemester() != null ? csDetailsDTO.getSemester() : generateCurrentSemester();
        if (!semesterTimeRangeService.isInCourseCreateTimeRange(semester)) {
            throw new ValidationException("当前不在课程创建时间范围内");
        }
        
        validateCSDetailsDTO(csDetailsDTO);
        validateTeacherExists(csDetailsDTO.getTeacherId());
        validateTeacherCourseMatchAndNumber(csDetailsDTO);

        // 二进制转换
        Long courseNumber = CourseUtils.createNumber(csDetailsDTO.getCycle(), csDetailsDTO.getCourseTotal());
        Long courseTime = CourseUtils.createTime(csDetailsDTO.getWeekTime());

        // 检查时间冲突
        checkTimeConflict(csDetailsDTO.getId(),csDetailsDTO.getTeacherId(), courseTime, courseNumber, null);

        CourseSelection courseSelection = buildCourseSelectionFromDTO(csDetailsDTO, courseNumber, courseTime);
        cSelectionMapper.addDetails(courseSelection);
    }

    /**
     * 更新课程选择详情信息
     */
    @Transactional
    public void updateDetails(CSDetailsDTO csDetailsDTO) {
        // 检查课程创建时间范围
        String semester = csDetailsDTO.getSemester() != null ? csDetailsDTO.getSemester() : generateCurrentSemester();
        if (!semesterTimeRangeService.isInCourseCreateTimeRange(semester)) {
            throw new ValidationException("当前不在课程创建时间范围内");
        }
        
        validateTeacherExists(csDetailsDTO.getTeacherId());
        validateTeacherCourseMatchAndNumber(csDetailsDTO);

        Long courseNumber = Optional.ofNullable(csDetailsDTO.getCycle())
                .map(cycle -> CourseUtils.createNumber(cycle, csDetailsDTO.getCourseTotal()))
                .orElse(null);
        Long courseTime = Optional.ofNullable(csDetailsDTO.getWeekTime())
                .map(CourseUtils::createTime)
                .orElse(null);

        // 检查时间冲突
        if (courseTime != null && courseNumber != null) {
            checkTimeConflict(csDetailsDTO.getId(),csDetailsDTO.getTeacherId(), courseTime, courseNumber, csDetailsDTO.getSemester());
        }

        CourseSelection courseSelection = new CourseSelection();
        BeanUtils.copyProperties(csDetailsDTO, courseSelection);
        courseSelection.setCourseNumber(courseNumber);
        courseSelection.setCourseTime(courseTime);
        cSelectionMapper.updateDetails(courseSelection);
    }

//  学生 -- 课程
    public PageInfo<StSelectionVO> selectStudent(StSelectionPageQueryDTO sSelectionDTO){
        PageHelper.startPage(sSelectionDTO.getPageNum(), sSelectionDTO.getPageSize());

        // 执行查询获取学生选课列表
        List<StSType> studentSelections = sSelectionMapper.selectStudent(sSelectionDTO);

        // 转换为StSelectionVO并根据courseName分类
        List<StSelectionVO> resultList = studentSelections.stream()
                // 按课程名称分组
                .collect(java.util.stream.Collectors.groupingBy(StSType::getCourseName))
                // 将分组结果转换为StSelectionVO对象
                .entrySet().stream()
                .map(entry -> {
                    StSelectionVO vo = new StSelectionVO();
                    vo.setCourseName(entry.getKey());
                    // 从第一个学生记录中提取教师信息
                    if (!entry.getValue().isEmpty()) {
                        StSType firstStudent = entry.getValue().get(0);
                        vo.setTeacherName(firstStudent.getTeacherName());
                        // 暂时设置课程状态为0，实际应从数据库获取
                        vo.setCourseStatus(firstStudent.getCourseStatus());
                    }
                    // 转换StSType列表为StSInfo列表
                    List<StSelectionVO.StSInfo> stSInfoList = entry.getValue().stream()
                            .map(stSType -> {
                                StSelectionVO.StSInfo stSInfo = new StSelectionVO.StSInfo();
                                stSInfo.setId(stSType.getId());
                                stSInfo.setStudentName(stSType.getStudentName());
                                stSInfo.setOrdinaryCredit(stSType.getOrdinaryCredit());
                                stSInfo.setExamCredit(stSType.getExamCredit());
                                stSInfo.setTotalCredit(stSType.getTotalCredit());
                                stSInfo.setStatus(stSType.getStatus());
                                return stSInfo;
                            })
                            .collect(java.util.stream.Collectors.toList());
                    vo.setStSInfo(stSInfoList);
                    return vo;
                })
                .collect(java.util.stream.Collectors.toList());

        return PageInfo.of(resultList);
    }

    @Override
    public void updateStudentCredit(List<StSType> stSTypeList) {
        if (stSTypeList == null || stSTypeList.isEmpty()) {
            throw new ValidationException("学生学分更新列表不能为空");
        }

        // 验证每个学生的学分数据
        for (StSType stSType : stSTypeList) {
            if (stSType.getId() == null) {
                throw new ValidationException("学生选课ID不能为空");
            }

            if (stSType.getOrdinaryCredit() != null &&
                (stSType.getOrdinaryCredit().compareTo(BigDecimal.ZERO) < 0 ||
                 stSType.getOrdinaryCredit().compareTo(new BigDecimal("100")) > 0)) {
                throw new ValidationException("平时学分必须在0-100之间");
            }

            if (stSType.getExamCredit() != null &&
                (stSType.getExamCredit().compareTo(BigDecimal.ZERO) < 0 ||
                 stSType.getExamCredit().compareTo(new BigDecimal("100")) > 0)) {
                throw new ValidationException("考试学分必须在0-100之间");
            }
        }

        // 批量更新学生学分
        sSelectionMapper.updateStudentCredit(stSTypeList);
    }

    @Override
    public void deleteStudent(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ValidationException("删除ID列表不能为空");
        }
        // 先获取要删除的记录对应的课程ID
        List<Integer> courseIds = sSelectionMapper.selectCourseIdsByIds(ids);
        // 删除学生选课记录
        sSelectionMapper.deleteStudent(ids);
        // 更新对应课程的已选人数
        for (Integer courseId : courseIds) {
            cSelectionMapper.updateCourseSelectionCount(courseId, -1);
        }
    }

    @Override
    public void updateStatus(Integer id, Integer status) {
        if (id == null) {
            throw new ValidationException("课程ID不能为空");
        }
        if (status == null) {
            throw new ValidationException("课程状态不能为空");
        }
        CourseSelection courseSelection = cSelectionMapper.selectById(id);
        if (courseSelection == null) {
            throw new BusinessException(BusinessErrorEnum.REQUEST_PARAM_ERROR, "课程不存在");
        }
        cSelectionMapper.updateStatus(id, status);
    }

    /**
     * 验证CSDetailsDTO的必要字段
     */
    private void validateCSDetailsDTO(CSDetailsDTO csDetailsDTO) {
        if (Objects.isNull(csDetailsDTO.getCourseId()) ||
                Objects.isNull(csDetailsDTO.getTeacherId()) ||
                Objects.isNull(csDetailsDTO.getCycle()) ||
                Objects.isNull(csDetailsDTO.getWeekTime()) ||
                Objects.isNull(csDetailsDTO.getNumber()) ||
                Objects.isNull(csDetailsDTO.getCourseTotal())) {
            throw new ValidationException("课程ID或教师ID或周次或课次或最大选课人数不能为空");
        }
    }

    /**
     * 验证教师是否存在
     */
    private void validateTeacherExists(Integer teacherId) {
        if (!teacherMapper.existsById(teacherId)) {
            throw new BusinessException(BusinessErrorEnum.USER_NOT_EXIST);
        }
    }

    /**
     * 验证教师与课程的匹配及选课人数
     */
    private void validateTeacherCourseMatchAndNumber(CSDetailsDTO csDetailsDTO) {
        boolean contrastTC = cSelectionMapper.contrastTC(csDetailsDTO.getTeacherId(), csDetailsDTO.getCourseId());
        Boolean checkNumber = cSelectionMapper.checkNumber(csDetailsDTO.getCourseId(), csDetailsDTO.getNumber());
        if (!contrastTC || checkNumber == null || !checkNumber) {
            throw new ValidationException("教师与课程学院不匹配或选课人数超过最大人数");
        }
    }

    /**
     * 检查时间冲突
     */
    private void checkTimeConflict(Integer courseId, Integer teacherId, Long courseTime, Long courseNumber, String semester) {
        String currentSemester = semester != null ? semester : generateCurrentSemester();
        Integer conflictId = cSelectionMapper.selectId(teacherId, courseTime, courseNumber, currentSemester);
        if (conflictId != null && !conflictId.equals(courseId)) {
            throw new ValidationException("选课时间冲突");
        }
    }

    /**
     * 生成当前学期信息
     */
    private String generateCurrentSemester() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int startYear, endYear;
        String semester;
        
        // 9月到次年8月为一个学年
        if (month >= 9) {
            startYear = year;
            endYear = year + 1;
            semester = "一";
        } else {
            startYear = year - 1;
            endYear = year;
            semester = "二";
        }
        
        return String.format("%d-%d学年第%s学期", startYear, endYear, semester);
    }

    /**
     * 从DTO构建CourseSelection对象
     */
    private CourseSelection buildCourseSelectionFromDTO(CSDetailsDTO csDetailsDTO, Long courseNumber, Long courseTime) {
        CourseSelection courseSelection = new CourseSelection();
        BeanUtils.copyProperties(csDetailsDTO, courseSelection, "id");

        // 根据创建时间后端生成学期信息，string类型，格式 （本年 - 明年学年第几学期） 6-12月创建为第一学期，1-6月为第二学期
        if (courseSelection.getSemester() == null) {
            courseSelection.setSemester(generateCurrentSemester());
        }

        courseSelection.setCreatedTime(LocalDateTime.now());
        courseSelection.setCourseNumber(courseNumber);
        courseSelection.setCourseTime(courseTime);

        return courseSelection;
    }


}