package com.laby.module.wms.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.laby.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 出入库统计查询 Request VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 出入库统计查询 Request VO")
@Data
public class InOutReportReqVO {

    @Schema(description = "统计类型：1-按日，2-按周，3-按月", example = "1")
    private Integer statisticType;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime endTime;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "商品ID", example = "1")
    private Long goodsId;

    @Schema(description = "供应商ID", example = "1")
    private Long supplierId;

    @Schema(description = "客户ID", example = "1")
    private Long customerId;

}

