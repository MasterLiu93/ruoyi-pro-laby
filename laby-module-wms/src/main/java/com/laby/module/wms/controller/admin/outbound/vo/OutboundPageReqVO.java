package com.laby.module.wms.controller.admin.outbound.vo;

import com.laby.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.laby.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 出库单分页查询 Request VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 出库单分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class OutboundPageReqVO extends PageParam {

    @Schema(description = "出库单号（模糊搜索）", example = "OUT20251028001")
    private String outboundNo;

    @Schema(description = "出库类型：1-销售出库，2-调拨出库，3-退货出库，4-其他出库", example = "1")
    private Integer outboundType;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @Schema(description = "出库状态：1-待审核，2-已审核，3-拣货中，4-待发货，5-已发货，6-已取消", example = "1")
    private Integer status;

    @Schema(description = "创建时间范围", example = "[2025-10-01 00:00:00, 2025-10-31 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "预计发货时间范围", example = "[2025-10-01 00:00:00, 2025-10-31 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] expectedShipmentTime;

}

