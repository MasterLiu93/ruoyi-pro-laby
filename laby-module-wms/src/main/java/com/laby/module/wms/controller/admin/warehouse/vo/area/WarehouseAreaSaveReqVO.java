package com.laby.module.wms.controller.admin.warehouse.vo.area;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 库区新增/修改 Request VO")
@Data
public class WarehouseAreaSaveReqVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;

    @Schema(description = "库区编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "AREA001")
    @NotBlank(message = "库区编码不能为空")
    @Size(max = 50, message = "库区编码长度不能超过50个字符")
    private String areaCode;

    @Schema(description = "库区名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "A区")
    @NotBlank(message = "库区名称不能为空")
    @Size(max = 100, message = "库区名称长度不能超过100个字符")
    private String areaName;

    @Schema(description = "库区类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "STORAGE")
    @NotBlank(message = "库区类型不能为空")
    private String areaType;

    @Schema(description = "楼层", example = "1")
    private Integer floor;

    @Schema(description = "面积(平方米)", example = "1000.00")
    @DecimalMin(value = "0", message = "面积必须大于等于0")
    private BigDecimal areaSize;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "备注", example = "这是一个库区")
    private String remark;

}

