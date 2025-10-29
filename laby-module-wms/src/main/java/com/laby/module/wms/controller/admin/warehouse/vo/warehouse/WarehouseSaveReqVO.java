package com.laby.module.wms.controller.admin.warehouse.vo.warehouse;

import com.laby.framework.common.enums.CommonStatusEnum;
import com.laby.framework.common.validation.InEnum;
import com.laby.module.wms.enums.WarehouseTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 仓库创建/修改 Request VO")
@Data
public class WarehouseSaveReqVO {

    @Schema(description = "主键ID", example = "1024")
    private Long id;

    @Schema(description = "仓库编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "WH001")
    @NotBlank(message = "仓库编码不能为空")
    @Size(max = 50, message = "仓库编码长度不能超过50个字符")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "仓库编码只能包含字母、数字、中划线和下划线")
    private String warehouseCode;

    @Schema(description = "仓库名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "北京总仓")
    @NotBlank(message = "仓库名称不能为空")
    @Size(max = 100, message = "仓库名称长度不能超过100个字符")
    private String warehouseName;

    @Schema(description = "仓库类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "仓库类型不能为空")
    @InEnum(value = WarehouseTypeEnum.class, message = "仓库类型必须是 {value}")
    private Integer warehouseType;

    @Schema(description = "省份", example = "北京市")
    @Size(max = 50, message = "省份长度不能超过50个字符")
    private String province;

    @Schema(description = "城市", example = "北京市")
    @Size(max = 50, message = "城市长度不能超过50个字符")
    private String city;

    @Schema(description = "区县", example = "朝阳区")
    @Size(max = 50, message = "区县长度不能超过50个字符")
    private String district;

    @Schema(description = "详细地址", example = "望京SOHO T1座")
    @Size(max = 200, message = "详细地址长度不能超过200个字符")
    private String address;

    @Schema(description = "联系人", example = "张三")
    @Size(max = 50, message = "联系人长度不能超过50个字符")
    private String contactPerson;

    @Schema(description = "联系电话", example = "13800138000")
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String contactPhone;

    @Schema(description = "总面积(平方米)", example = "50000.00")
    @DecimalMin(value = "0", message = "总面积必须大于0")
    private BigDecimal totalArea;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    @InEnum(value = CommonStatusEnum.class, message = "状态必须是 {value}")
    private Integer status;

    @Schema(description = "备注", example = "北京地区主仓库")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

}

