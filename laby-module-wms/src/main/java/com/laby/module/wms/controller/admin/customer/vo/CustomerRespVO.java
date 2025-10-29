package com.laby.module.wms.controller.admin.customer.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户信息 Response VO
 * 用于返回客户详情和列表
 *
 * 功能说明：
 * 1. 包含客户的所有信息字段
 * 2. 包含累计订单数和累计金额等统计字段
 * 3. 包含创建时间和更新时间等基础字段
 *
 * @author laby
 */
@Schema(description = "管理后台 - 客户信息 Response VO")
@Data
public class CustomerRespVO {

    @Schema(description = "客户ID", example = "1")
    private Long id;

    @Schema(description = "客户编码", example = "CUST-001")
    private String customerCode;

    @Schema(description = "客户名称", example = "深圳市XX商贸有限公司")
    private String customerName;

    @Schema(description = "客户类型（1-零售,2-批发,3-企业）", example = "1")
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

    @Schema(description = "累计订单数", example = "50")
    private Integer totalOrders;

    @Schema(description = "累计金额", example = "500000.00")
    private BigDecimal totalAmount;

    @Schema(description = "状态（0-禁用,1-启用）", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "优质客户，长期合作")
    private String remark;

    @Schema(description = "创建时间", example = "2023-10-01 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2023-10-01 10:00:00")
    private LocalDateTime updateTime;
}

