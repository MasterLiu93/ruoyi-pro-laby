package com.laby.module.wms.controller.admin.inbound.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 入库单保存 Request VO
 * 用于创建和修改入库单
 *
 * 业务说明：
 * - 创建时不需要传入id和inboundNo（自动生成）
 * - 修改时必须传入id
 * - 必须包含至少一条明细
 *
 * @author laby
 */
@Schema(description = "管理后台 - 入库单保存 Request VO")
@Data
public class InboundSaveReqVO {

    @Schema(description = "入库单ID（修改时必传）", example = "1")
    private Long id;

    @Schema(description = "入库单号（系统自动生成）", example = "IN20251028001")
    private String inboundNo;

    @Schema(description = "入库类型：1-采购入库，2-退货入库，3-调拨入库，4-其他入库", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "入库类型不能为空")
    private Integer inboundType;

    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    @Schema(description = "供应商ID（采购入库时必填）", example = "1")
    private Long supplierId;

    @Schema(description = "供应商名称", example = "xx供应商")
    private String supplierName;

    @Schema(description = "预计到货时间", example = "2025-10-28 10:00:00")
    private LocalDateTime expectedArrivalTime;

    @Schema(description = "备注", example = "批量采购")
    private String remark;

    @Schema(description = "入库明细列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "入库明细不能为空")
    private List<InboundItemSaveReqVO> items;

}

