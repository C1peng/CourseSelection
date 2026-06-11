package com.example.mapper;

import com.example.entity.BO.CreditType;
import com.example.entity.CreditLine;
import com.example.entity.DTO.CreditLinePageQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CreditLineMapper {
    /**
     * 根据学院、年级和排序查询学分要求
     * @param deptId 学院编号
     * @param grade 年级
     * @param sort 排序
     * @return 学分要求
     */
    CreditLine selectByDeptAndGradeAndSort(
            @Param("deptId") String deptId,
            @Param("grade") String grade,
            @Param("sort") Integer sort
    );

    /**
     * 新增学分要求
     * @param creditLine 学分要求
     */
    void insert(CreditLine creditLine);

    /**
     * 更新学分要求
     * @param creditLine 学分要求
     */
    void update(CreditLine creditLine);

    /**
     * 删除学分要求
     * @param id 主键
     */
    void deleteById(Integer id);

    /**
     * 根据ID查询学分要求
     * @param id 主键
     * @return 学分要求
     */
    CreditLine selectById(Integer id);

    /**
     * 查询所有学分要求
     * @return 学分要求列表
     */
    List<CreditLine> selectAll();

    /**
     * 分页查询学分要求
     * @param creditLinePageQueryDTO 查询条件
     * @return 学分要求列表
     */
    List<CreditLine> selectByCondition(CreditLinePageQueryDTO creditLinePageQueryDTO);

    /**
     * 批量删除学分要求
     * @param ids 主键列表
     */
    void deleteBatch(List<Integer> ids);

    /**
     * 查询总计ID
     * @param deptId 学院编号
     * @param grade 年级
     * @param sort 排序
     * @return 总计ID
     */
    Integer selectTotalId(
            @Param("deptId") String deptId,
            @Param("grade") String grade,
            @Param("sort") Integer sort
    );

    /**
     * 批量更新学分要求
     * @param grade 年级
     * @param creditTypes 学分类型列表
     */
    void updateBatch(
            @Param("grade") String grade,
            @Param("creditTypes") List<CreditType> creditTypes
    );

    /**
     * 检查学院和年级是否存在
     * @param deptId 学院编号
     * @param grade 年级
     * @return 是否存在
     */
    boolean existsByDIdAndGrade(
            @Param("deptId") String deptId,
            @Param("grade") String grade
    );

    /**
     * 批量添加学分要求
     * @param creditLines 学分要求列表
     */
    void add(List<CreditLine> creditLines);
}