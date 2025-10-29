package com.laby.module.wms.dal.dataobject.supplier;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 供应商信息 DO
 * 对应数据库表：wms_supplier
 *
 * 功能说明：
 * 1. 存储供应商基本信息（编码、名称、类型、信用等级等）
 * 2. 存储供应商联系信息（联系人、电话、邮箱）
 * 3. 存储供应商地址信息（省市区、详细地址）
 * 4. 记录合作开始日期
 *
 * @author laby
 */
@TableName("wms_supplier")
@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierDO extends BaseDO {

    /**
     * 供应商ID（主键）
     */
    @TableId
    private Long id;

    /**
     * 供应商编码（唯一标识）
     * 示例：SUP-001
     */
    private String supplierCode;

    /**
     * 供应商名称
     * 示例：深圳市华强电子有限公司
     */
    private String supplierName;

    /**
     * 供应商类型（字典字段）
     * 字典类型：wms_supplier_type
     * 1-普通供应商，2-VIP供应商，3-战略供应商
     */
    private Integer supplierType;

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
     * 示例：zhangsan@example.com
     */
    private String contactEmail;

    /**
     * 省份
     * 示例：广东省
     */
    private String province;

    /**
     * 城市
     * 示例：深圳市
     */
    private String city;

    /**
     * 区县
     * 示例：南山区
     */
    private String district;

    /**
     * 详细地址
     * 示例：科技园南区深南大道9988号
     */
    private String address;

    /**
     * 信用等级（字典字段）
     * 字典类型：wms_supplier_credit_level
     * 1-优秀，2-良好，3-一般，4-较差
     */
    private Integer creditLevel;

    /**
     * 合作开始日期
     * 示例：2023-01-01
     */
    private LocalDate cooperationStartDate;

    /**
     * 状态
     * 0-禁用，1-启用
     */
    private Integer status;

    /**
     * 备注
     * 示例：长期合作伙伴，质量稳定
     */
    private String remark;
}

