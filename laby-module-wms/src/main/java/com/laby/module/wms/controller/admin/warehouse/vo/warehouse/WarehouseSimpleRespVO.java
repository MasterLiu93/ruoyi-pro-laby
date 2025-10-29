package com.laby.module.wms.controller.admin.warehouse.vo.warehouse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 仓库精简 Response VO")
@Data
public class WarehouseSimpleRespVO {

    @Schema(description = "主键ID", example = "1024")
    private Long id;

    @Schema(description = "仓库编码", example = "WH001")
    private String warehouseCode;

    @Schema(description = "仓库名称", example = "北京总仓")
    private String warehouseName;

}

