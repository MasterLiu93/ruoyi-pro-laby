package com.laby.module.wms.controller.admin.warehouse.vo.area;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 库区精简信息 Response VO")
@Data
public class WarehouseAreaSimpleRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "库区编码", example = "AREA001")
    private String areaCode;

    @Schema(description = "库区名称", example = "A区")
    private String areaName;

    @Schema(description = "库区类型", example = "STORAGE")
    private String areaType;

}

