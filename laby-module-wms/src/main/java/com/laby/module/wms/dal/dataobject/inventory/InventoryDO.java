package com.laby.module.wms.dal.dataobject.inventory;

import com.laby.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存信息 DO
 * 对应数据库表：wms_inventory
 * 
 * 功能说明：
 * - 记录商品在各仓库/库位的库存数量
 * - 支持批次号、序列号管理
 * - 支持库存锁定（预留库存）
 * - 可用数量 = 库存数量 - 锁定数量（数据库虚拟列）
 * - 支持库存状态管理（正常、冻结、待检、损坏）
 * - 使用乐观锁防止并发问题
 * 
 * @author laby
 */
@TableName("wms_inventory")
@KeySequence("wms_inventory_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDO extends TenantBaseDO {

    /**
     * 库存ID（主键）
     */
    @TableId
    private Long id;
    
    /**
     * 仓库ID
     * 关联：wms_warehouse.id
     * 说明：库存所在的仓库
     */
    private Long warehouseId;
    
    /**
     * 库位ID
     * 关联：wms_warehouse_location.id
     * 说明：库存所在的具体库位，可为空（表示仓库级库存）
     */
    private Long locationId;
    
    /**
     * 商品ID
     * 关联：wms_goods.id
     * 说明：库存对应的商品
     */
    private Long goodsId;
    
    /**
     * 批次号
     * 示例：BATCH-20250101-001
     * 说明：
     * - 食品、药品等需批次追踪的商品使用
     * - 同一商品的不同批次分别记录库存
     * - 可为空（不启用批次管理时）
     */
    private String batchNo;
    
    /**
     * 序列号
     * 示例：SN-1234567890
     * 说明：
     * - 手机、电脑等贵重物品使用
     * - 每个序列号对应一个库存记录
     * - 可为空（不启用序列号管理时）
     */
    private String serialNo;
    
    /**
     * 库存数量
     * 说明：
     * - 当前实际库存总量
     * - 包含已锁定数量
     * - 不能为负数
     */
    private BigDecimal quantity;
    
    /**
     * 锁定数量
     * 说明：
     * - 已被订单/出库单锁定，不可销售的数量
     * - 待出库确认后扣减库存
     * - 不能为负数，不能大于库存数量
     */
    private BigDecimal lockQuantity;
    
    /**
     * 可用数量（虚拟列，不需要手动赋值）
     * 计算公式：quantity - lockQuantity
     * 说明：
     * - 由数据库自动计算
     * - 查询时自动返回
     * - 表示可销售/可分配的数量
     * 
     * 注意：此字段为数据库 GENERATED ALWAYS AS 虚拟列，不需要在代码中赋值
     */
    // private BigDecimal availableQuantity;  // 虚拟列，查询时自动计算
    
    /**
     * 生产日期
     * 说明：
     * - 有保质期的商品必填
     * - 用于计算过期日期
     * - 可为空
     */
    private LocalDate productionDate;
    
    /**
     * 过期日期
     * 说明：
     * - 有保质期的商品必填
     * - 用于库存预警和先进先出
     * - 可为空
     */
    private LocalDate expireDate;
    
    /**
     * 入库日期
     * 说明：
     * - 记录商品首次入库时间
     * - 用于先进先出拣货策略
     */
    private LocalDateTime inboundDate;
    
    /**
     * 供应商ID
     * 关联：wms_supplier.id
     * 说明：
     * - 记录库存来源供应商
     * - 用于质量追溯
     * - 可为空
     */
    private Long supplierId;
    
    /**
     * 供应商名称
     * 冗余字段，方便查询显示
     * 说明：
     * - 从供应商表复制过来
     * - 避免每次都关联查询
     */
    private String supplierName;
    
    /**
     * 乐观锁版本号
     * 说明：
     * - 用于防止库存并发修改
     * - 每次更新自动+1
     * - MyBatis Plus @Version 注解使用
     */
    private Integer version;
    
    /**
     * 库存状态（字典值，必须使用 Integer）
     * 字典类型：wms_inventory_status
     * 1-正常：可正常使用的库存
     * 2-冻结：被冻结，不可使用（如质量问题）
     * 3-待检：新入库，等待质检
     * 4-损坏：损坏报废，待处理
     * 
     * 枚举 {@link com.laby.module.wms.enums.InventoryStatusEnum}
     */
    private Integer status;

}

