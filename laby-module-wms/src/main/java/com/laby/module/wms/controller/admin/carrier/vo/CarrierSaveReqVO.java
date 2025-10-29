package com.laby.module.wms.controller.admin.carrier.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 承运商信息保存 Request VO
 * 
 * 功能说明：
 * - 用于新增和修改承运商信息
 * - id 为空时表示新增，不为空时表示修改
 * 
 * @author laby
 */
@Data
@Schema(description = "管理后台 - 承运商信息保存 Request VO")
public class CarrierSaveReqVO {

    /**
     * 承运商ID（修改时必传）
     */
    @Schema(description = "承运商ID", example = "1")
    private Long id;

    /**
     * 承运商编码（必填，唯一）
     */
    @Schema(description = "承运商编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "CARRIER-001")
    @NotBlank(message = "承运商编码不能为空")
    private String carrierCode;

    /**
     * 承运商名称（必填）
     */
    @Schema(description = "承运商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "顺丰速运")
    @NotBlank(message = "承运商名称不能为空")
    private String carrierName;

    /**
     * 承运商类型（必填，字典：wms_carrier_type）
     * 1-快递，2-物流，3-专线
     */
    @Schema(description = "承运商类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "承运商类型不能为空")
    private Integer carrierType;

    /**
     * 联系人
     */
    @Schema(description = "联系人", example = "张三")
    private String contactPerson;

    /**
     * 联系电话（必填）
     */
    @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "contact@sf-express.com")
    private String contactEmail;

    /**
     * 服务区域
     */
    @Schema(description = "服务区域", example = "全国范围")
    private String serviceArea;

    /**
     * 收费标准
     */
    @Schema(description = "收费标准", example = "首重1kg/15元，续重1kg/5元")
    private String priceStandard;

    /**
     * 时效要求
     */
    @Schema(description = "时效要求", example = "同城24小时，跨省48小时")
    private String timeLimit;

    /**
     * 服务评分（1-5分）
     */
    @Schema(description = "服务评分", example = "4.8")
    private BigDecimal rating;

    /**
     * 合作开始日期
     */
    @Schema(description = "合作开始日期", example = "2023-01-01")
    private LocalDate cooperationStartDate;

    /**
     * 状态（必填）
     * 1-启用，0-禁用
     */
    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "优质承运商，服务好")
    private String remark;
}

