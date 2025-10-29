package com.laby.module.wms.controller.admin.inventory.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存流水 Response VO
 * 
 * 功能说明：
 * - 返回库存流水的详细信息
 * - 包含关联的仓库名、商品名等字段
 * - 用于列表展示和详情查看
 * 
 * 关联字段说明：
 * - warehouseName：通过 warehouseId 关联查询
 * - goodsName/skuCode：通过 goodsId 关联查询
 * - locationCode：通过 locationId 关联查询
 * - 这些字段由 Service 层填充，不对应数据库字段
 *
 * @author laby
 */
@Schema(description = "管理后台 - 库存流水 Response VO")
@Data
public class InventoryLogRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

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

    @Schema(description = "库位ID", example = "1")
    private Long locationId;

    @Schema(description = "库位编码（关联查询字段）", example = "BJ-A01-01-01")
    private String locationCode;

    @Schema(description = "批次号", example = "BATCH-20250128")
    private String batchNo;

    @Schema(description = "操作类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "INBOUND")
    private String operationType;

    @Schema(description = "操作前数量", example = "100.00")
    private BigDecimal quantityBefore;

    @Schema(description = "变化数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "50.00")
    private BigDecimal quantityChange;

    @Schema(description = "操作后数量", example = "150.00")
    private BigDecimal quantityAfter;

    @Schema(description = "业务类型", example = "PURCHASE")
    private String businessType;

    @Schema(description = "业务单号", example = "PO-20250128001")
    private String businessNo;

    @Schema(description = "操作人", example = "admin")
    private String operator;

    @Schema(description = "备注", example = "采购入库")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

