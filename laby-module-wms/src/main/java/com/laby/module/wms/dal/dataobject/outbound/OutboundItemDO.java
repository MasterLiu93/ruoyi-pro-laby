package com.laby.module.wms.dal.dataobject.outbound;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 出库单明细 DO
 * 对应数据库表：wms_outbound_item
 *
 * 业务说明：
 * - 出库单明细记录每个商品的出库信息
 * - 一个出库单（OutboundDO）包含多个明细行
 * - 支持批次管理和序列号管理
 * - 支持多库位拣货
 *
 * @author laby
 */
@TableName("wms_outbound_item")
@Data
@EqualsAndHashCode(callSuper = true)
public class OutboundItemDO extends BaseDO {

    /**
     * 明细ID（主键）
     */
    @TableId
    private Long id;

    /**
     * 出库单ID
     * 关联：wms_outbound.id
     */
    private Long outboundId;

    /**
     * 商品ID
     * 关联：wms_goods.id
     */
    private Long goodsId;

    /**
     * 库位ID
     * 关联：wms_warehouse_location.id
     * 说明：拣货时指定的拣货库位
     */
    private Long locationId;

    /**
     * 批次号
     * 说明：需要批次管理的商品必填
     */
    private String batchNo;

    /**
     * 序列号
     * 说明：需要序列号管理的商品必填
     */
    private String serialNo;

    /**
     * 计划数量
     * 说明：预期出库数量
     */
    private BigDecimal planQuantity;

    /**
     * 已拣货数量
     * 说明：实际拣货数量
     */
    private BigDecimal pickedQuantity;

    /**
     * 已发货数量
     * 说明：实际发货数量（复核后）
     */
    private BigDecimal shippedQuantity;

    /**
     * 单价
     * 说明：商品单价（可选）
     */
    private BigDecimal price;

    /**
     * 金额
     * 说明：数量 × 单价（可选）
     */
    private BigDecimal amount;

    /**
     * 备注
     */
    private String remark;

}

