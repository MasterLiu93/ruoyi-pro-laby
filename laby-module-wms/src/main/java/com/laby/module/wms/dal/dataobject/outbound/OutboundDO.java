package com.laby.module.wms.dal.dataobject.outbound;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出库单主表 DO
 * 对应数据库表：wms_outbound
 *
 * 业务说明：
 * - 出库单是WMS系统中货物出库的核心单据
 * - 支持多种出库类型：销售出库、调拨出库、退货出库、其他出库等
 * - 出库流程：创建 → 审核 → 拣货 → 复核发货 → 完成
 * - 每个出库单包含多个明细行（OutboundItemDO）
 *
 * @author laby
 */
@TableName("wms_outbound")
@Data
@EqualsAndHashCode(callSuper = true)
public class OutboundDO extends BaseDO {

    /**
     * 出库单ID（主键）
     */
    @TableId
    private Long id;

    /**
     * 出库单号
     * 规则：OUT + yyyyMMdd + 4位流水号
     * 示例：OUT20251028001
     */
    private String outboundNo;

    /**
     * 出库类型（字典值）
     * 字典类型：wms_outbound_type
     * 1-销售出库，2-调拨出库，3-退货出库，4-其他出库
     */
    private Integer outboundType;

    /**
     * 仓库ID
     * 关联：wms_warehouse.id
     */
    private Long warehouseId;

    /**
     * 客户ID
     * 关联：wms_customer.id
     * 说明：销售出库时必填
     */
    private Long customerId;

    /**
     * 客户名称
     * 冗余字段，方便查询显示
     */
    private String customerName;

    /**
     * 出库状态（字典值）
     * 字典类型：wms_outbound_status
     * 1-待审核，2-已审核，3-拣货中，4-待发货，5-已发货，6-已取消
     */
    private Integer status;

    /**
     * 预计发货时间
     */
    private LocalDateTime expectedShipmentTime;

    /**
     * 实际发货时间
     */
    private LocalDateTime actualShipmentTime;

    /**
     * 总数量
     * 说明：所有明细行数量之和
     */
    private BigDecimal totalQuantity;

    /**
     * 已拣货数量
     * 说明：实际拣货数量，可能与预期数量不一致
     */
    private BigDecimal pickedQuantity;

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

