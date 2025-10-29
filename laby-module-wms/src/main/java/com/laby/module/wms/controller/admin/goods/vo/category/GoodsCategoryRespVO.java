package com.laby.module.wms.controller.admin.goods.vo.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 商品分类 Response VO")
@Data
public class GoodsCategoryRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "分类编码", example = "CAT001")
    private String categoryCode;

    @Schema(description = "分类名称", example = "电子产品")
    private String categoryName;

    @Schema(description = "父分类ID", example = "0")
    private Long parentId;

    @Schema(description = "层级", example = "1")
    private Integer level;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

