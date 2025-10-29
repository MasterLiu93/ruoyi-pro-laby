package com.laby.module.wms.dal.dataobject.warehouse;

import com.laby.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 库区信息 DO
 * 对应数据库表：wms_warehouse_area
 * 
 * 功能说明：
 * - 存储库区的基本信息，库区是仓库的子区域划分
 * - 一个仓库可以包含多个库区（如存储区、拣货区、暂存区等）
 * - 一个库区可以包含多个库位
 * - 支持多租户隔离
 * 
 * @author laby
 */
@TableName("wms_warehouse_area")
@KeySequence("wms_warehouse_area_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseAreaDO extends TenantBaseDO {

    /**
     * 库区ID（主键）
     */
    @TableId
    private Long id;
    
    /**
     * 仓库ID
     * 关联：wms_warehouse.id
     * 说明：标识该库区属于哪个仓库
     */
    private Long warehouseId;
    
    /**
     * 库区编码，同一仓库内唯一
     * 示例：A01, B02-STORAGE
     */
    private String areaCode;
    
    /**
     * 库区名称
     * 示例：A区存储区、B区拣货区
     */
    private String areaName;
    
    /**
     * 库区类型（字典值，整数类型）
     * 字典类型：wms_warehouse_area_type
     * 取值：1-存储区，2-拣货区，3-暂存区，4-收货区，5-发货区
     * 
     * 说明：
     * - 存储区：长期存储商品
     * - 拣货区：拣货作业专用区域
     * - 暂存区：临时存储，中转区域
     * - 收货区：收货验货专用区域
     * - 发货区：发货装车专用区域
     */
    private String areaType;
    
    /**
     * 楼层
     * 示例：1, 2, 3（表示第几层）
     * -1 表示地下一层，0 表示地面层
     */
    private Integer floor;
    
    /**
     * 面积(平方米)
     * 示例：500.00
     * 说明：库区的占地面积，用于计算仓库利用率
     */
    private BigDecimal areaSize;
    
    /**
     * 状态
     * 0-禁用，1-启用
     * 
     * 枚举 {@link com.laby.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;
    
    /**
     * 备注
     * 用于记录库区的额外说明信息
     */
    private String remark;

}

