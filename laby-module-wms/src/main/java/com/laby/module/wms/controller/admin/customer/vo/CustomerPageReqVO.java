package com.laby.module.wms.controller.admin.customer.vo;

import com.laby.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.laby.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 客户信息分页查询 Request VO
 *
 * 功能说明：
 * 1. 支持按客户编码、名称进行模糊查询
 * 2. 支持按客户类型、客户等级、状态进行精确查询
 * 3. 支持按联系人、联系电话进行模糊查询
 * 4. 支持按创建时间范围查询
 *
 * @author laby
 */
@Schema(description = "管理后台 - 客户信息分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerPageReqVO extends PageParam {

    @Schema(description = "客户编码（模糊查询）", example = "CUST-001")
    private String customerCode;

    @Schema(description = "客户名称（模糊查询）", example = "XX商贸")
    private String customerName;

    @Schema(description = "客户类型（1-零售,2-批发,3-企业）", example = "1")
    private Integer customerType;

    @Schema(description = "客户等级（1-VIP,2-金牌,3-银牌,4-普通）", example = "4")
    private Integer customerLevel;

    @Schema(description = "联系人（模糊查询）", example = "李四")
    private String contactPerson;

    @Schema(description = "联系电话（模糊查询）", example = "139")
    private String contactPhone;

    @Schema(description = "状态（0-禁用,1-启用）", example = "1")
    private Integer status;

    @Schema(description = "创建时间范围", example = "[2023-10-01 00:00:00, 2023-10-31 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}

