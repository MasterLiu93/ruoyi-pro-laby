package com.laby.module.wms.controller.admin.outbound.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库单 Response VO
 * 用于返回出库单详情和列表
 *
 * @author laby
 */
@Schema(description = "管理后台 - 出库单 Response VO")
@Data
public class OutboundRespVO {

    @Schema(description = "出库单ID", example = "1")
    private Long id;

    @Schema(description = "出库单号", example = "OUT20251028001")
    private String outboundNo;

    @Schema(description = "出库类型：1-销售出库，2-调拨出库，3-退货出库，4-其他出库", example = "1")
    private Integer outboundType;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称（关联查询字段）", example = "北京仓")
    private String warehouseName;

    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @Schema(description = "客户名称", example = "xx客户")
    private String customerName;

    @Schema(description = "出库状态：1-待审核，2-已审核，3-拣货中，4-待发货，5-已发货，6-已取消", example = "1")
    private Integer status;

    @Schema(description = "预计发货时间", example = "2025-10-28 10:00:00")
    private LocalDateTime expectedShipmentTime;

    @Schema(description = "实际发货时间", example = "2025-10-28 11:00:00")
    private LocalDateTime actualShipmentTime;

    @Schema(description = "总数量", example = "500")
    private BigDecimal totalQuantity;

    @Schema(description = "已拣货数量", example = "500")
    private BigDecimal pickedQuantity;

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

    @Schema(description = "备注", example = "客户急单")
    private String remark;

    @Schema(description = "创建时间", example = "2025-10-28 08:00:00")
    private LocalDateTime createTime;

    @Schema(description = "出库明细列表")
    private List<OutboundItemRespVO> items;

}

