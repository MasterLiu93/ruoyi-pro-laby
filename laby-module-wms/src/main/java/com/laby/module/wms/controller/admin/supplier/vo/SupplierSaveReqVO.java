package com.laby.module.wms.controller.admin.supplier.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 供应商信息保存 Request VO
 * 用于新增和修改供应商信息
 *
 * 功能说明：
 * 1. 新增供应商时，id 为空
 * 2. 修改供应商时，id 必填
 * 3. 所有必填字段都添加了校验注解
 *
 * @author laby
 */
@Schema(description = "管理后台 - 供应商信息保存 Request VO")
@Data
public class SupplierSaveReqVO {

    @Schema(description = "供应商ID", example = "1")
    private Long id;

    @Schema(description = "供应商编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "SUP-001")
    @NotBlank(message = "供应商编码不能为空")
    private String supplierCode;

    @Schema(description = "供应商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "深圳市华强电子有限公司")
    @NotBlank(message = "供应商名称不能为空")
    private String supplierName;

    @Schema(description = "供应商类型（1-普通,2-VIP,3-战略）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "供应商类型不能为空")
    private Integer supplierType;

    @Schema(description = "联系人", example = "张三")
    private String contactPerson;

    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String contactEmail;

    @Schema(description = "省份", example = "广东省")
    private String province;

    @Schema(description = "城市", example = "深圳市")
    private String city;

    @Schema(description = "区县", example = "南山区")
    private String district;

    @Schema(description = "详细地址", example = "科技园南区深南大道9988号")
    private String address;

    @Schema(description = "信用等级（1-优秀,2-良好,3-一般,4-较差）", example = "2")
    private Integer creditLevel;

    @Schema(description = "合作开始日期", example = "2023-01-01")
    private LocalDate cooperationStartDate;

    @Schema(description = "状态（0-禁用,1-启用）", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "长期合作伙伴，质量稳定")
    private String remark;
}

