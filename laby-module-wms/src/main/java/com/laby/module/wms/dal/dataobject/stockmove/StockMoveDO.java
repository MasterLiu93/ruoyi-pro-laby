package com.laby.module.wms.dal.dataobject.stockmove;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 移库单 DO
 * 
 * 库存移动记录，包括库位调整、库区调整、仓库调拨等类型
 *
 * @author laby
 */
@TableName(value = "wms_stock_move", autoResultMap = true)
@KeySequence("wms_stock_move_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMoveDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 移库单号
     */
    private String moveNo;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 移库类型（字典值）
     * 字典类型：wms_stock_move_type
     * 1-库位调整，2-库区调整，3-仓库调拨
     */
    private Integer moveType;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 源库位ID
     */
    private Long fromLocationId;

    /**
     * 源库位编码
     */
    private String fromLocationCode;

    /**
     * 目标库位ID
     */
    private Long toLocationId;

    /**
     * 目标库位编码
     */
    private String toLocationCode;

    /**
     * 移库数量
     */
    private BigDecimal quantity;

    /**
     * 移库原因
     */
    private String moveReason;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    /**
     * 状态（字典值）
     * 字典类型：wms_stock_move_status
     * 1-待执行，2-执行中，3-已完成，4-已取消
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}

