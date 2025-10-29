package com.laby.module.wms.controller.admin.carrier.vo;

import com.laby.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.laby.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 承运商信息分页查询 Request VO
 * 
 * 功能说明：
 * - 支持多条件组合查询
 * - 承运商编码和名称支持模糊查询
 * - 承运商类型和状态支持精确查询
 * 
 * @author laby
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理后台 - 承运商信息分页查询 Request VO")
public class CarrierPageReqVO extends PageParam {

    /**
     * 承运商编码（模糊搜索）
     */
    @Schema(description = "承运商编码", example = "CARRIER-001")
    private String carrierCode;

    /**
     * 承运商名称（模糊搜索）
     */
    @Schema(description = "承运商名称", example = "顺丰")
    private String carrierName;

    /**
     * 承运商类型（精确搜索）
     * 1-快递，2-物流，3-专线
     */
    @Schema(description = "承运商类型", example = "1")
    private Integer carrierType;

    /**
     * 状态（精确搜索）
     * 1-启用，0-禁用
     */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /**
     * 创建时间范围
     */
    @Schema(description = "创建时间范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}

