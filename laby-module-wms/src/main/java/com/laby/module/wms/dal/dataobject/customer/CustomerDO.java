package com.laby.module.wms.dal.dataobject.customer;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 客户信息 DO
 * 对应数据库表：wms_customer
 *
 * 功能说明：
 * 1. 存储客户基本信息（编码、名称、类型、等级等）
 * 2. 存储客户联系信息（联系人、电话、邮箱）
 * 3. 存储客户收货地址信息（省市区、详细地址）
 * 4. 记录客户信用额度、累计订单数、累计金额
 *
 * @author laby
 */
@TableName("wms_customer")
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerDO extends BaseDO {

    /**
     * 客户ID（主键）
     */
    @TableId
    private Long id;

    /**
     * 客户编码（唯一标识）
     * 示例：CUST-001
     */
    private String customerCode;

    /**
     * 客户名称
     * 示例：深圳市XX商贸有限公司
     */
    private String customerName;

    /**
     * 客户类型（字典字段）
     * 字典类型：wms_customer_type
     * 1-零售客户，2-批发客户，3-企业客户
     */
    private Integer customerType;

    /**
     * 客户等级（字典字段）
     * 字典类型：wms_customer_level
     * 1-VIP客户，2-金牌客户，3-银牌客户，4-普通客户
     */
    private Integer customerLevel;

    /**
     * 联系人
     * 示例：李四
     */
    private String contactPerson;

    /**
     * 联系电话
     * 示例：13900139000
     */
    private String contactPhone;

    /**
     * 邮箱
     * 示例：lisi@example.com
     */
    private String contactEmail;

    /**
     * 收货省份
     * 示例：广东省
     */
    private String deliveryProvince;

    /**
     * 收货城市
     * 示例：深圳市
     */
    private String deliveryCity;

    /**
     * 收货区县
     * 示例：福田区
     */
    private String deliveryDistrict;

    /**
     * 收货地址
     * 示例：华强北路1001号
     */
    private String deliveryAddress;

    /**
     * 信用额度
     * 示例：100000.00
     */
    private BigDecimal creditLimit;

    /**
     * 累计订单数
     * 示例：50
     */
    private Integer totalOrders;

    /**
     * 累计金额
     * 示例：500000.00
     */
    private BigDecimal totalAmount;

    /**
     * 状态
     * 0-禁用，1-启用
     */
    private Integer status;

    /**
     * 备注
     * 示例：优质客户，长期合作
     */
    private String remark;
}

