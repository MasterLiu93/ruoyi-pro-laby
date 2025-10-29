package com.laby.module.wms.controller.admin.warehouse.vo.location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 库位 Response VO")
@Data
public class WarehouseLocationRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "中央仓库")
    private String warehouseName;

    @Schema(description = "库区ID", example = "1")
    private Long areaId;

    @Schema(description = "库区名称", example = "A区")
    private String areaName;

    @Schema(description = "库位编码", example = "LOC-A01-R01-C01-L01")
    private String locationCode;

    @Schema(description = "库位类型: NORMAL-普通, TEMP-临时, DEFECT-残次品, FROZEN-冷冻", example = "NORMAL")
    private String locationType;

    @Schema(description = "排号", example = "1")
    private Integer rowNo;

    @Schema(description = "列号", example = "1")
    private Integer columnNo;

    @Schema(description = "层号", example = "1")
    private Integer layerNo;

    @Schema(description = "容量(立方米)", example = "10.00")
    private BigDecimal capacity;

    @Schema(description = "最大承重(kg)", example = "1000.00")
    private BigDecimal maxWeight;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "备注", example = "这是一个库位")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

