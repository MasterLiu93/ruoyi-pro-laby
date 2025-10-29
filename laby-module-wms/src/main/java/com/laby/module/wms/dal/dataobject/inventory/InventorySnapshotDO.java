package com.laby.module.wms.dal.dataobject.inventory;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 库存快照 DO
 * 
 * 功能说明：
 * - 每日定时生成库存快照
 * - 用于库存趋势分析、报表统计
 * - 支持历史库存数据查询
 * - 为库存预警提供数据支持
 * 
 * 对应数据库表：wms_inventory_snapshot
 * 
 * 业务场景：
 * 1. 每日凌晨自动生成前一天的库存快照
 * 2. 库存趋势分析：查看某段时间内库存变化曲线
 * 3. 库存周转率计算：基于快照数据统计
 * 4. 历史库存查询：查询某个时间点的库存状态
 * 
 * 注意事项：
 * - snapshot_date 为 VARCHAR(10) 类型，格式：YYYY-MM-DD
 * - 每个商品每个仓库每天只有一条快照记录
 * - 快照数据为只读，不支持修改
 *
 * @author laby
 */
@TableName("wms_inventory_snapshot")
@Data
@EqualsAndHashCode(callSuper = true)
public class InventorySnapshotDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 快照日期
     * 格式：YYYY-MM-DD（如：2025-01-28）
     * 类型：VARCHAR(10)
     * 说明：使用字符串存储日期，方便查询和索引
     */
    private String snapshotDate;

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
     * 库存数量
     * 说明：快照时刻的总库存数量
     */
    private BigDecimal quantity;

    /**
     * 锁定数量
     * 说明：快照时刻的锁定库存数量
     */
    private BigDecimal lockQuantity;

}

