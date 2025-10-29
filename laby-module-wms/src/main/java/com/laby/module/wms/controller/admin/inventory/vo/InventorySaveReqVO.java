package com.laby.module.wms.controller.admin.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存信息保存 Request VO
 * 用于新增和修改库存信息
 * 
 * 使用场景：
 * - 手动调整库存
 * - 盘点后调整库存
 * - 初始化库存数据
 * 
 * 注意：
 * - 正常业务流程中的入库/出库不直接调用此接口
 * - 入库/出库有专门的业务单据流程
 * 
 * @author laby
 */
@Schema(description = "管理后台 - 库存信息保存 Request VO")
@Data
public class InventorySaveReqVO {

    /**
     * 库存ID（修改时必传）
     */
    @Schema(description = "库存ID", example = "1")
    private Long id;

    /**
     * 仓库ID（必填）
     */
    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    /**
     * 库位ID（可选）
     * 不填表示仓库级库存
     */
    @Schema(description = "库位ID", example = "1")
    private Long locationId;

    /**
     * 商品ID（必填）
     */
    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "商品不能为空")
    private Long goodsId;

    /**
     * 批次号（可选）
     * 启用批次管理的商品必填
     */
    @Schema(description = "批次号", example = "BATCH-20250101-001")
    private String batchNo;

    /**
     * 序列号（可选）
     * 启用序列号管理的商品必填
     */
    @Schema(description = "序列号", example = "SN-1234567890")
    private String serialNo;

    /**
     * 库存数量（必填）
     * 必须大于等于0
     */
    @Schema(description = "库存数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    @NotNull(message = "库存数量不能为空")
    @DecimalMin(value = "0", message = "库存数量不能为负数")
    private BigDecimal quantity;

    /**
     * 锁定数量（可选）
     * 默认为0，不能大于库存数量
     */
    @Schema(description = "锁定数量", example = "10.00")
    @DecimalMin(value = "0", message = "锁定数量不能为负数")
    private BigDecimal lockQuantity;

    /**
     * 生产日期（可选）
     * 有保质期的商品建议填写
     */
    @Schema(description = "生产日期", example = "2025-01-01")
    private LocalDate productionDate;

    /**
     * 过期日期（可选）
     * 有保质期的商品建议填写
     */
    @Schema(description = "过期日期", example = "2026-01-01")
    private LocalDate expireDate;

    /**
     * 入库日期（可选）
     * 用于先进先出策略
     */
    @Schema(description = "入库日期", example = "2025-10-28 10:00:00")
    private LocalDateTime inboundDate;

    /**
     * 供应商ID（可选）
     * 用于质量追溯
     */
    @Schema(description = "供应商ID", example = "1")
    private Long supplierId;

    /**
     * 供应商名称（可选）
     * 冗余字段，方便显示
     */
    @Schema(description = "供应商名称", example = "XX供应商")
    private String supplierName;

    /**
     * 库存状态（必填）
     * 字典：wms_inventory_status
     * 1-正常，2-冻结，3-待检，4-损坏
     */
    @Schema(description = "库存状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "库存状态不能为空")
    private Integer status;

}

