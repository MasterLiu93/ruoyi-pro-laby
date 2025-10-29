package com.laby.module.wms.controller.admin.picking;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.picking.vo.PickingTaskPageReqVO;
import com.laby.module.wms.controller.admin.picking.vo.PickingTaskPickReqVO;
import com.laby.module.wms.controller.admin.picking.vo.PickingTaskRespVO;
import com.laby.module.wms.convert.picking.PickingTaskConvert;
import com.laby.module.wms.dal.dataobject.picking.PickingTaskDO;
import com.laby.module.wms.service.picking.PickingTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * WMS拣货任务 Controller
 *
 * 功能说明：
 * 1. 拣货任务的查询
 * 2. 拣货任务的分配（分配给拣货员）
 * 3. 拣货任务的执行（拣货员执行拣货）
 * 4. 拣货任务的完成、取消
 * 5. 拣货异常的标记和处理
 *
 * @author laby
 */
@Tag(name = "管理后台 - WMS拣货任务")
@RestController
@RequestMapping("/wms/picking-task")
@Validated
@Slf4j
public class PickingTaskController {

    @Resource
    private PickingTaskService pickingTaskService;

    /**
     * 获取拣货任务详情
     *
     * @param id 拣货任务ID
     * @return 拣货任务详情
     */
    @GetMapping("/get")
    @Operation(summary = "获取拣货任务详情")
    @Parameter(name = "id", description = "拣货任务ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:picking-task:query')")
    public CommonResult<PickingTaskRespVO> getPickingTask(@RequestParam("id") Long id) {
        PickingTaskDO task = pickingTaskService.getPickingTask(id);
        return success(PickingTaskConvert.INSTANCE.convert(task));
    }

    /**
     * 获取拣货任务分页列表
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "获取拣货任务分页列表")
    @PreAuthorize("@ss.hasPermission('wms:picking-task:query')")
    public CommonResult<PageResult<PickingTaskRespVO>> getPickingTaskPage(@Valid PickingTaskPageReqVO pageReqVO) {
        PageResult<PickingTaskDO> pageResult = pickingTaskService.getPickingTaskPage(pageReqVO);
        return success(PickingTaskConvert.INSTANCE.convertPage(pageResult));
    }

    /**
     * 根据出库单ID查询拣货任务列表
     *
     * @param outboundId 出库单ID
     * @return 拣货任务列表
     */
    @GetMapping("/list-by-outbound")
    @Operation(summary = "根据出库单ID查询拣货任务列表")
    @Parameter(name = "outboundId", description = "出库单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:picking-task:query')")
    public CommonResult<List<PickingTaskRespVO>> getPickingTaskListByOutboundId(@RequestParam("outboundId") Long outboundId) {
        List<PickingTaskDO> list = pickingTaskService.getPickingTaskListByOutboundId(outboundId);
        return success(PickingTaskConvert.INSTANCE.convertList(list));
    }

    /**
     * 分配拣货任务
     *
     * 业务说明：
     * - 将待拣货的任务分配给指定拣货员
     * - 可批量分配多个任务
     * - 只有"待拣货"状态的任务才能分配
     *
     * @param ids 拣货任务ID列表
     * @param pickerId 拣货员ID
     * @param pickerName 拣货员姓名
     * @return 是否成功
     */
    @PutMapping("/assign")
    @Operation(summary = "分配拣货任务")
    @PreAuthorize("@ss.hasPermission('wms:picking-task:execute')")
    public CommonResult<Boolean> assignPickingTasks(
            @RequestParam("ids") List<Long> ids,
            @RequestParam("pickerId") Long pickerId,
            @RequestParam("pickerName") String pickerName) {
        pickingTaskService.assignPickingTasks(ids, pickerId, pickerName);
        return success(true);
    }

    /**
     * 执行拣货操作
     *
     * 业务说明：
     * - 拣货员执行拣货，提交拣货结果
     * - 记录实际拣货数量
     * - 如果发生异常（库位空、库存不足等），需填写异常信息
     * - 拣货完成后，更新出库单的拣货数量
     *
     * @param pickReqVO 拣货操作请求
     * @return 是否成功
     */
    @PutMapping("/pick")
    @Operation(summary = "执行拣货操作")
    @PreAuthorize("@ss.hasPermission('wms:picking-task:execute')")
    public CommonResult<Boolean> executePicking(@Valid @RequestBody PickingTaskPickReqVO pickReqVO) {
        pickingTaskService.executePicking(pickReqVO);
        return success(true);
    }

    /**
     * 完成拣货任务
     *
     * @param id 拣货任务ID
     * @return 是否成功
     */
    @PutMapping("/complete")
    @Operation(summary = "完成拣货任务")
    @Parameter(name = "id", description = "拣货任务ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:picking-task:execute')")
    public CommonResult<Boolean> completePickingTask(@RequestParam("id") Long id) {
        pickingTaskService.completePickingTask(id);
        return success(true);
    }

    /**
     * 取消拣货任务
     *
     * @param id 拣货任务ID
     * @return 是否成功
     */
    @DeleteMapping("/cancel")
    @Operation(summary = "取消拣货任务")
    @Parameter(name = "id", description = "拣货任务ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:picking-task:execute')")
    public CommonResult<Boolean> cancelPickingTask(@RequestParam("id") Long id) {
        pickingTaskService.cancelPickingTask(id);
        return success(true);
    }

    /**
     * 标记拣货异常
     *
     * @param id 拣货任务ID
     * @param exceptionType 异常类型
     * @param exceptionRemark 异常说明
     * @return 是否成功
     */
    @PutMapping("/mark-exception")
    @Operation(summary = "标记拣货异常")
    @PreAuthorize("@ss.hasPermission('wms:picking-task:exception')")
    public CommonResult<Boolean> markException(
            @RequestParam("id") Long id,
            @RequestParam("exceptionType") Integer exceptionType,
            @RequestParam(value = "exceptionRemark", required = false) String exceptionRemark) {
        pickingTaskService.markException(id, exceptionType, exceptionRemark);
        return success(true);
    }
}

