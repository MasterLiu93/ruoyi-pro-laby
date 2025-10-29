package com.laby.module.wms.controller.admin.warehouse.vo.area;

import com.laby.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.laby.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 库区分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WarehouseAreaPageReqVO extends PageParam {

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "库区编码", example = "AREA001")
    private String areaCode;

    @Schema(description = "库区名称", example = "A区")
    private String areaName;

    @Schema(description = "库区类型", example = "STORAGE")
    private String areaType;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

