package com.laby.module.wms.controller.admin.goods.vo.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 商品 Response VO")
@Data
public class GoodsRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "SKU编码", example = "SKU001")
    private String skuCode;

    @Schema(description = "商品名称", example = "iPhone 15 Pro")
    private String goodsName;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "电子产品")
    private String categoryName;

    @Schema(description = "品牌", example = "Apple")
    private String brand;

    @Schema(description = "型号", example = "A2848")
    private String model;

    @Schema(description = "条形码", example = "6901234567890")
    private String barcode;

    @Schema(description = "计量单位", example = "1")
    private Integer unit;

    @Schema(description = "规格", example = "256GB 黑色")
    private String spec;

    @Schema(description = "重量(KG)", example = "0.5")
    private BigDecimal weight;

    @Schema(description = "体积(立方米)", example = "0.001")
    private BigDecimal volume;

    @Schema(description = "保质期(天)", example = "365")
    private Integer shelfLife;

    @Schema(description = "最低存储温度", example = "-10")
    private BigDecimal storageTempMin;

    @Schema(description = "最高存储温度", example = "25")
    private BigDecimal storageTempMax;

    @Schema(description = "是否批次管理", example = "false")
    private Boolean needBatch;

    @Schema(description = "是否序列号管理", example = "true")
    private Boolean needSerial;

    @Schema(description = "安全库存", example = "100")
    private BigDecimal safetyStock;

    @Schema(description = "最大库存", example = "10000")
    private BigDecimal maxStock;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "高端手机")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

