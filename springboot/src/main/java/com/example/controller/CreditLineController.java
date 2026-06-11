package com.example.controller;

import com.example.common.Result;
import com.example.common.annotation.RequiresPermissions;

import com.example.entity.DTO.CreditLineDTO;
import com.example.entity.DTO.CreditLinePageQueryDTO;
import com.example.entity.VO.CreditLineVO;
import com.example.service.CreditLineService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/creditLine")
@Slf4j
public class CreditLineController {
    @Autowired
    private CreditLineService creditLineService;
    @PostMapping("/selectPage")
    @RequiresPermissions(value = {"ALL:VIEW", "CRLINE:VIEW"})
    public Result selectPage(@RequestBody CreditLinePageQueryDTO creditLineDTO) {
        log.info("查询学分达标列表{}", creditLineDTO.toString());
        PageInfo<CreditLineVO> pageInfo = creditLineService.selectPage(creditLineDTO);
        return Result.success(pageInfo);
    }

    @PostMapping("/delete")
    @RequiresPermissions(value = {"ALL:DELETE"})
    public Result delete(@RequestBody List<Integer> ids) {
        log.info("删除学分达标信息");
        creditLineService.deleteBatch(ids);
        return Result.success();
    }

    @PostMapping("/update")
    @RequiresPermissions(value = {"ALL:EDIT"})
    public Result update(@RequestBody CreditLineDTO creditLineDTO) {
        log.info("更新学分达标信息{}", creditLineDTO.toString());
        creditLineService.update(creditLineDTO);
        return Result.success();
    }

    @PostMapping("/add")
    @RequiresPermissions(value = {"ALL:ADD"})
    public Result add(@RequestBody CreditLineDTO creditLineDTO) {
        log.info("添加学分达标信息{}", creditLineDTO.toString());
        creditLineService.add(creditLineDTO);
        return Result.success();
    }

}
