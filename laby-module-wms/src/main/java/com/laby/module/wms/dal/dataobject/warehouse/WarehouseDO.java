package com.laby.module.wms.dal.dataobject.warehouse;

import com.laby.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 仓库信息 DO
 * 对应数据库表：wms_warehouse
 * 
 * 功能说明：
 * - 存储仓库的基本信息，包括编码、名称、类型、地址等
 * - 支持多租户隔离
 * - 一个仓库可以包含多个库区
 * 
 * @author laby
 */
@TableName("wms_warehouse")
@KeySequence("wms_warehouse_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDO extends TenantBaseDO {

    /**
     * 仓库ID（主键）
     */
    @TableId
    private Long id;
    
    /**
     * 仓库编码，唯一标识
     * 示例：WH-001, WH-HZ-001
     */
    private String warehouseCode;
    
    /**
     * 仓库名称
     * 示例：上海中心仓库、杭州原材料仓库
     */
    private String warehouseName;
    
    /**
     * 仓库类型（字典值，整数类型）
     * 字典类型：wms_warehouse_type
     * 取值：1-中心仓库，2-区域仓库，3-前置仓，4-虚拟仓
     * 
     * 枚举 {@link com.laby.module.wms.enums.WarehouseTypeEnum}
     */
    private Integer warehouseType;
    
    /**
     * 省份
     * 示例：浙江省
     */
    private String province;
    
    /**
     * 城市
     * 示例：杭州市
     */
    private String city;
    
    /**
     * 区县
     * 示例：余杭区
     */
    private String district;
    
    /**
     * 详细地址
     * 示例：文一西路XXX号
     */
    private String address;
    
    /**
     * 联系人
     * 示例：张三
     */
    private String contactPerson;
    
    /**
     * 联系电话
     * 示例：13800138000
     */
    private String contactPhone;
    
    /**
     * 总面积(平方米)
     * 用于记录仓库的总占地面积
     * 示例：10000.00
     */
    private BigDecimal totalArea;
    
    /**
     * 状态
     * 0-禁用，1-启用
     * 
     * 枚举 {@link com.laby.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;
    
    /**
     * 备注
     * 用于记录仓库的额外说明信息
     */
    private String remark;

}

