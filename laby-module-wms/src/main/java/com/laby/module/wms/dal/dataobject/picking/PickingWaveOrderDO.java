package com.laby.module.wms.dal.dataobject.picking;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 拣货波次订单关联 DO
 * 
 * 对应数据库表：wms_picking_wave_order
 * 
 * 业务说明：
 * - 记录拣货波次和出库单的关联关系
 * - 一个波次可以包含多个出库单
 * - 一个出库单只能属于一个波次
 * 
 * @author laby
 */
@TableName("wms_picking_wave_order")
@KeySequence("wms_picking_wave_order_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickingWaveOrderDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 波次ID
     * 关联 wms_picking_wave.id
     */
    private Long waveId;

    /**
     * 波次号（冗余字段）
     */
    private String waveNo;

    /**
     * 出库单ID
     * 关联 wms_outbound.id
     */
    private Long outboundId;

    /**
     * 出库单号（冗余字段）
     */
    private String outboundNo;
}

