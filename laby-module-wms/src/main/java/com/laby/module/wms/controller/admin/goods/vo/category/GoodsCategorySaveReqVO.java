package com.laby.module.wms.controller.admin.goods.vo.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 商品分类新增/修改 Request VO")
@Data
public class GoodsCategorySaveReqVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "分类编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "CAT001")
    @NotEmpty(message = "分类编码不能为空")
    private String categoryCode;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "电子产品")
    @NotEmpty(message = "分类名称不能为空")
    private String categoryName;

    @Schema(description = "父分类ID", example = "0")
    private Long parentId;

    @Schema(description = "层级", example = "1")
    private Integer level;

    @Schema(description = "排序", example = "1")
    private Integer sort;

}

