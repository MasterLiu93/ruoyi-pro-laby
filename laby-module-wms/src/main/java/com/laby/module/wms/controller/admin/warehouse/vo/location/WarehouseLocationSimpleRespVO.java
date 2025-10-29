package com.laby.module.wms.controller.admin.warehouse.vo.location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 库位精简信息 Response VO")
@Data
public class WarehouseLocationSimpleRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "库区ID", example = "1")
    private Long areaId;

    @Schema(description = "库位编码", example = "LOC-A01-R01-C01-L01")
    private String locationCode;

    @Schema(description = "库位类型", example = "NORMAL")
    private String locationType;

}

