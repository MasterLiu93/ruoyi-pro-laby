package com.laby.module.wms.controller.admin.carrier.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 承运商信息 Response VO
 * 
 * 功能说明：
 * - 用于返回承运商详情和列表
 * - 包含所有字段信息
 * - 包含字典名称字段（carrierTypeName）
 * 
 * @author laby
 */
@Data
@Schema(description = "管理后台 - 承运商信息 Response VO")
public class CarrierRespVO {

    @Schema(description = "承运商ID", example = "1")
    private Long id;

    @Schema(description = "承运商编码", example = "CARRIER-001")
    private String carrierCode;

    @Schema(description = "承运商名称", example = "顺丰速运")
    private String carrierName;

    @Schema(description = "承运商类型", example = "1")
    private Integer carrierType;

    @Schema(description = "联系人", example = "张三")
    private String contactPerson;

    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    @Schema(description = "邮箱", example = "contact@sf-express.com")
    private String contactEmail;

    @Schema(description = "服务区域", example = "全国范围")
    private String serviceArea;

    @Schema(description = "收费标准", example = "首重1kg/15元，续重1kg/5元")
    private String priceStandard;

    @Schema(description = "时效要求", example = "同城24小时，跨省48小时")
    private String timeLimit;

    @Schema(description = "服务评分", example = "4.8")
    private BigDecimal rating;

    @Schema(description = "合作开始日期", example = "2023-01-01")
    private LocalDate cooperationStartDate;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "优质承运商，服务好")
    private String remark;

    @Schema(description = "创建时间", example = "2023-01-01 12:00:00")
    private LocalDateTime createTime;
}

