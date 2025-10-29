package com.laby.module.wms.controller.admin.picking.vo;

import com.laby.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.laby.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 拣货波次分页查询 Request VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 拣货波次分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PickingWavePageReqVO extends PageParam {

    @Schema(description = "波次号（模糊搜索）", example = "WAVE-20250101-0001")
    private String waveNo;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "波次类型（1-批次拣货, 2-分区拣货, 3-单品拣货）", example = "1")
    private Integer waveType;

    @Schema(description = "拣货员ID", example = "1")
    private Long pickerId;

    @Schema(description = "波次状态（1-待分配，2-已分配，3-拣货中，4-已完成，5-已取消）", example = "1")
    private Integer status;

    @Schema(description = "创建时间范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}

