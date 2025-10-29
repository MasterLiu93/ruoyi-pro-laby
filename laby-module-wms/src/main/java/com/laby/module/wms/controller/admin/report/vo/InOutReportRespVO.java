package com.laby.module.wms.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 出入库统计 Response VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 出入库统计 Response VO")
@Data
public class InOutReportRespVO {

    @Schema(description = "统计日期", example = "2025-10-28")
    private LocalDate statisticDate;

    @Schema(description = "入库数量", example = "100.00")
    private BigDecimal inboundQuantity;

    @Schema(description = "入库单数", example = "10")
    private Integer inboundOrderCount;

    @Schema(description = "出库数量", example = "80.00")
    private BigDecimal outboundQuantity;

    @Schema(description = "出库单数", example = "8")
    private Integer outboundOrderCount;

    @Schema(description = "净库存变化", example = "20.00")
    private BigDecimal netChange;

}

