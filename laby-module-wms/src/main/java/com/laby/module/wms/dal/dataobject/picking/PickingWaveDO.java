package com.laby.module.wms.dal.dataobject.picking;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拣货波次 DO
 * 
 * 对应数据库表：wms_picking_wave
 * 
 * 业务说明：
 * - 拣货波次是将多个出库单合并成一个批次进行拣货
 * - 可以提高拣货效率，减少拣货路径
 * - 支持批次拣货、分区拣货、单品拣货等多种模式
 * - 状态流转：待分配 → 已分配 → 拣货中 → 已完成/已取消
 * 
 * @author laby
 */
@TableName("wms_picking_wave")
@KeySequence("wms_picking_wave_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickingWaveDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 波次号
     * 规则：WAVE-yyyyMMdd-流水号
     */
    private String waveNo;

    /**
     * 仓库ID
     * 关联 wms_warehouse.id
     */
    private Long warehouseId;

    /**
     * 波次类型
     * 字典类型：wms_wave_type
     * 1-批次拣货, 2-分区拣货, 3-单品拣货
     */
    private Integer waveType;

    /**
     * 订单数量
     * 关联的出库单数量
     */
    private Integer orderCount;

    /**
     * 商品种类数
     */
    private Integer itemCount;

    /**
     * 总数量
     */
    private BigDecimal totalQuantity;

    /**
     * 优先级
     * 1-普通, 2-紧急, 3-特急
     */
    private Integer priority;

    /**
     * 拣货员ID
     * 关联 system_user.id
     */
    private Long pickerId;

    /**
     * 拣货员姓名（冗余字段）
     */
    private String pickerName;

    /**
     * 预计耗时（秒）
     */
    private Integer estimatedTime;

    /**
     * 实际耗时（秒）
     */
    private Integer actualTime;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 波次状态
     * 字典类型：wms_wave_status
     * 1-待分配, 2-已分配, 3-拣货中, 4-已完成, 5-已取消
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}

