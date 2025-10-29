package com.laby.module.wms.controller.admin.outbound.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 出库明细保存 Request VO
 * 用于创建和修改出库明细
 *
 * @author laby
 */
@Schema(description = "管理后台 - 出库明细保存 Request VO")
@Data
public class OutboundItemSaveReqVO {

    @Schema(description = "明细ID（修改时必传）", example = "1")
    private Long id;

    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "商品不能为空")
    private Long goodsId;

    @Schema(description = "库位ID", example = "1")
    private Long locationId;

    @Schema(description = "批次号", example = "BATCH20251028001")
    private String batchNo;

    @Schema(description = "序列号", example = "SN20251028001")
    private String serialNo;

    @Schema(description = "计划数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "计划数量不能为空")
    private BigDecimal planQuantity;

    @Schema(description = "单价", example = "10.50")
    private BigDecimal price;

    @Schema(description = "备注", example = "易碎品")
    private String remark;

}

