package com.laby.module.wms.controller.admin.picking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 执行拣货操作 Request VO
 * 
 * 用于拣货员执行拣货时提交拣货结果
 *
 * @author laby
 */
@Schema(description = "管理后台 - 执行拣货操作 Request VO")
@Data
public class PickingTaskPickReqVO {

    @Schema(description = "拣货任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "拣货任务ID不能为空")
    private Long id;

    @Schema(description = "实际拣货数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    @NotNull(message = "实际拣货数量不能为空")
    @Positive(message = "实际拣货数量必须大于0")
    private BigDecimal actualQuantity;

    @Schema(description = "异常类型（发生异常时填写：1-库位空, 2-库存不足, 3-商品损坏, 4-商品过期, 5-拣错商品）", example = "2")
    private Integer exceptionType;

    @Schema(description = "异常说明（发生异常时填写）", example = "库位库存不足，只拣到50件")
    private String exceptionRemark;

    @Schema(description = "备注", example = "商品外包装有轻微破损")
    private String remark;
}

