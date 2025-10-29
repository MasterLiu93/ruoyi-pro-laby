package com.laby.module.wms.dal.dataobject.picking;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.laby.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拣货任务 DO
 * 
 * 对应数据库表：wms_picking_task
 * 
 * 业务说明：
 * - 每个拣货任务对应一个"商品+库位"组合
 * - 可以关联到波次，也可以单独存在
 * - 状态流转：待拣货 → 拣货中 → 已完成/异常
 * 
 * @author laby
 */
@TableName("wms_picking_task")
@KeySequence("wms_picking_task_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickingTaskDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 任务编号
     * 规则：PICK-yyyyMMdd-流水号
     */
    private String taskNo;

    /**
     * 波次ID（可选）
     * 关联 wms_picking_wave.id
     */
    private Long waveId;

    /**
     * 波次号（冗余字段）
     */
    private String waveNo;

    /**
     * 出库单ID（必填）
     * 关联 wms_outbound.id
     */
    private Long outboundId;

    /**
     * 出库单号（冗余字段）
     */
    private String outboundNo;

    /**
     * 仓库ID
     * 关联 wms_warehouse.id
     */
    private Long warehouseId;

    /**
     * 商品ID
     * 关联 wms_goods.id
     */
    private Long goodsId;

    /**
     * SKU编码（冗余字段）
     */
    private String skuCode;

    /**
     * 商品名称（冗余字段）
     */
    private String goodsName;

    /**
     * 批次号（可选）
     */
    private String batchNo;

    /**
     * 库位ID
     * 关联 wms_warehouse_location.id
     */
    private Long locationId;

    /**
     * 库位编码（冗余字段）
     */
    private String locationCode;

    /**
     * 计划拣货数量
     */
    private BigDecimal planQuantity;

    /**
     * 实际拣货数量
     * 默认值：0
     */
    private BigDecimal actualQuantity;

    /**
     * 拣货顺序
     * 用于优化拣货路径，数值越小优先级越高
     */
    private Integer sortOrder;

    /**
     * 拣货员ID（分配后填写）
     * 关联 system_user.id
     */
    private Long pickerId;

    /**
     * 拣货员姓名（冗余字段）
     */
    private String pickerName;

    /**
     * 拣货时间（完成时填写）
     */
    private LocalDateTime pickingTime;

    /**
     * 拣货任务状态
     * 字典类型：wms_picking_task_status
     * 1-待拣货，2-拣货中，3-已完成，4-异常
     */
    private Integer status;

    /**
     * 异常类型（发生异常时填写）
     * 字典类型：wms_picking_exception_type
     * 1-库位空, 2-库存不足, 3-商品损坏, 4-商品过期, 5-拣错商品
     */
    private Integer exceptionType;

    /**
     * 异常说明
     */
    private String exceptionRemark;

    /**
     * 备注
     */
    private String remark;
}

