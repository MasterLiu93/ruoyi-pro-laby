package com.laby.module.wms.controller.admin.stocktaking.vo;

import com.laby.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.laby.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 盘点单分页查询 Request VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 盘点单分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StockTakingPageReqVO extends PageParam {

    @Schema(description = "盘点单号", example = "TAKE202510280001")
    private String takingNo;

    @Schema(description = "盘点计划ID", example = "1")
    private Long planId;

    @Schema(description = "计划编号", example = "PLAN202510280001")
    private String planNo;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "库位ID", example = "1")
    private Long locationId;

    @Schema(description = "商品名称", example = "iPhone 15 Pro Max")
    private String goodsName;

    @Schema(description = "SKU编码", example = "SKU001")
    private String skuCode;

    @Schema(description = "批次号", example = "BATCH20251028")
    private String batchNo;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

