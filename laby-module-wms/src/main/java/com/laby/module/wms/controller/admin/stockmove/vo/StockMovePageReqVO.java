package com.laby.module.wms.controller.admin.stockmove.vo;

import com.laby.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.laby.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 移库单分页查询 Request VO
 *
 * @author laby
 */
@Schema(description = "管理后台 - 移库单分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StockMovePageReqVO extends PageParam {

    @Schema(description = "移库单号", example = "MOVE202510280001")
    private String moveNo;

    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    @Schema(description = "移库类型", example = "1")
    private Integer moveType;

    @Schema(description = "商品名称", example = "iPhone 15 Pro Max")
    private String goodsName;

    @Schema(description = "SKU编码", example = "SKU001")
    private String skuCode;

    @Schema(description = "批次号", example = "BATCH20251028")
    private String batchNo;

    @Schema(description = "源库位ID", example = "1")
    private Long fromLocationId;

    @Schema(description = "目标库位ID", example = "2")
    private Long toLocationId;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

