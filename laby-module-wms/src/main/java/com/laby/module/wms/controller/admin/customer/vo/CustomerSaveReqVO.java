package com.laby.module.wms.controller.admin.customer.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 客户信息保存 Request VO
 * 用于新增和修改客户信息
 *
 * 功能说明：
 * 1. 新增客户时，id 为空
 * 2. 修改客户时，id 必填
 * 3. 所有必填字段都添加了校验注解
 *
 * @author laby
 */
@Schema(description = "管理后台 - 客户信息保存 Request VO")
@Data
public class CustomerSaveReqVO {

    @Schema(description = "客户ID", example = "1")
    private Long id;

    @Schema(description = "客户编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "CUST-001")
    @NotBlank(message = "客户编码不能为空")
    private String customerCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "深圳市XX商贸有限公司")
    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @Schema(description = "客户类型（1-零售,2-批发,3-企业）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "客户类型不能为空")
    private Integer customerType;

    @Schema(description = "客户等级（1-VIP,2-金牌,3-银牌,4-普通）", example = "4")
    private Integer customerLevel;

    @Schema(description = "联系人", example = "李四")
    private String contactPerson;

    @Schema(description = "联系电话", example = "13900139000")
    private String contactPhone;

    @Schema(description = "邮箱", example = "lisi@example.com")
    private String contactEmail;

    @Schema(description = "收货省份", example = "广东省")
    private String deliveryProvince;

    @Schema(description = "收货城市", example = "深圳市")
    private String deliveryCity;

    @Schema(description = "收货区县", example = "福田区")
    private String deliveryDistrict;

    @Schema(description = "收货地址", example = "华强北路1001号")
    private String deliveryAddress;

    @Schema(description = "信用额度", example = "100000.00")
    private BigDecimal creditLimit;

    @Schema(description = "状态（0-禁用,1-启用）", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "优质客户，长期合作")
    private String remark;
}

