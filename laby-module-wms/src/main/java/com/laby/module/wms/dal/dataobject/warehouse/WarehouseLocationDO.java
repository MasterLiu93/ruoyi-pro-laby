package com.laby.module.wms.dal.dataobject.warehouse;

import com.laby.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 库位信息 DO
 * 对应数据库表：wms_warehouse_location
 * 
 * 功能说明：
 * - 存储库位的基本信息，库位是库区的最小存储单元
 * - 一个库区可以包含多个库位
 * - 库位采用三维坐标定位：排号、列号、层号
 * - 每个库位有容量和承重限制
 * - 支持多租户隔离
 * 
 * @author laby
 */
@TableName("wms_warehouse_location")
@KeySequence("wms_warehouse_location_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseLocationDO extends TenantBaseDO {

    /**
     * 库位ID（主键）
     */
    @TableId
    private Long id;
    
    /**
     * 仓库ID
     * 关联：wms_warehouse.id
     * 说明：标识该库位属于哪个仓库（冗余字段，便于查询）
     */
    private Long warehouseId;
    
    /**
     * 库区ID
     * 关联：wms_warehouse_area.id
     * 说明：标识该库位属于哪个库区
     */
    private Long areaId;
    
    /**
     * 库位编码，全局唯一
     * 示例：LOC-A01-R01-C01-L01（库区-排-列-层）
     * 命名规则：库区编码 + 排号 + 列号 + 层号
     */
    private String locationCode;
    
    /**
     * 库位类型（字典值，整数类型）
     * 字典类型：wms_warehouse_location_type
     * 取值：1-普通库位，2-临时库位，3-残次品库位，4-冷冻库位
     * 
     * 说明：
     * - 普通库位：正常商品存储
     * - 临时库位：临时存储，快速周转
     * - 残次品库位：存储有瑕疵的商品
     * - 冷冻库位：需要冷冻保存的商品
     */
    private String locationType;
    
    /**
     * 排号
     * 示例：1, 2, 3...
     * 说明：库位的排数，横向编号
     */
    private Integer rowNo;
    
    /**
     * 列号
     * 示例：1, 2, 3...
     * 说明：库位的列数，纵向编号
     */
    private Integer columnNo;
    
    /**
     * 层号
     * 示例：1, 2, 3...
     * 说明：库位的层数，垂直编号，从下往上递增
     */
    private Integer layerNo;
    
    /**
     * 容量(立方米)
     * 示例：5.00
     * 说明：库位的最大存储容量，用于计算是否可以存放商品
     */
    private BigDecimal capacity;
    
    /**
     * 最大承重(kg)
     * 示例：1000.00
     * 说明：库位的最大承重能力，用于安全校验
     */
    private BigDecimal maxWeight;
    
    /**
     * 状态（使用专用字典）
     * 字典类型：wms_location_status
     * 取值：0-禁用，1-空闲，2-占用，3-锁定
     * 
     * 说明：
     * - 禁用：库位不可用
     * - 空闲：库位可用，无商品
     * - 占用：库位已有商品
     * - 锁定：库位被锁定，不可操作
     */
    private Integer status;
    
    /**
     * 备注
     * 用于记录库位的额外说明信息
     */
    private String remark;

}

