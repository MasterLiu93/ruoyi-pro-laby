package com.laby.module.wms.controller.admin.inventory.vo.warning;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 库存预警 Response VO
 * 
 * 功能说明：
 * - 返回库存预警信息
 * - 包含低库存预警和即将过期预警
 * - 用于库存预警列表展示
 * 
 * 预警类型：
 * - LOW_STOCK：低库存预警（可用数量低于安全库存）
 * - EXPIRING：即将过期预警（距离过期日期小于7天）
 *
 * @author laby
 */
@Schema(description = "管理后台 - 库存预警 Response VO")
@Data
public class InventoryWarningRespVO {

    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "北京仓")
    private String warehouseName;

    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long goodsId;

    @Schema(description = "商品名称", example = "iPhone 15 Pro Max 256GB")
    private String goodsName;

    @Schema(description = "SKU编码", example = "SKU-PHONE-001")
    private String skuCode;

    @Schema(description = "批次号", example = "BATCH-20250128")
    private String batchNo;

    @Schema(description = "预警类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "LOW_STOCK")
    private String warningType;

    @Schema(description = "当前库存数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "5.00")
    private BigDecimal quantity;

    @Schema(description = "锁定数量", example = "2.00")
    private BigDecimal lockQuantity;

    @Schema(description = "可用数量", example = "3.00")
    private BigDecimal availableQuantity;

    @Schema(description = "安全库存", example = "10.00")
    private BigDecimal safetyStock;

    @Schema(description = "过期日期", example = "2025-02-05")
    private LocalDate expireDate;

    @Schema(description = "距离过期天数", example = "7")
    private Integer daysToExpire;

}

