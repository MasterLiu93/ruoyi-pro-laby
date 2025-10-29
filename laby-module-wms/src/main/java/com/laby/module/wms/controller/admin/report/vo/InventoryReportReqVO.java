package com.laby.module.wms.controller.admin.report.vo;

import com.laby.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 库存报表查询 Request VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 库存报表查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InventoryReportReqVO extends PageParam {

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "商品分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "商品名称", example = "iPhone 15 Pro Max")
    private String goodsName;

    @Schema(description = "SKU编码", example = "SKU001")
    private String skuCode;

    @Schema(description = "是否低库存（true-仅显示低于安全库存的商品）", example = "false")
    private Boolean lowStock;

}

