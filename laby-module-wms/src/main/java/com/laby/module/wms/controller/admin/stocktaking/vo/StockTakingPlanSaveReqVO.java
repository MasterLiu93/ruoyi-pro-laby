package com.laby.module.wms.controller.admin.stocktaking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 盘点计划创建/更新 Request VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 盘点计划创建/更新 Request VO")
@Data
public class StockTakingPlanSaveReqVO {

    @Schema(description = "主键ID（更新时必填）", example = "1")
    private Long id;

    @Schema(description = "计划名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025年10月全盘计划")
    @NotBlank(message = "计划名称不能为空")
    private String planName;

    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    @Schema(description = "盘点类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "盘点类型不能为空")
    private Integer takingType;

    @Schema(description = "范围类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "范围类型不能为空")
    private Integer scopeType;

    @Schema(description = "范围值（JSON数组）", example = "[1,2,3]")
    private String scopeValue;

    @Schema(description = "计划开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "计划开始时间不能为空")
    private LocalDateTime planStartTime;

    @Schema(description = "计划结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "计划结束时间不能为空")
    private LocalDateTime planEndTime;

    @Schema(description = "备注", example = "年度盘点")
    private String remark;

}

