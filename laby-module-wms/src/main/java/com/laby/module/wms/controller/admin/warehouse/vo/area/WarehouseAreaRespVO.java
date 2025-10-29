package com.laby.module.wms.controller.admin.warehouse.vo.area;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 库区 Response VO")
@Data
public class WarehouseAreaRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "中央仓库")
    private String warehouseName;

    @Schema(description = "库区编码", example = "AREA001")
    private String areaCode;

    @Schema(description = "库区名称", example = "A区")
    private String areaName;

    @Schema(description = "库区类型", example = "STORAGE")
    private String areaType;

    @Schema(description = "楼层", example = "1")
    private Integer floor;

    @Schema(description = "面积(平方米)", example = "1000.00")
    private BigDecimal areaSize;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "备注", example = "这是一个库区")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

