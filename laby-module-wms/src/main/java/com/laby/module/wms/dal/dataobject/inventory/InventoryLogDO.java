package com.laby.module.wms.dal.dataobject.inventory;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存流水 DO
 * 
 * 功能说明：
 * - 记录所有库存变动的详细日志
 * - 支持入库、出库、移库、锁定、解锁等操作类型
 * - 记录操作前后的数量变化
 * - 关联业务单据，便于追溯
 * 
 * 对应数据库表：wms_inventory_log
 * 
 * 业务场景：
 * 1. 入库（INBOUND）：采购入库、退货入库等
 * 2. 出库（OUTBOUND）：销售出库、报损出库等
 * 3. 移库（MOVE）：库位调整、仓库调拨等
 * 4. 锁定（LOCK）：订单占用库存
 * 5. 解锁（UNLOCK）：订单取消释放库存
 *
 * @author laby
 */
@TableName("wms_inventory_log")
@Data
@EqualsAndHashCode(callSuper = true)
public class InventoryLogDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 仓库ID
     * 关联：wms_warehouse.id
     */
    private Long warehouseId;

    /**
     * 商品ID
     * 关联：wms_goods.id
     */
    private Long goodsId;

    /**
     * 库位ID（可为空）
     * 关联：wms_warehouse_location.id
     * 说明：移库操作会涉及源库位和目标库位
     */
    private Long locationId;

    /**
     * 批次号（可为空）
     * 说明：批次管理的商品必填
     */
    private String batchNo;

    /**
     * 操作类型
     * 可选值：
     * - INBOUND：入库
     * - OUTBOUND：出库
     * - MOVE：移库
     * - LOCK：锁定
     * - UNLOCK：解锁
     * - ADJUST：调整
     */
    private String operationType;

    /**
     * 操作前数量
     * 说明：操作执行前的库存数量
     */
    private BigDecimal quantityBefore;

    /**
     * 变化数量
     * 说明：
     * - 正数表示增加（入库、解锁）
     * - 负数表示减少（出库、锁定）
     */
    private BigDecimal quantityChange;

    /**
     * 操作后数量
     * 说明：操作执行后的库存数量
     * 计算公式：quantityAfter = quantityBefore + quantityChange
     */
    private BigDecimal quantityAfter;

    /**
     * 业务类型（可为空）
     * 示例：
     * - PURCHASE：采购入库
     * - SALES：销售出库
     * - RETURN：退货
     * - DAMAGE：报损
     * - ORDER：订单锁定
     */
    private String businessType;

    /**
     * 业务单号（可为空）
     * 说明：关联的业务单据编号
     * 示例：PO-20250128001（采购单）、SO-20250128001（销售单）
     */
    private String businessNo;

    /**
     * 操作人
     * 说明：执行操作的用户
     */
    private String operator;

    /**
     * 备注
     * 说明：操作说明或特殊情况记录
     */
    private String remark;

}

