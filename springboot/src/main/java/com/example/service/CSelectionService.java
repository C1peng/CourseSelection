package com.example.service;

import com.example.entity.BO.StSType;
import com.example.entity.DTO.CSDetailsDTO;
import com.example.entity.DTO.CSelectionPageQueryDTO;
import com.example.entity.DTO.CSelectionDTO;
import com.example.entity.DTO.StSelectionPageQueryDTO;
import com.example.entity.VO.admin.AdminCSDetailsVO;
import com.example.entity.VO.StSelectionVO;
import com.example.entity.VO.admin.AdminCSelectionVO;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface CSelectionService {
    PageInfo<AdminCSelectionVO> selectPage(CSelectionPageQueryDTO cSelectionDTO);

    PageInfo<AdminCSDetailsVO> selectDetailPage(CSelectionPageQueryDTO cSelectionDTO);

    void addBasic(CSelectionDTO courseSectionDTO);

    void updateBasic(CSelectionDTO courseSectionDTO);

    void addDetails(CSDetailsDTO csDetailsDTO);

    void updateDetails(CSDetailsDTO csDetailsDTO);

    void deleteBatch(List<Integer> ids);

    PageInfo<StSelectionVO> selectStudent(StSelectionPageQueryDTO sSelectionDTO);

    void updateStudentCredit(List<StSType> stSTypeList);

    void deleteStudent(List<Integer> ids);

    void updateStatus(Integer id, Integer status);
}
