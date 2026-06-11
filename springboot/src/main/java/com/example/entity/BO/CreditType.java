package com.example.entity.BO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreditType {
    private Integer id;
    private Integer sort;// 排序(0.总 1.大一 2.大二 3.大三 4.大四)
    private BigDecimal publicCredit; // 公共选修课学分要求
    private BigDecimal foreignLanguageCredit; // 外语选修课学分要求
    private BigDecimal sportCredit; // 体育选修课学分要求
    private BigDecimal artCredit; // 艺术选修课学分要求
}
