package com.laby.module.wms.controller.admin.picking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 拣货波次创建/更新 Request VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 拣货波次创建/更新 Request VO")
@Data
public class PickingWaveSaveReqVO {

    @Schema(description = "波次ID（更新时必填）", example = "1")
    private Long id;

    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;

    @Schema(description = "波次类型（1-批次拣货, 2-分区拣货, 3-单品拣货）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "波次类型不能为空")
    private Integer waveType;

    @Schema(description = "出库单ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "出库单列表不能为空")
    private List<Long> outboundIds;

    @Schema(description = "优先级（1-普通, 2-紧急, 3-特急）", example = "1")
    private Integer priority;

    @Schema(description = "备注", example = "紧急订单，优先拣货")
    private String remark;
}

