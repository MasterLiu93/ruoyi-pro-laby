package com.laby.module.wms.service.picking;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.picking.vo.PickingTaskPageReqVO;
import com.laby.module.wms.controller.admin.picking.vo.PickingTaskPickReqVO;
import com.laby.module.wms.dal.dataobject.picking.PickingTaskDO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 拣货任务 Service 接口
 * 
 * 提供拣货任务的查询、执行、异常处理等功能
 *
 * @author laby
 */
public interface PickingTaskService {

    /**
     * 获取拣货任务详情
     *
     * @param id 拣货任务ID
     * @return 拣货任务DO
     */
    PickingTaskDO getPickingTask(Long id);

    /**
     * 获取拣货任务分页列表
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    PageResult<PickingTaskDO> getPickingTaskPage(PickingTaskPageReqVO pageReqVO);

    /**
     * 根据出库单ID查询拣货任务列表
     *
     * @param outboundId 出库单ID
     * @return 拣货任务列表
     */
    List<PickingTaskDO> getPickingTaskListByOutboundId(Long outboundId);

    /**
     * 根据波次ID查询拣货任务列表
     *
     * @param waveId 波次ID
     * @return 拣货任务列表
     */
    List<PickingTaskDO> getPickingTaskListByWaveId(Long waveId);

    /**
     * 分配拣货任务给拣货员
     * 
     * 业务说明：
     * - 只有"待拣货"状态的任务才能分配
     * - 分配后更新拣货员信息
     * - 可批量分配多个任务
     *
     * @param ids 拣货任务ID列表
     * @param pickerId 拣货员ID
     * @param pickerName 拣货员姓名
     */
    void assignPickingTasks(List<Long> ids, Long pickerId, String pickerName);

    /**
     * 执行拣货操作
     * 
     * 业务说明：
     * - 记录实际拣货数量
     * - 如果有异常，标记为异常状态
     * - 完成拣货后，更新出库单的拣货数量
     * - 扣减库存锁定量，增加实际出库量
     *
     * @param pickReqVO 拣货操作请求
     */
    void executePicking(@Valid PickingTaskPickReqVO pickReqVO);

    /**
     * 完成拣货任务
     * 
     * 业务说明：
     * - 将任务状态更新为"已完成"
     * - 记录拣货完成时间
     * - 通知出库单更新拣货进度
     *
     * @param id 拣货任务ID
     */
    void completePickingTask(Long id);

    /**
     * 取消拣货任务
     * 
     * 业务说明：
     * - 取消任务，恢复库存锁定
     * - 只有"待拣货"和"拣货中"状态可以取消
     *
     * @param id 拣货任务ID
     */
    void cancelPickingTask(Long id);

    /**
     * 标记拣货异常
     * 
     * 业务说明：
     * - 记录异常类型和说明
     * - 更新任务状态为"异常"
     * - 通知相关人员处理
     *
     * @param id 拣货任务ID
     * @param exceptionType 异常类型（字典值：1-库位空缺，2-数量不足，3-商品损坏，4-商品过期，5-商品错误）
     * @param exceptionRemark 异常说明
     */
    void markException(Long id, Integer exceptionType, String exceptionRemark);

    /**
     * 校验拣货任务是否存在
     *
     * @param id 拣货任务ID
     * @return 拣货任务DO
     */
    PickingTaskDO validatePickingTaskExists(Long id);
}

