package com.laby.module.wms.controller.admin.supplier.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应商信息 Response VO
 * 用于返回供应商详情和列表
 *
 * 功能说明：
 * 1. 包含供应商的所有信息字段
 * 2. 包含创建时间和更新时间等基础字段
 *
 * @author laby
 */
@Schema(description = "管理后台 - 供应商信息 Response VO")
@Data
public class SupplierRespVO {

    @Schema(description = "供应商ID", example = "1")
    private Long id;

    @Schema(description = "供应商编码", example = "SUP-001")
    private String supplierCode;

    @Schema(description = "供应商名称", example = "深圳市华强电子有限公司")
    private String supplierName;

    @Schema(description = "供应商类型（1-普通,2-VIP,3-战略）", example = "1")
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

    @Schema(description = "创建时间", example = "2023-10-01 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2023-10-01 10:00:00")
    private LocalDateTime updateTime;
}

