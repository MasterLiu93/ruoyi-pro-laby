package com.laby.module.wms.controller.admin.stocktaking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 盘点单创建/更新 Request VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 盘点单创建/更新 Request VO")
@Data
public class StockTakingSaveReqVO {

    @Schema(description = "主键ID（更新时必填）", example = "1")
    private Long id;

    @Schema(description = "盘点计划ID", example = "1")
    private Long planId;

    @Schema(description = "盘点计划编号", example = "PLAN202510280001")
    private String planNo;

    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    @Schema(description = "库位ID", example = "1")
    private Long locationId;

    @Schema(description = "库位编码", example = "A-01-01-01")
    private String locationCode;

    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "商品不能为空")
    private Long goodsId;

    @Schema(description = "SKU编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "SKU001")
    @NotBlank(message = "SKU编码不能为空")
    private String skuCode;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "iPhone 15 Pro Max")
    @NotBlank(message = "商品名称不能为空")
    private String goodsName;

    @Schema(description = "批次号", example = "BATCH20251028")
    private String batchNo;

    @Schema(description = "账面数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    @NotNull(message = "账面数量不能为空")
    @DecimalMin(value = "0", message = "账面数量不能为负数")
    private BigDecimal bookQuantity;

    @Schema(description = "实盘数量", example = "98.00")
    @DecimalMin(value = "0", message = "实盘数量不能为负数")
    private BigDecimal actualQuantity;

    @Schema(description = "差异原因", example = "货损")
    private String diffReason;

    @Schema(description = "备注", example = "需要二次复核")
    private String remark;

}

