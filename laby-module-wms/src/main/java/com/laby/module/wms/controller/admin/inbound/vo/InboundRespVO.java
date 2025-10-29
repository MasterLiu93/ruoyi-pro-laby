package com.laby.module.wms.controller.admin.inbound.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 入库单 Response VO
 * 用于返回入库单详情和列表
 *
 * @author laby
 */
@Schema(description = "管理后台 - 入库单 Response VO")
@Data
public class InboundRespVO {

    @Schema(description = "入库单ID", example = "1")
    private Long id;

    @Schema(description = "入库单号", example = "IN20251028001")
    private String inboundNo;

    @Schema(description = "入库类型：1-采购入库，2-退货入库，3-调拨入库，4-其他入库", example = "1")
    private Integer inboundType;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称（关联查询字段）", example = "北京仓")
    private String warehouseName;

    @Schema(description = "供应商ID", example = "1")
    private Long supplierId;

    @Schema(description = "供应商名称", example = "xx供应商")
    private String supplierName;

    @Schema(description = "入库状态：1-待审核，2-已审核，3-收货中，4-已完成，5-已取消", example = "1")
    private Integer status;

    @Schema(description = "预计到货时间", example = "2025-10-28 10:00:00")
    private LocalDateTime expectedArrivalTime;

    @Schema(description = "实际到货时间", example = "2025-10-28 11:00:00")
    private LocalDateTime actualArrivalTime;

    @Schema(description = "总数量", example = "500")
    private BigDecimal totalQuantity;

    @Schema(description = "已收货数量", example = "500")
    private BigDecimal receivedQuantity;

    @Schema(description = "总金额", example = "10000.00")
    private BigDecimal totalAmount;

    @Schema(description = "审核人ID", example = "1")
    private Long auditBy;

    @Schema(description = "审核人名称", example = "admin")
    private String auditByName;

    @Schema(description = "审核时间", example = "2025-10-28 09:00:00")
    private LocalDateTime auditTime;

    @Schema(description = "完成人ID", example = "1")
    private Long completeBy;

    @Schema(description = "完成人名称", example = "admin")
    private String completeByName;

    @Schema(description = "完成时间", example = "2025-10-28 15:00:00")
    private LocalDateTime completeTime;

    @Schema(description = "备注", example = "批量采购")
    private String remark;

    @Schema(description = "创建时间", example = "2025-10-28 08:00:00")
    private LocalDateTime createTime;

    @Schema(description = "入库明细列表")
    private List<InboundItemRespVO> items;

}

