package com.laby.module.wms.controller.admin.outbound.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出库明细 Response VO
 * 用于返回出库明细详情和列表
 *
 * @author laby
 */
@Schema(description = "管理后台 - 出库明细 Response VO")
@Data
public class OutboundItemRespVO {

    @Schema(description = "明细ID", example = "1")
    private Long id;

    @Schema(description = "出库单ID", example = "1")
    private Long outboundId;

    @Schema(description = "商品ID", example = "1")
    private Long goodsId;

    @Schema(description = "商品名称（关联查询字段）", example = "iPhone 15")
    private String goodsName;

    @Schema(description = "SKU编码（关联查询字段）", example = "SKU-001")
    private String skuCode;

    @Schema(description = "商品单位（关联查询字段）", example = "1")
    private Integer goodsUnit;

    @Schema(description = "库位ID", example = "1")
    private Long locationId;

    @Schema(description = "库位编码（关联查询字段）", example = "A-01-01")
    private String locationCode;

    @Schema(description = "批次号", example = "BATCH20251028001")
    private String batchNo;

    @Schema(description = "序列号", example = "SN20251028001")
    private String serialNo;

    @Schema(description = "计划数量", example = "100")
    private BigDecimal planQuantity;

    @Schema(description = "已拣货数量", example = "100")
    private BigDecimal pickedQuantity;

    @Schema(description = "已发货数量", example = "100")
    private BigDecimal shippedQuantity;

    @Schema(description = "单价", example = "10.50")
    private BigDecimal price;

    @Schema(description = "金额", example = "1050.00")
    private BigDecimal amount;

    @Schema(description = "备注", example = "易碎品")
    private String remark;

    @Schema(description = "创建时间", example = "2025-10-28 08:00:00")
    private LocalDateTime createTime;

}

