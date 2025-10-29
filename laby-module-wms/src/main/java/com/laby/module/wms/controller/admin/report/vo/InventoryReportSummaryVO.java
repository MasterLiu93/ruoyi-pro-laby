package com.laby.module.wms.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 库存报表汇总 VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 库存报表汇总 VO")
@Data
public class InventoryReportSummaryVO {

    @Schema(description = "总商品种类数", example = "100")
    private Integer totalGoodsCount;

    @Schema(description = "总库存数量", example = "10000.00")
    private BigDecimal totalQuantity;

    @Schema(description = "可用库存数量", example = "8000.00")
    private BigDecimal availableQuantity;

    @Schema(description = "锁定库存数量", example = "2000.00")
    private BigDecimal lockedQuantity;

    @Schema(description = "低库存商品数", example = "10")
    private Integer lowStockCount;

    @Schema(description = "零库存商品数", example = "5")
    private Integer zeroStockCount;

}

