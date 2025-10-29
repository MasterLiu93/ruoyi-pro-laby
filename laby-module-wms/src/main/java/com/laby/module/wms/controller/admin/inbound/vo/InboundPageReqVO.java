package com.laby.module.wms.controller.admin.inbound.vo;

import com.laby.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.laby.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 入库单分页查询 Request VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 入库单分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class InboundPageReqVO extends PageParam {

    @Schema(description = "入库单号（模糊搜索）", example = "IN20251028001")
    private String inboundNo;

    @Schema(description = "入库类型：1-采购入库，2-退货入库，3-调拨入库，4-其他入库", example = "1")
    private Integer inboundType;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "供应商ID", example = "1")
    private Long supplierId;

    @Schema(description = "入库状态：1-待审核，2-已审核，3-收货中，4-已完成，5-已取消", example = "1")
    private Integer status;

    @Schema(description = "创建时间范围", example = "[2025-10-01 00:00:00, 2025-10-31 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "预计到货时间范围", example = "[2025-10-01 00:00:00, 2025-10-31 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] expectedArrivalTime;

}

