package com.laby.module.wms.controller.admin.supplier.vo;

import com.laby.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.laby.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 供应商信息分页查询 Request VO
 *
 * 功能说明：
 * 1. 支持按供应商编码、名称进行模糊查询
 * 2. 支持按供应商类型、信用等级、状态进行精确查询
 * 3. 支持按联系人、联系电话进行模糊查询
 * 4. 支持按创建时间范围查询
 *
 * @author laby
 */
@Schema(description = "管理后台 - 供应商信息分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierPageReqVO extends PageParam {

    @Schema(description = "供应商编码（模糊查询）", example = "SUP-001")
    private String supplierCode;

    @Schema(description = "供应商名称（模糊查询）", example = "华强")
    private String supplierName;

    @Schema(description = "供应商类型（1-普通,2-VIP,3-战略）", example = "1")
    private Integer supplierType;

    @Schema(description = "信用等级（1-优秀,2-良好,3-一般,4-较差）", example = "2")
    private Integer creditLevel;

    @Schema(description = "联系人（模糊查询）", example = "张三")
    private String contactPerson;

    @Schema(description = "联系电话（模糊查询）", example = "138")
    private String contactPhone;

    @Schema(description = "状态（0-禁用,1-启用）", example = "1")
    private Integer status;

    @Schema(description = "创建时间范围", example = "[2023-10-01 00:00:00, 2023-10-31 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}

