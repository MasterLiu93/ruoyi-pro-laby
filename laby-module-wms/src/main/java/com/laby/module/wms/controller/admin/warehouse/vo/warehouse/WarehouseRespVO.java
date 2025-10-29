package com.laby.module.wms.controller.admin.warehouse.vo.warehouse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 仓库 Response VO")
@Data
public class WarehouseRespVO {

    @Schema(description = "主键ID", example = "1024")
    private Long id;

    @Schema(description = "仓库编码", example = "WH001")
    private String warehouseCode;

    @Schema(description = "仓库名称", example = "北京总仓")
    private String warehouseName;

    @Schema(description = "仓库类型", example = "1")
    private Integer warehouseType;

    @Schema(description = "省份", example = "北京市")
    private String province;

    @Schema(description = "城市", example = "北京市")
    private String city;

    @Schema(description = "区县", example = "朝阳区")
    private String district;

    @Schema(description = "详细地址", example = "望京SOHO T1座")
    private String address;

    @Schema(description = "联系人", example = "张三")
    private String contactPerson;

    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    @Schema(description = "总面积(平方米)", example = "50000.00")
    private BigDecimal totalArea;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "北京地区主仓库")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

