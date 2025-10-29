package com.laby.module.wms.controller.admin.inventory.vo;

import com.laby.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.laby.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 库存信息分页查询 Request VO
 * 用于库存列表的搜索和分页
 * 
 * 支持的查询条件：
 * - 仓库ID（精确查询）
 * - 库位ID（精确查询）
 * - 商品ID/名称（精确/模糊查询）
 * - 批次号（精确查询）
 * - 序列号（精确查询）
 * - 库存状态（精确查询）
 * - 创建时间范围（区间查询）
 * 
 * @author laby
 */
@Schema(description = "管理后台 - 库存信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InventoryPageReqVO extends PageParam {

    /**
     * 仓库ID（精确查询）
     * 必填，库存查询必须指定仓库
     */
    @Schema(description = "仓库ID", example = "1")
    private Long warehouseId;

    /**
     * 库位ID（精确查询）
     * 可选，用于查询指定库位的库存
     */
    @Schema(description = "库位ID", example = "1")
    private Long locationId;

    /**
     * 商品ID（精确查询）
     * 可选，用于查询指定商品的库存
     */
    @Schema(description = "商品ID", example = "1")
    private Long goodsId;

    /**
     * 商品名称（模糊查询）
     * 可选，用于搜索商品名称
     */
    @Schema(description = "商品名称", example = "iPhone")
    private String goodsName;

    /**
     * SKU编码（模糊查询）
     * 可选，用于搜索SKU编码
     */
    @Schema(description = "SKU编码", example = "SKU-001")
    private String skuCode;

    /**
     * 批次号（精确查询）
     * 可选，用于查询指定批次的库存
     */
    @Schema(description = "批次号", example = "BATCH-20250101-001")
    private String batchNo;

    /**
     * 序列号（精确查询）
     * 可选，用于查询指定序列号的库存
     */
    @Schema(description = "序列号", example = "SN-1234567890")
    private String serialNo;

    /**
     * 库存状态（精确查询）
     * 字典：wms_inventory_status
     * 1-正常，2-冻结，3-待检，4-损坏
     */
    @Schema(description = "库存状态", example = "1")
    private Integer status;

    /**
     * 是否低于安全库存（可选）
     * true-只查询低库存预警的商品
     * false-查询所有
     */
    @Schema(description = "是否低于安全库存", example = "false")
    private Boolean lowStock;

    /**
     * 创建时间范围（区间查询）
     */
    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

