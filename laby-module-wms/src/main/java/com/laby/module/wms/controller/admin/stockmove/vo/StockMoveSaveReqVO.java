package com.laby.module.wms.controller.admin.stockmove.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 移库单创建/更新 Request VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 移库单创建/更新 Request VO")
@Data
public class StockMoveSaveReqVO {

    @Schema(description = "主键ID（更新时必填）", example = "1")
    private Long id;

    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    @Schema(description = "移库类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "移库类型不能为空")
    private Integer moveType;

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

    @Schema(description = "源库位ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "源库位不能为空")
    private Long fromLocationId;

    @Schema(description = "源库位编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "A-01-01-01")
    @NotBlank(message = "源库位编码不能为空")
    private String fromLocationCode;

    @Schema(description = "目标库位ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "目标库位不能为空")
    private Long toLocationId;

    @Schema(description = "目标库位编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "B-01-01-01")
    @NotBlank(message = "目标库位编码不能为空")
    private String toLocationCode;

    @Schema(description = "移库数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "移库数量不能为空")
    @DecimalMin(value = "0.01", message = "移库数量必须大于0")
    private BigDecimal quantity;

    @Schema(description = "移库原因", example = "库位优化调整")
    private String moveReason;

    @Schema(description = "备注", example = "紧急移库")
    private String remark;

}

