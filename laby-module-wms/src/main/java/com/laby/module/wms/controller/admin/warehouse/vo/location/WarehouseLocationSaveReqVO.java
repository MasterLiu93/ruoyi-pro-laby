package com.laby.module.wms.controller.admin.warehouse.vo.location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 库位新增/修改 Request VO")
@Data
public class WarehouseLocationSaveReqVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;

    @Schema(description = "库区ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "库区ID不能为空")
    private Long areaId;

    @Schema(description = "库位编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "LOC-A01-R01-C01-L01")
    @NotBlank(message = "库位编码不能为空")
    @Size(max = 50, message = "库位编码长度不能超过50个字符")
    private String locationCode;

    @Schema(description = "库位类型: NORMAL-普通, TEMP-临时, DEFECT-残次品, FROZEN-冷冻", requiredMode = Schema.RequiredMode.REQUIRED, example = "NORMAL")
    @NotBlank(message = "库位类型不能为空")
    private String locationType;

    @Schema(description = "排号", example = "1")
    private Integer rowNo;

    @Schema(description = "列号", example = "1")
    private Integer columnNo;

    @Schema(description = "层号", example = "1")
    private Integer layerNo;

    @Schema(description = "容量(立方米)", example = "10.00")
    @DecimalMin(value = "0", message = "容量必须大于等于0")
    private BigDecimal capacity;

    @Schema(description = "最大承重(kg)", example = "1000.00")
    @DecimalMin(value = "0", message = "最大承重必须大于等于0")
    private BigDecimal maxWeight;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "备注", example = "这是一个库位")
    private String remark;

}

