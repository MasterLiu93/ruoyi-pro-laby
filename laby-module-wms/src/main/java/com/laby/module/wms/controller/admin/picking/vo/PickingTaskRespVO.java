package com.laby.module.wms.controller.admin.picking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拣货任务 Response VO
 * 
 * 用于列表展示和详情查看
 *
 * @author laby
 */
@Schema(description = "管理后台 - 拣货任务 Response VO")
@Data
public class PickingTaskRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "任务编号", example = "PICK-20250101-0001")
    private String taskNo;

    @Schema(description = "波次ID", example = "1")
    private Long waveId;

    @Schema(description = "波次号", example = "WAVE-20250101-0001")
    private String waveNo;

    @Schema(description = "出库单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long outboundId;

    @Schema(description = "出库单号", example = "OUT-20250101-0001")
    private String outboundNo;

    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "中心仓库")
    private String warehouseName;

    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long goodsId;

    @Schema(description = "SKU编码", example = "SKU001")
    private String skuCode;

    @Schema(description = "商品名称", example = "华为 Mate 60 Pro")
    private String goodsName;

    @Schema(description = "批次号", example = "BATCH-20250101")
    private String batchNo;

    @Schema(description = "库位ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long locationId;

    @Schema(description = "库位编码", example = "A01-01-01")
    private String locationCode;

    @Schema(description = "计划拣货数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    private BigDecimal planQuantity;

    @Schema(description = "实际拣货数量", example = "100.00")
    private BigDecimal actualQuantity;

    @Schema(description = "拣货顺序", example = "1")
    private Integer sortOrder;

    @Schema(description = "拣货员ID", example = "1")
    private Long pickerId;

    @Schema(description = "拣货员姓名", example = "张三")
    private String pickerName;

    @Schema(description = "拣货时间", example = "2025-01-01 10:30:00")
    private LocalDateTime pickingTime;

    @Schema(description = "拣货任务状态（1-待拣货，2-拣货中，3-已完成，4-异常）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "异常类型（1-库位空, 2-库存不足, 3-商品损坏, 4-商品过期, 5-拣错商品）", example = "2")
    private Integer exceptionType;

    @Schema(description = "异常说明", example = "库位库存不足，只有50件")
    private String exceptionRemark;

    @Schema(description = "备注", example = "优先拣货")
    private String remark;

    @Schema(description = "创建时间", example = "2025-01-01 10:00:00")
    private LocalDateTime createTime;
}

