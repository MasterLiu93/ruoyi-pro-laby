package com.laby.module.wms.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 库存报表 Response VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 库存报表 Response VO")
@Data
public class InventoryReportRespVO {

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "上海仓库")
    private String warehouseName;

    @Schema(description = "库位ID", example = "1")
    private Long locationId;

    @Schema(description = "库位编码", example = "A-01-01-01")
    private String locationCode;

    @Schema(description = "商品ID", example = "1")
    private Long goodsId;

    @Schema(description = "SKU编码", example = "SKU001")
    private String skuCode;

    @Schema(description = "商品名称", example = "iPhone 15 Pro Max")
    private String goodsName;

    @Schema(description = "商品分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "商品分类名称", example = "电子产品")
    private String categoryName;

    @Schema(description = "批次号", example = "BATCH20251028")
    private String batchNo;

    @Schema(description = "总数量", example = "100.00")
    private BigDecimal totalQuantity;

    @Schema(description = "可用数量", example = "80.00")
    private BigDecimal availableQuantity;

    @Schema(description = "锁定数量", example = "20.00")
    private BigDecimal lockedQuantity;

    @Schema(description = "安全库存", example = "50.00")
    private BigDecimal safetyStock;

    @Schema(description = "是否低库存", example = "false")
    private Boolean isLowStock;

    @Schema(description = "库存状态", example = "正常")
    private String stockStatus;

}

