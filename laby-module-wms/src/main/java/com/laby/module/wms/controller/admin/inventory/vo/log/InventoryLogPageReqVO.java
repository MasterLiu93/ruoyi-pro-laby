package com.laby.module.wms.controller.admin.inventory.vo.log;

import com.laby.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.laby.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 库存流水分页查询 Request VO
 * 
 * 功能说明：
 * - 提供库存流水的多条件查询
 * - 支持按仓库、商品、批次等过滤
 * - 支持按操作类型、业务类型过滤
 * - 支持按时间范围查询
 * 
 * 应用场景：
 * 1. 库存流水查询页面
 * 2. 库存变动追溯
 * 3. 业务单据关联查询
 * 4. 库存审计报表
 *
 * @author laby
 */
@Schema(description = "管理后台 - 库存流水分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InventoryLogPageReqVO extends PageParam {

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "商品ID", example = "1")
    private Long goodsId;

    @Schema(description = "库位ID", example = "1")
    private Long locationId;

    @Schema(description = "批次号", example = "BATCH-20250128")
    private String batchNo;

    @Schema(description = "操作类型", example = "INBOUND")
    private String operationType;

    @Schema(description = "业务类型", example = "PURCHASE")
    private String businessType;

    @Schema(description = "业务单号", example = "PO-20250128001")
    private String businessNo;

    @Schema(description = "创建时间范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

