package com.laby.module.wms.controller.admin.picking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 拣货波次 Response VO
 * 
 * 用于列表展示和详情查看
 *
 * @author laby
 */
@Schema(description = "管理后台 - 拣货波次 Response VO")
@Data
public class PickingWaveRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "波次号", example = "WAVE-20250101-0001")
    private String waveNo;

    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "中心仓库")
    private String warehouseName;

    @Schema(description = "波次类型（1-批次拣货, 2-分区拣货, 3-单品拣货）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer waveType;

    @Schema(description = "订单数量", example = "10")
    private Integer orderCount;

    @Schema(description = "商品种类数", example = "25")
    private Integer itemCount;

    @Schema(description = "总数量", example = "500.00")
    private BigDecimal totalQuantity;

    @Schema(description = "优先级（1-普通, 2-紧急, 3-特急）", example = "1")
    private Integer priority;

    @Schema(description = "拣货员ID", example = "1")
    private Long pickerId;

    @Schema(description = "拣货员姓名", example = "张三")
    private String pickerName;

    @Schema(description = "预计耗时（秒）", example = "3600")
    private Integer estimatedTime;

    @Schema(description = "实际耗时（秒）", example = "3200")
    private Integer actualTime;

    @Schema(description = "开始时间", example = "2025-01-01 10:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2025-01-01 11:30:00")
    private LocalDateTime endTime;

    @Schema(description = "波次状态（1-待分配，2-已分配，3-拣货中，4-已完成，5-已取消）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "备注", example = "紧急订单，优先拣货")
    private String remark;

    @Schema(description = "创建时间", example = "2025-01-01 09:00:00")
    private LocalDateTime createTime;

    @Schema(description = "关联的出库单号列表")
    private List<String> outboundNos;
}

