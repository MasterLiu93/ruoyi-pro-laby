package com.laby.module.wms.controller.admin.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存信息 Response VO
 * 用于返回库存详情和列表
 * 
 * 说明：
 * - 包含关联查询字段（仓库名、库位编码、商品名等）
 * - 关联字段由 Service 层填充，不对应数据库字段
 * 
 * @author laby
 */
@Schema(description = "管理后台 - 库存信息 Response VO")
@Data
public class InventoryRespVO {

    @Schema(description = "库存ID", example = "1")
    private Long id;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    /**
     * 仓库名称（关联查询字段）
     * 不对应数据库字段，由 Service 层填充
     */
    @Schema(description = "仓库名称", example = "北京仓")
    private String warehouseName;

    @Schema(description = "库位ID", example = "1")
    private Long locationId;

    /**
     * 库位编码（关联查询字段）
     * 不对应数据库字段，由 Service 层填充
     */
    @Schema(description = "库位编码", example = "A01-01-01")
    private String locationCode;

    @Schema(description = "商品ID", example = "1")
    private Long goodsId;

    /**
     * 商品名称（关联查询字段）
     * 不对应数据库字段，由 Service 层填充
     */
    @Schema(description = "商品名称", example = "iPhone 15 Pro Max")
    private String goodsName;

    /**
     * SKU编码（关联查询字段）
     * 不对应数据库字段，由 Service 层填充
     */
    @Schema(description = "SKU编码", example = "SKU-001")
    private String skuCode;

    /**
     * 商品单位（关联查询字段）
     * 字典：wms_goods_unit
     */
    @Schema(description = "商品单位", example = "1")
    private Integer goodsUnit;

    @Schema(description = "批次号", example = "BATCH-20250101-001")
    private String batchNo;

    @Schema(description = "序列号", example = "SN-1234567890")
    private String serialNo;

    @Schema(description = "库存数量", example = "100.00")
    private BigDecimal quantity;

    @Schema(description = "锁定数量", example = "10.00")
    private BigDecimal lockQuantity;

    /**
     * 可用数量（虚拟列）
     * = 库存数量 - 锁定数量
     */
    @Schema(description = "可用数量", example = "90.00")
    private BigDecimal availableQuantity;

    @Schema(description = "生产日期", example = "2025-01-01")
    private LocalDate productionDate;

    @Schema(description = "过期日期", example = "2026-01-01")
    private LocalDate expireDate;

    @Schema(description = "入库日期", example = "2025-10-28 10:00:00")
    private LocalDateTime inboundDate;

    @Schema(description = "供应商ID", example = "1")
    private Long supplierId;

    @Schema(description = "供应商名称", example = "XX供应商")
    private String supplierName;

    @Schema(description = "乐观锁版本号", example = "0")
    private Integer version;

    /**
     * 库存状态
     * 字典：wms_inventory_status
     * 1-正常，2-冻结，3-待检，4-损坏
     */
    @Schema(description = "库存状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间", example = "2025-10-28 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-10-28 10:00:00")
    private LocalDateTime updateTime;

}

