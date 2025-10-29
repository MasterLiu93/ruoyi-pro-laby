package com.laby.module.wms.controller.admin.stocktaking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 盘点计划 Response VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 盘点计划 Response VO")
@Data
public class StockTakingPlanRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "计划编号", example = "PLAN202510280001")
    private String planNo;

    @Schema(description = "计划名称", example = "2025年10月全盘计划")
    private String planName;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "上海仓库")
    private String warehouseName;

    @Schema(description = "盘点类型", example = "1")
    private Integer takingType;

    @Schema(description = "范围类型", example = "1")
    private Integer scopeType;

    @Schema(description = "范围值", example = "[1,2,3]")
    private String scopeValue;

    @Schema(description = "计划开始时间")
    private LocalDateTime planStartTime;

    @Schema(description = "计划结束时间")
    private LocalDateTime planEndTime;

    @Schema(description = "实际开始时间")
    private LocalDateTime actualStartTime;

    @Schema(description = "实际结束时间")
    private LocalDateTime actualEndTime;

    @Schema(description = "盘点总数", example = "100")
    private Integer totalCount;

    @Schema(description = "已完成数", example = "80")
    private Integer completedCount;

    @Schema(description = "差异数", example = "5")
    private Integer diffCount;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "审核人", example = "张三")
    private String auditUser;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "备注", example = "年度盘点")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

