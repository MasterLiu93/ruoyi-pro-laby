package com.laby.module.wms.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 出入库统计汇总 VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 出入库统计汇总 VO")
@Data
public class InOutReportSummaryVO {

    @Schema(description = "总入库数量", example = "1000.00")
    private BigDecimal totalInboundQuantity;

    @Schema(description = "总入库单数", example = "50")
    private Integer totalInboundOrderCount;

    @Schema(description = "总出库数量", example = "800.00")
    private BigDecimal totalOutboundQuantity;

    @Schema(description = "总出库单数", example = "40")
    private Integer totalOutboundOrderCount;

    @Schema(description = "净库存变化", example = "200.00")
    private BigDecimal netChange;

    @Schema(description = "库存周转率（%）", example = "80.00")
    private BigDecimal turnoverRate;

}

