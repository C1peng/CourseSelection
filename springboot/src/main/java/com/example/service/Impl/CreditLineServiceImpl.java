package com.example.service.Impl;

import com.example.common.exception.BusinessErrorEnum;
import com.example.common.exception.BusinessException;
import com.example.common.exception.ValidationException;
import com.example.entity.BO.CreditType;
import com.example.entity.CreditLine;
import com.example.entity.DTO.CreditLineDTO;
import com.example.entity.DTO.CreditLinePageQueryDTO;
import com.example.entity.Department;
import com.example.entity.VO.CreditLineVO;
import com.example.mapper.CreditLineMapper;
import com.example.mapper.DepartmentMapper;
import com.example.service.CreditLineService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CreditLineServiceImpl implements CreditLineService {
    @Autowired
    private CreditLineMapper creditLineMapper;
    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 分页查询学分信息
     */
    public PageInfo<CreditLineVO> selectPage(CreditLinePageQueryDTO creditLineDTO) {
        PageHelper.startPage(creditLineDTO.getPageNum(), creditLineDTO.getPageSize() * 5);

        // 1. 查询所有CreditLine数据
        List<CreditLine> creditLineList = creditLineMapper.selectByCondition(creditLineDTO);

        if (creditLineList.isEmpty()) {
            return new PageInfo<>(Collections.emptyList());
        }

        // 2. 提取所有deptId并去重
        Set<String> deptIds = creditLineList.stream()
                .map(CreditLine::getDeptId)
                .collect(Collectors.toSet());

        // 3. 批量查询所有department信息
        List<Department> departments = departmentMapper.selectByDeptIds(deptIds);

        // 4. 将department转换为Map，方便后续查找
        Map<String, String> deptIdToNameMap = departments.stream()
                .collect(Collectors.toMap(Department::getDeptId, Department::getDeptName));

        // 5. 转换为CreditLineVO - 修复原始代码中lambda表达式的语法错误
        List<CreditLineVO> creditLineVOList = creditLineList.stream()
                .collect(Collectors.groupingBy(cl -> cl.getDeptId() + "_" + cl.getGrade()))
                .values()
                .stream()
                .map(group -> {
                    CreditLineVO creditLineVO = new CreditLineVO();
                    CreditLine first = group.get(0);
                    creditLineVO.setDeptId(first.getDeptId());
                    // 从Map中获取deptName
                    creditLineVO.setDeptName(deptIdToNameMap.getOrDefault(first.getDeptId(), first.getDeptId()));
                    creditLineVO.setGrade(first.getGrade());

                    // 转换creditTypes
                    List<CreditType> creditTypes = group.stream().map(cl -> {
                        CreditType creditType = new CreditType();
                        creditType.setId(cl.getId());
                        creditType.setSort(cl.getSort());
                        creditType.setPublicCredit(cl.getPublicCredit());
                        creditType.setForeignLanguageCredit(cl.getForeignLanguageCredit());
                        creditType.setSportCredit(cl.getSportCredit());
                        creditType.setArtCredit(cl.getArtCredit());
                        return creditType;
                    }).collect(Collectors.toList());

                    creditLineVO.setCreditTypes(creditTypes);
                    return creditLineVO;
                })
                .collect(Collectors.toList());

        return new PageInfo<>(creditLineVOList);
    }

    /**
     * 删除学分信息
     */
    public void deleteBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ValidationException("删除ID列表不能为空");
        }
        creditLineMapper.deleteBatch(ids);
    }

    /**
     * 更新学分信息
     */
    public void update(CreditLineDTO creditLineDTO) {
        Integer id = creditLineMapper.selectTotalId(creditLineDTO.getDeptId(), creditLineDTO.getGrade(), 1);
        //判断学院与年级是否已存在，通过大一的id排除本身
        if (id != null) {
            // 从creditTypes中获取sort为1的元素的id
            Optional<Integer> sortOneId = creditLineDTO.getCreditTypes().stream()
                    .filter(ct -> ct.getSort() != null && ct.getSort().equals(1))
                    .map(CreditType::getId)
                    .findFirst();
            // 如果找到了sort为1的元素且其id与查询到的id不相等，则抛出异常
            if (sortOneId.isPresent() && !sortOneId.get().equals(id)) {
                throw new BusinessException(BusinessErrorEnum.COURSE_EXIST);
            }
        }

        // 加和学分记录总数
        List<CreditType> creditTypes = creditLineDTO.getCreditTypes();
        if (creditTypes != null && !creditTypes.isEmpty()) {
            Integer totalId = creditLineMapper.selectTotalId(creditLineDTO.getDeptId(), creditLineDTO.getGrade(), 0);

            // 使用辅助方法计算总和，提高代码可读性和可维护性
            CreditType totalCreditType = calculateTotalCreditType(creditTypes, totalId);
            creditTypes.add(totalCreditType);
        }
        creditLineMapper.updateBatch(creditLineDTO.getGrade(), creditTypes);
    }

    /**
     * 添加学分信息
     */
    @Transactional
    public void add(CreditLineDTO creditLineDTO) {
        //判断学院是否存在,不存在抛出异常
        if (!departmentMapper.existsByDeptId(creditLineDTO.getDeptId())) {
            throw new BusinessException(BusinessErrorEnum.COLLEGE_NOT_EXIST);
        }
        //判断学院与年级是否已存在,存在抛出异常
        if (creditLineMapper.existsByDIdAndGrade(creditLineDTO.getDeptId(), creditLineDTO.getGrade())) {
            throw new BusinessException(BusinessErrorEnum.COURSE_EXIST);
        }

        List<CreditType> creditTypes = creditLineDTO.getCreditTypes();

        if (creditTypes != null && !creditTypes.isEmpty()) {
            // 计算总计
            CreditType totalCreditType = calculateTotalCreditType(creditTypes, null);

            // 转换为CreditLine对象
            List<CreditLine> creditLines = creditTypes.stream()
                    .map(creditType -> convertToCreditLine(creditLineDTO.getDeptId(), creditLineDTO.getGrade(), creditType))
                    .collect(Collectors.toList());

            // 添加总计记录
            CreditLine totalCreditLine = convertToCreditLine(creditLineDTO.getDeptId(), creditLineDTO.getGrade(), totalCreditType);
            totalCreditLine.setSort(0); // 设置为0表示总计
            creditLines.add(totalCreditLine);

            // 批量插入
            creditLineMapper.add(creditLines);
        }
    }

    /**
     * 计算学分类型的总和
     * @param creditTypes 学分类型列表
     * @param totalId 总计记录的ID
     * @return 包含总计的CreditType对象
     */
    private CreditType calculateTotalCreditType(List<CreditType> creditTypes, Integer totalId) {
        // 使用四个独立变量计算总和（可读性更好）
        BigDecimal totalPublicCredit = BigDecimal.ZERO;
        BigDecimal totalForeignLanguageCredit = BigDecimal.ZERO;
        BigDecimal totalSportCredit = BigDecimal.ZERO;
        BigDecimal totalArtCredit = BigDecimal.ZERO;

        for (CreditType ct : creditTypes) {
            totalPublicCredit = totalPublicCredit.add(ct.getPublicCredit() != null ? ct.getPublicCredit() : BigDecimal.ZERO);
            totalForeignLanguageCredit = totalForeignLanguageCredit.add(ct.getForeignLanguageCredit() != null ? ct.getForeignLanguageCredit() : BigDecimal.ZERO);
            totalSportCredit = totalSportCredit.add(ct.getSportCredit() != null ? ct.getSportCredit() : BigDecimal.ZERO);
            totalArtCredit = totalArtCredit.add(ct.getArtCredit() != null ? ct.getArtCredit() : BigDecimal.ZERO);
        }

        CreditType totalCreditType = new CreditType();
        totalCreditType.setId(totalId);
        totalCreditType.setPublicCredit(totalPublicCredit);
        totalCreditType.setForeignLanguageCredit(totalForeignLanguageCredit);
        totalCreditType.setSportCredit(totalSportCredit);
        totalCreditType.setArtCredit(totalArtCredit);
        totalCreditType.setSort(0); // 设置为0表示总计

        return totalCreditType;
    }

    /**
     * 将CreditType转换为CreditLine对象
     * @param deptId 学院ID
     * @param grade 年级
     * @param creditType 学分类型对象
     * @return CreditLine对象
     */
    private CreditLine convertToCreditLine(String deptId, String grade, CreditType creditType) {
        CreditLine creditLine = new CreditLine();
        creditLine.setDeptId(deptId);
        creditLine.setGrade(grade);
        creditLine.setSort(creditType.getSort());
        creditLine.setPublicCredit(creditType.getPublicCredit());
        creditLine.setForeignLanguageCredit(creditType.getForeignLanguageCredit());
        creditLine.setSportCredit(creditType.getSportCredit());
        creditLine.setArtCredit(creditType.getArtCredit());
        return creditLine;
    }
}
