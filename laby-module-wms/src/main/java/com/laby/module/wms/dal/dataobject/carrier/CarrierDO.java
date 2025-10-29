package com.laby.module.wms.dal.dataobject.carrier;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 承运商信息 DO
 * 
 * 功能说明：
 * - 对应数据库表 wms_carrier
 * - 管理承运商的基本信息、服务区域、收费标准、评分等
 * - 支持快递、物流、专线等多种承运商类型
 * 
 * @author laby
 */
@TableName("wms_carrier")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarrierDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 承运商编码（唯一标识）
     * 示例：CARRIER-001
     */
    private String carrierCode;

    /**
     * 承运商名称
     * 示例：顺丰速运
     */
    private String carrierName;

    /**
     * 承运商类型（字典值：wms_carrier_type）
     * 1-快递，2-物流，3-专线
     */
    private Integer carrierType;

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
     * 邮箱
     * 示例：contact@sf-express.com
     */
    private String contactEmail;

    /**
     * 服务区域
     * 示例：全国范围
     */
    private String serviceArea;

    /**
     * 收费标准
     * 示例：首重1kg/15元，续重1kg/5元
     */
    private String priceStandard;

    /**
     * 时效要求
     * 示例：同城24小时，跨省48小时
     */
    private String timeLimit;

    /**
     * 服务评分（1-5分）
     * 默认值：5.00
     */
    private BigDecimal rating;

    /**
     * 合作开始日期
     */
    private LocalDate cooperationStartDate;

    /**
     * 状态
     * 1-启用，0-禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}

