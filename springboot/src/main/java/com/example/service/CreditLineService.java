package com.example.service;

import com.example.entity.DTO.CreditLineDTO;
import com.example.entity.DTO.CreditLinePageQueryDTO;
import com.github.pagehelper.PageInfo;
import com.example.entity.VO.CreditLineVO;
import java.util.List;

public interface CreditLineService {

    PageInfo<CreditLineVO> selectPage(CreditLinePageQueryDTO creditLineDTO);

    void add(CreditLineDTO creditLineDTO);

    void update(CreditLineDTO creditLineDTO);

    void deleteBatch(List<Integer> ids);
}
