package com.laby.module.wms.dal.dataobject.inbound;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 入库单主表 DO
 * 对应数据库表：wms_inbound
 *
 * 业务说明：
 * - 入库单是WMS系统中货物入库的核心单据
 * - 支持多种入库类型：采购入库、退货入库、调拨入库、其他入库等
 * - 入库流程：创建 → 审核 → 收货上架 → 完成
 * - 每个入库单包含多个明细行（InboundItemDO）
 *
 * @author laby
 */
@TableName("wms_inbound")
@Data
@EqualsAndHashCode(callSuper = true)
public class InboundDO extends BaseDO {

    /**
     * 入库单ID（主键）
     */
    @TableId
    private Long id;

    /**
     * 入库单号
     * 规则：IN + yyyyMMdd + 4位流水号
     * 示例：IN20251028001
     */
    private String inboundNo;

    /**
     * 入库类型（字典值）
     * 字典类型：wms_inbound_type
     * 1-采购入库，2-退货入库，3-调拨入库，4-其他入库
     */
    private Integer inboundType;

    /**
     * 仓库ID
     * 关联：wms_warehouse.id
     */
    private Long warehouseId;

    /**
     * 供应商ID
     * 关联：wms_supplier.id
     * 说明：采购入库时必填
     */
    private Long supplierId;

    /**
     * 供应商名称
     * 冗余字段，方便查询显示
     */
    private String supplierName;

    /**
     * 入库状态（字典值）
     * 字典类型：wms_inbound_status
     * 1-待审核，2-已审核，3-收货中，4-已完成，5-已取消
     */
    private Integer status;

    /**
     * 预计到货时间
     */
    private LocalDateTime expectedArrivalTime;

    /**
     * 实际到货时间
     */
    private LocalDateTime actualArrivalTime;

    /**
     * 总数量
     * 说明：所有明细行数量之和
     */
    private BigDecimal totalQuantity;

    /**
     * 已收货数量
     * 说明：实际收货数量，可能与预期数量不一致
     */
    private BigDecimal receivedQuantity;

    /**
     * 总金额
     * 说明：所有明细行金额之和（可选字段）
     */
    private BigDecimal totalAmount;

    /**
     * 审核人ID
     */
    private Long auditBy;

    /**
     * 审核人名称
     */
    private String auditByName;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 完成人ID
     */
    private Long completeBy;

    /**
     * 完成人名称
     */
    private String completeByName;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 备注
     */
    private String remark;

}

