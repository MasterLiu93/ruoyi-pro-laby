package com.laby.module.wms.dal.dataobject.stocktaking;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 盘点计划 DO
 * 
 * 盘点计划管理，包括全盘、循环盘、抽盘、动态盘等多种盘点类型
 *
 * @author laby
 */
@TableName(value = "wms_stock_taking_plan", autoResultMap = true)
@KeySequence("wms_stock_taking_plan_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTakingPlanDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 盘点计划编号
     */
    private String planNo;

    /**
     * 盘点计划名称
     */
    private String planName;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 盘点类型（字典值）
     * 字典类型：wms_stock_taking_type
     * 1-全盘，2-循环盘，3-抽盘，4-动态盘
     */
    private Integer takingType;

    /**
     * 盘点范围（字典值）
     * 字典类型：wms_stock_taking_scope_type
     * 1-全仓，2-库区，3-库位，4-商品
     */
    private Integer scopeType;

    /**
     * 范围值（JSON数组）
     * 根据scopeType存储不同的ID数组
     */
    private String scopeValue;

    /**
     * 计划开始时间
     */
    private LocalDateTime planStartTime;

    /**
     * 计划结束时间
     */
    private LocalDateTime planEndTime;

    /**
     * 实际开始时间
     */
    private LocalDateTime actualStartTime;

    /**
     * 实际结束时间
     */
    private LocalDateTime actualEndTime;

    /**
     * 盘点总数
     */
    private Integer totalCount;

    /**
     * 已完成数
     */
    private Integer completedCount;

    /**
     * 差异数
     */
    private Integer diffCount;

    /**
     * 状态（字典值）
     * 字典类型：wms_stock_taking_plan_status
     * 1-待审核，2-待执行，3-执行中，4-已完成，5-已取消
     */
    private Integer status;

    /**
     * 审核人
     */
    private String auditUser;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 备注
     */
    private String remark;

}

