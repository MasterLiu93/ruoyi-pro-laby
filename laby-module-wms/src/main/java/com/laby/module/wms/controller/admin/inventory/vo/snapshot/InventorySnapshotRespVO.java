package com.laby.module.wms.controller.admin.inventory.vo.snapshot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存快照 Response VO
 * 
 * 功能说明：
 * - 返回库存快照的详细信息
 * - 包含关联的仓库名、商品名等字段
 * - 用于列表展示和趋势分析
 * 
 * 关联字段说明：
 * - warehouseName：通过 warehouseId 关联查询
 * - goodsName/skuCode：通过 goodsId 关联查询
 * - availableQuantity：计算字段 (quantity - lockQuantity)
 * - 这些字段由 Service 层填充，不对应数据库字段
 *
 * @author laby
 */
@Schema(description = "管理后台 - 库存快照 Response VO")
@Data
public class InventorySnapshotRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "快照日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-01-28")
    private String snapshotDate;

    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称（关联查询字段）", example = "北京仓")
    private String warehouseName;

    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long goodsId;

    @Schema(description = "商品名称（关联查询字段）", example = "iPhone 15 Pro Max 256GB")
    private String goodsName;

    @Schema(description = "SKU编码（关联查询字段）", example = "SKU-PHONE-001")
    private String skuCode;

    @Schema(description = "库存数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    private BigDecimal quantity;

    @Schema(description = "锁定数量", example = "10.00")
    private BigDecimal lockQuantity;

    @Schema(description = "可用数量（计算字段）", example = "90.00")
    private BigDecimal availableQuantity;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

