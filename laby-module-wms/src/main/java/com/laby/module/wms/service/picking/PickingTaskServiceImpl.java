package com.laby.module.wms.service.picking;

import cn.hutool.core.collection.CollUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.picking.vo.PickingTaskPageReqVO;
import com.laby.module.wms.controller.admin.picking.vo.PickingTaskPickReqVO;
import com.laby.module.wms.dal.dataobject.picking.PickingTaskDO;
import com.laby.module.wms.dal.mysql.picking.PickingTaskMapper;
import com.laby.module.wms.enums.PickingTaskStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.module.wms.enums.ErrorCodeConstants.*;

/**
 * 拣货任务 Service 实现类
 * 
 * 实现说明：
 * - 所有公共方法都经过参数校验（@Validated）
 * - 状态流转：待拣货 → 拣货中 → 已完成/异常
 * - 拣货完成后需要更新出库单的拣货数量
 * - 使用 MapStruct 进行对象转换
 * - 异常统一使用 ServiceException
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class PickingTaskServiceImpl implements PickingTaskService {

    @Resource
    private PickingTaskMapper pickingTaskMapper;

    @Resource
    private com.laby.module.wms.service.outbound.OutboundService outboundService;

    @Override
    public PickingTaskDO getPickingTask(Long id) {
        return pickingTaskMapper.selectById(id);
    }

    @Override
    public PageResult<PickingTaskDO> getPickingTaskPage(PickingTaskPageReqVO pageReqVO) {
        return pickingTaskMapper.selectPage(pageReqVO);
    }

    @Override
    public List<PickingTaskDO> getPickingTaskListByOutboundId(Long outboundId) {
        return pickingTaskMapper.selectList(PickingTaskDO::getOutboundId, outboundId);
    }

    @Override
    public List<PickingTaskDO> getPickingTaskListByWaveId(Long waveId) {
        return pickingTaskMapper.selectList(PickingTaskDO::getWaveId, waveId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPickingTasks(List<Long> ids, Long pickerId, String pickerName) {
        // 1. 校验任务是否存在
        if (CollUtil.isEmpty(ids)) {
            return;
        }

        // 2. 批量更新拣货员信息
        for (Long id : ids) {
            PickingTaskDO task = validatePickingTaskExists(id);
            
            // 3. 校验任务状态（只有待拣货状态可以分配）
            if (!PickingTaskStatusEnum.PENDING.getStatus().equals(task.getStatus())) {
                throw exception(PICKING_TASK_STATUS_ERROR);
            }

            // 4. 更新拣货员信息，状态保持为"待拣货"
            PickingTaskDO updateObj = new PickingTaskDO();
            updateObj.setId(id);
            updateObj.setPickerId(pickerId);
            updateObj.setPickerName(pickerName);
            pickingTaskMapper.updateById(updateObj);
        }

        log.info("[拣货任务] 分配拣货任务成功，任务数量：{}，拣货员：{}", ids.size(), pickerName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executePicking(PickingTaskPickReqVO pickReqVO) {
        // 1. 校验任务是否存在
        PickingTaskDO task = validatePickingTaskExists(pickReqVO.getId());

        // 2. 校验任务状态（待拣货、拣货中都可以执行）
        if (PickingTaskStatusEnum.COMPLETED.getStatus().equals(task.getStatus())) {
            throw exception(PICKING_TASK_ALREADY_COMPLETED);
        }
        if (PickingTaskStatusEnum.EXCEPTION.getStatus().equals(task.getStatus())) {
            throw exception(PICKING_TASK_EXCEPTION_EXISTS);
        }

        // 3. 校验拣货数量（实际数量不能超过计划数量）
        if (pickReqVO.getActualQuantity().compareTo(task.getPlanQuantity()) > 0) {
            throw exception(PICKING_QUANTITY_EXCEED);
        }

        // 4. 构建更新对象
        PickingTaskDO updateObj = new PickingTaskDO();
        updateObj.setId(pickReqVO.getId());
        updateObj.setActualQuantity(pickReqVO.getActualQuantity());
        updateObj.setPickingTime(LocalDateTime.now());
        updateObj.setRemark(pickReqVO.getRemark());

        // 5. 判断是否有异常
        if (pickReqVO.getExceptionType() != null) {
            // 有异常，标记为异常状态
            updateObj.setStatus(PickingTaskStatusEnum.EXCEPTION.getStatus());
            updateObj.setExceptionType(pickReqVO.getExceptionType());
            updateObj.setExceptionRemark(pickReqVO.getExceptionRemark());
            log.warn("[拣货任务] 拣货异常，任务ID：{}，异常类型：{}，异常说明：{}", 
                    pickReqVO.getId(), pickReqVO.getExceptionType(), pickReqVO.getExceptionRemark());
        } else {
            // 无异常，标记为已完成
            updateObj.setStatus(PickingTaskStatusEnum.COMPLETED.getStatus());
            log.info("[拣货任务] 拣货完成，任务ID：{}，计划数量：{}，实际数量：{}", 
                    pickReqVO.getId(), task.getPlanQuantity(), pickReqVO.getActualQuantity());
        }

        // 6. 更新任务
        pickingTaskMapper.updateById(updateObj);

        // 7. 如果拣货完成（无异常），更新出库单的拣货数量
        if (pickReqVO.getExceptionType() == null) {
            outboundService.updatePickedQuantity(task.getOutboundId(), pickReqVO.getActualQuantity());
            log.info("[拣货任务] 已通知出库单更新拣货数量，出库单ID：{}，拣货数量：{}", 
                    task.getOutboundId(), pickReqVO.getActualQuantity());
        }
        
        // 8. checkAndUpdatePickingStatus 方法会自动检查所有拣货任务是否完成
        //    如果完成则更新出库单状态为"待发货"
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completePickingTask(Long id) {
        // 1. 校验任务是否存在
        PickingTaskDO task = validatePickingTaskExists(id);

        // 2. 校验任务状态（只有拣货中状态可以完成）
        if (!PickingTaskStatusEnum.PICKING.getStatus().equals(task.getStatus())) {
            throw exception(PICKING_TASK_STATUS_ERROR);
        }

        // 3. 更新任务状态
        PickingTaskDO updateObj = new PickingTaskDO();
        updateObj.setId(id);
        updateObj.setStatus(PickingTaskStatusEnum.COMPLETED.getStatus());
        updateObj.setPickingTime(LocalDateTime.now());
        pickingTaskMapper.updateById(updateObj);

        log.info("[拣货任务] 完成拣货任务，任务ID：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelPickingTask(Long id) {
        // 1. 校验任务是否存在
        PickingTaskDO task = validatePickingTaskExists(id);

        // 2. 校验任务状态（只有待拣货、拣货中状态可以取消）
        if (PickingTaskStatusEnum.COMPLETED.getStatus().equals(task.getStatus()) ||
            PickingTaskStatusEnum.EXCEPTION.getStatus().equals(task.getStatus())) {
            throw exception(PICKING_TASK_STATUS_ERROR);
        }

        // 3. 删除任务（软删除）
        pickingTaskMapper.deleteById(id);

        log.info("[拣货任务] 取消拣货任务，任务ID：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markException(Long id, Integer exceptionType, String exceptionRemark) {
        // 1. 校验任务是否存在
        PickingTaskDO task = validatePickingTaskExists(id);

        // 2. 校验任务状态（已完成的任务不能标记异常）
        if (PickingTaskStatusEnum.COMPLETED.getStatus().equals(task.getStatus())) {
            throw exception(PICKING_TASK_ALREADY_COMPLETED);
        }

        // 3. 更新任务状态
        PickingTaskDO updateObj = new PickingTaskDO();
        updateObj.setId(id);
        updateObj.setStatus(PickingTaskStatusEnum.EXCEPTION.getStatus());
        updateObj.setExceptionType(exceptionType);
        updateObj.setExceptionRemark(exceptionRemark);
        pickingTaskMapper.updateById(updateObj);

        log.warn("[拣货任务] 标记拣货异常，任务ID：{}，异常类型：{}，异常说明：{}", 
                id, exceptionType, exceptionRemark);
    }

    @Override
    public PickingTaskDO validatePickingTaskExists(Long id) {
        PickingTaskDO task = pickingTaskMapper.selectById(id);
        if (task == null) {
            throw exception(PICKING_TASK_NOT_EXISTS);
        }
        return task;
    }
}

