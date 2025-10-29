package com.laby.module.wms.controller.admin.stocktaking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 盘点单 Response VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 盘点单 Response VO")
@Data
public class StockTakingRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "盘点单号", example = "TAKE202510280001")
    private String takingNo;

    @Schema(description = "盘点计划ID", example = "1")
    private Long planId;

    @Schema(description = "计划编号", example = "PLAN202510280001")
    private String planNo;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "上海仓库")
    private String warehouseName;

    @Schema(description = "库位ID", example = "1")
    private Long locationId;

    @Schema(description = "库位编码", example = "A-01-01-01")
    private String locationCode;

    @Schema(description = "商品ID", example = "1")
    private Long goodsId;

    @Schema(description = "SKU编码", example = "SKU001")
    private String skuCode;

    @Schema(description = "商品名称", example = "iPhone 15 Pro Max")
    private String goodsName;

    @Schema(description = "批次号", example = "BATCH20251028")
    private String batchNo;

    @Schema(description = "账面数量", example = "100.00")
    private BigDecimal bookQuantity;

    @Schema(description = "实盘数量", example = "98.00")
    private BigDecimal actualQuantity;

    @Schema(description = "差异数量", example = "-2.00")
    private BigDecimal diffQuantity;

    @Schema(description = "差异原因", example = "货损")
    private String diffReason;

    @Schema(description = "盘点人", example = "张三")
    private String operator;

    @Schema(description = "盘点时间")
    private LocalDateTime operateTime;

    @Schema(description = "复核人", example = "李四")
    private String reviewer;

    @Schema(description = "复核时间")
    private LocalDateTime reviewTime;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "需要二次复核")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

