package com.laby.module.wms.dal.dataobject.goods;

import com.laby.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.math.BigDecimal;

/**
 * 商品信息 DO
 * 对应数据库表：wms_goods
 * 
 * 功能说明：
 * - 存储商品的详细信息，包括基本信息、规格参数、存储要求等
 * - 支持批次管理和序列号管理（用于食品、药品、贵重物品等）
 * - 支持安全库存和最大库存设置，用于库存告警
 * - 支持多租户隔离
 * 
 * @author laby
 */
@TableName("wms_goods")
@KeySequence("wms_goods_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDO extends TenantBaseDO {

    /**
     * 商品ID（主键）
     */
    @TableId
    private Long id;
    
    /**
     * SKU编码，唯一标识
     * 示例：SKU-001, SKU-PHONE-001
     * 说明：Stock Keeping Unit，库存量单位，商品的唯一标识
     */
    private String skuCode;
    
    /**
     * 商品名称
     * 示例：iPhone 15 Pro Max 512GB 深空黑色
     */
    private String goodsName;
    
    /**
     * 商品分类ID
     * 关联：wms_goods_category.id
     * 说明：标识该商品属于哪个分类
     */
    private Long categoryId;
    
    /**
     * 品牌
     * 示例：Apple、华为、小米
     */
    private String brand;
    
    /**
     * 型号
     * 示例：A2849、Mate 60 Pro
     */
    private String model;
    
    /**
     * 条形码
     * 示例：6901234567890
     * 说明：商品的标准条形码，用于扫码识别
     */
    private String barcode;
    
    /**
     * 计量单位（字典值，整数类型）
     * 字典类型：wms_goods_unit
     * 取值：1-个，2-箱，3-千克，4-吨，5-升，6-米，7-平方米，8-立方米
     * 
     * 说明：商品的计量单位，用于库存管理和出入库记录
     */
    private Integer unit;
    
    /**
     * 规格描述
     * 示例：512GB 深空黑色、1L*12瓶/箱、200g*20袋/箱
     * 说明：商品的规格说明，便于识别和管理
     */
    private String spec;
    
    /**
     * 重量(KG)
     * 示例：0.221（iPhone）, 15.5（箱装饮料）
     * 说明：单个商品的重量，用于计算运费和库位承重
     */
    private BigDecimal weight;
    
    /**
     * 体积(立方米)
     * 示例：0.00015（手机）, 0.025（箱装商品）
     * 说明：单个商品的体积，用于计算库位容量
     */
    private BigDecimal volume;
    
    /**
     * 保质期(天)
     * 示例：365（食品）, 730（药品）, 0（电子产品，无保质期）
     * 说明：
     * - 0 表示无保质期
     * - >0 表示保质期天数
     * - 用于库存预警和过期检查
     */
    private Integer shelfLife;
    
    /**
     * 最低存储温度(℃)
     * 示例：-18（冷冻食品）, 2（冷藏食品）, 15（常温商品）
     * 说明：商品要求的最低存储温度，可为负数
     */
    private BigDecimal storageTempMin;
    
    /**
     * 最高存储温度(℃)
     * 示例：-12（冷冻食品）, 8（冷藏食品）, 25（常温商品）
     * 说明：商品要求的最高存储温度
     */
    private BigDecimal storageTempMax;
    
    /**
     * 是否启用批次管理
     * true-启用，false-不启用
     * 
     * 说明：
     * - 启用后，入库时需指定批次号
     * - 适用于食品、药品等需要批次追溯的商品
     * - 出库时遵循先进先出（FIFO）原则
     */
    private Boolean needBatch;
    
    /**
     * 是否启用序列号管理
     * true-启用，false-不启用
     * 
     * 说明：
     * - 启用后，入库时需记录每个商品的序列号
     * - 适用于手机、电脑、珠宝等贵重物品
     * - 便于售后追溯和防伪验证
     */
    private Boolean needSerial;
    
    /**
     * 安全库存
     * 示例：50（手机）, 500（快销品）
     * 说明：
     * - 当库存低于此值时，系统会发出库存告警
     * - 用于提醒及时补货
     */
    private BigDecimal safetyStock;
    
    /**
     * 最大库存
     * 示例：1000（手机）, 10000（快销品）
     * 说明：
     * - 当库存高于此值时，系统会发出库存告警
     * - 用于防止库存积压
     */
    private BigDecimal maxStock;
    
    /**
     * 状态
     * 0-禁用，1-启用
     * 
     * 说明：
     * - 禁用后，商品不可入库、出库
     * - 已有库存不受影响
     * 
     * 枚举 {@link com.laby.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;
    
    /**
     * 备注
     * 用于记录商品的额外说明信息
     */
    private String remark;

}

