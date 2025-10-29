package com.laby.module.wms.controller.admin.outbound.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库单保存 Request VO
 * 用于创建和修改出库单
 *
 * 业务说明：
 * - 创建时不需要传入id和outboundNo（自动生成）
 * - 修改时必须传入id
 * - 必须包含至少一条明细
 *
 * @author laby
 */
@Schema(description = "管理后台 - 出库单保存 Request VO")
@Data
public class OutboundSaveReqVO {

    @Schema(description = "出库单ID（修改时必传）", example = "1")
    private Long id;

    @Schema(description = "出库单号（系统自动生成）", example = "OUT20251028001")
    private String outboundNo;

    @Schema(description = "出库类型：1-销售出库，2-调拨出库，3-退货出库，4-其他出库", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "出库类型不能为空")
    private Integer outboundType;

    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    @Schema(description = "客户ID（销售出库时必填）", example = "1")
    private Long customerId;

    @Schema(description = "客户名称", example = "xx客户")
    private String customerName;

    @Schema(description = "预计发货时间", example = "2025-10-28 10:00:00")
    private LocalDateTime expectedShipmentTime;

    @Schema(description = "备注", example = "客户急单")
    private String remark;

    @Schema(description = "出库明细列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "出库明细不能为空")
    private List<OutboundItemSaveReqVO> items;

}

