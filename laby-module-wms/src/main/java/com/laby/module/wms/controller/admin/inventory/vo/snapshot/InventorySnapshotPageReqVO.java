package com.laby.module.wms.controller.admin.inventory.vo.snapshot;

import com.laby.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存快照分页查询 Request VO
 * 
 * 功能说明：
 * - 提供库存快照的多条件查询
 * - 支持按日期范围查询
 * - 支持按仓库、商品过滤
 * 
 * 应用场景：
 * 1. 库存快照查询页面
 * 2. 库存趋势分析
 * 3. 历史库存查询
 * 4. 库存周转率统计
 *
 * @author laby
 */
@Schema(description = "管理后台 - 库存快照分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InventorySnapshotPageReqVO extends PageParam {

    @Schema(description = "快照日期范围", example = "[\"2025-01-01\", \"2025-01-31\"]")
    private String[] snapshotDateRange;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "商品ID", example = "1")
    private Long goodsId;

}

