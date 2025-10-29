package com.laby.module.wms.dal.dataobject.inbound;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入库单明细 DO
 * 对应数据库表：wms_inbound_item
 *
 * 业务说明：
 * - 入库单明细记录每个商品的入库信息
 * - 一个入库单（InboundDO）包含多个明细行
 * - 支持批次管理和序列号管理
 * - 支持质检功能（合格数量、不合格数量）
 *
 * @author laby
 */
@TableName("wms_inbound_item")
@Data
@EqualsAndHashCode(callSuper = true)
public class InboundItemDO extends BaseDO {

    /**
     * 明细ID（主键）
     */
    @TableId
    private Long id;

    /**
     * 入库单ID
     * 关联：wms_inbound.id
     */
    private Long inboundId;

    /**
     * 商品ID
     * 关联：wms_goods.id
     */
    private Long goodsId;

    /**
     * 库位ID
     * 关联：wms_warehouse_location.id
     * 说明：收货时指定的上架库位
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
     * 生产日期
     * 说明：需要批次管理的商品可填
     */
    private LocalDate productionDate;

    /**
     * 过期日期
     * 说明：需要批次管理的商品可填
     */
    private LocalDate expireDate;

    /**
     * 计划数量
     * 说明：预期入库数量
     */
    private BigDecimal planQuantity;

    /**
     * 实收数量
     * 说明：实际收货数量
     */
    private BigDecimal receivedQuantity;

    /**
     * 合格数量
     * 说明：质检合格的数量
     */
    private BigDecimal qualifiedQuantity;

    /**
     * 不合格数量
     * 说明：质检不合格的数量
     */
    private BigDecimal unqualifiedQuantity;

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

