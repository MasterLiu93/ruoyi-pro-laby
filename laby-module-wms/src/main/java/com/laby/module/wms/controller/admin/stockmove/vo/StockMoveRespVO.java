package com.laby.module.wms.controller.admin.stockmove.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 移库单 Response VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 移库单 Response VO")
@Data
public class StockMoveRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "移库单号", example = "MOVE202510280001")
    private String moveNo;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "上海仓库")
    private String warehouseName;

    @Schema(description = "移库类型", example = "1")
    private Integer moveType;

    @Schema(description = "商品ID", example = "1")
    private Long goodsId;

    @Schema(description = "SKU编码", example = "SKU001")
    private String skuCode;

    @Schema(description = "商品名称", example = "iPhone 15 Pro Max")
    private String goodsName;

    @Schema(description = "批次号", example = "BATCH20251028")
    private String batchNo;

    @Schema(description = "源库位ID", example = "1")
    private Long fromLocationId;

    @Schema(description = "源库位编码", example = "A-01-01-01")
    private String fromLocationCode;

    @Schema(description = "目标库位ID", example = "2")
    private Long toLocationId;

    @Schema(description = "目标库位编码", example = "B-01-01-01")
    private String toLocationCode;

    @Schema(description = "移库数量", example = "100.00")
    private BigDecimal quantity;

    @Schema(description = "移库原因", example = "库位优化调整")
    private String moveReason;

    @Schema(description = "操作人", example = "张三")
    private String operator;

    @Schema(description = "操作时间")
    private LocalDateTime operateTime;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "紧急移库")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

