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
 * 盘点计划分页查询 Request VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 盘点计划分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StockTakingPlanPageReqVO extends PageParam {

    @Schema(description = "计划编号", example = "PLAN202510280001")
    private String planNo;

    @Schema(description = "计划名称", example = "2025年10月全盘计划")
    private String planName;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "盘点类型", example = "1")
    private Integer takingType;

    @Schema(description = "范围类型", example = "1")
    private Integer scopeType;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

