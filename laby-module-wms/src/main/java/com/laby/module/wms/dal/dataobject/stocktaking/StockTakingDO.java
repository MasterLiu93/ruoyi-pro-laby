package com.laby.module.wms.dal.dataobject.stocktaking;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 盘点单 DO
 * 
 * 盘点单记录每个商品的盘点情况，包括账面数量、实盘数量、差异等
 *
 * @author laby
 */
@TableName(value = "wms_stock_taking", autoResultMap = true)
@KeySequence("wms_stock_taking_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTakingDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 盘点单号
     */
    private String takingNo;

    /**
     * 盘点计划ID
     */
    private Long planId;

    /**
     * 盘点计划编号
     */
    private String planNo;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 库位ID
     */
    private Long locationId;

    /**
     * 库位编码
     */
    private String locationCode;

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
     * 账面数量
     */
    private BigDecimal bookQuantity;

    /**
     * 实盘数量
     */
    private BigDecimal actualQuantity;

    /**
     * 差异原因
     */
    private String diffReason;

    /**
     * 盘点人
     */
    private String operator;

    /**
     * 盘点时间
     */
    private LocalDateTime operateTime;

    /**
     * 复核人
     */
    private String reviewer;

    /**
     * 复核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 状态（字典值）
     * 字典类型：wms_stock_taking_status
     * 1-待盘点，2-已盘点，3-已复核，4-已调整
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}

