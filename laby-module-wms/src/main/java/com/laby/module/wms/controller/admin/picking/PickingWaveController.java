package com.laby.module.wms.controller.admin.picking;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.picking.vo.PickingWavePageReqVO;
import com.laby.module.wms.controller.admin.picking.vo.PickingWaveRespVO;
import com.laby.module.wms.controller.admin.picking.vo.PickingWaveSaveReqVO;
import com.laby.module.wms.service.picking.PickingWaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * WMS拣货波次 Controller
 *
 * 功能说明：
 * 1. 拣货波次的创建、修改、删除、查询
 * 2. 拣货波次的分配（分配给拣货员）
 * 3. 拣货波次的执行（开始、完成、取消）
 * 4. 支持自动生成拣货波次
 *
 * @author laby
 */
@Tag(name = "管理后台 - WMS拣货波次")
@RestController
@RequestMapping("/wms/picking-wave")
@Validated
@Slf4j
public class PickingWaveController {

    @Resource
    private PickingWaveService pickingWaveService;

    /**
     * 创建拣货波次
     *
     * 业务说明：
     * - 选择多个出库单生成拣货波次
     * - 自动生成波次号
     * - 初始状态为"待分配"
     *
     * @param createReqVO 创建信息
     * @return 波次ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建拣货波次")
    @PreAuthorize("@ss.hasPermission('wms:picking-wave:create')")
    public CommonResult<Long> createPickingWave(@Valid @RequestBody PickingWaveSaveReqVO createReqVO) {
        return success(pickingWaveService.createPickingWave(createReqVO));
    }

    /**
     * 更新拣货波次
     *
     * @param updateReqVO 更新信息
     * @return 是否成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新拣货波次")
    @PreAuthorize("@ss.hasPermission('wms:picking-wave:create')")
    public CommonResult<Boolean> updatePickingWave(@Valid @RequestBody PickingWaveSaveReqVO updateReqVO) {
        pickingWaveService.updatePickingWave(updateReqVO);
        return success(true);
    }

    /**
     * 删除拣货波次
     *
     * @param id 波次ID
     * @return 是否成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除拣货波次")
    @Parameter(name = "id", description = "波次ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:picking-wave:create')")
    public CommonResult<Boolean> deletePickingWave(@RequestParam("id") Long id) {
        pickingWaveService.deletePickingWave(id);
        return success(true);
    }

    /**
     * 获取拣货波次详情
     *
     * @param id 波次ID
     * @return 波次详情
     */
    @GetMapping("/get")
    @Operation(summary = "获取拣货波次详情")
    @Parameter(name = "id", description = "波次ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:picking-wave:query')")
    public CommonResult<PickingWaveRespVO> getPickingWave(@RequestParam("id") Long id) {
        return success(pickingWaveService.getPickingWave(id));
    }

    /**
     * 获取拣货波次分页列表
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "获取拣货波次分页列表")
    @PreAuthorize("@ss.hasPermission('wms:picking-wave:query')")
    public CommonResult<PageResult<PickingWaveRespVO>> getPickingWavePage(@Valid PickingWavePageReqVO pageReqVO) {
        return success(pickingWaveService.getPickingWavePage(pageReqVO));
    }

    /**
     * 分配拣货员
     *
     * 业务说明：
     * - 将波次分配给指定拣货员
     * - 更新状态为"已分配"
     *
     * @param id 波次ID
     * @param pickerId 拣货员ID
     * @param pickerName 拣货员姓名
     * @return 是否成功
     */
    @PutMapping("/assign")
    @Operation(summary = "分配拣货员")
    @PreAuthorize("@ss.hasPermission('wms:picking-wave:assign')")
    public CommonResult<Boolean> assignPicker(
            @RequestParam("id") Long id,
            @RequestParam("pickerId") Long pickerId,
            @RequestParam("pickerName") String pickerName) {
        pickingWaveService.assignPicker(id, pickerId, pickerName);
        return success(true);
    }

    /**
     * 开始拣货
     *
     * @param id 波次ID
     * @return 是否成功
     */
    @PutMapping("/start")
    @Operation(summary = "开始拣货")
    @Parameter(name = "id", description = "波次ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:picking-wave:assign')")
    public CommonResult<Boolean> startPicking(@RequestParam("id") Long id) {
        pickingWaveService.startPicking(id);
        return success(true);
    }

    /**
     * 完成拣货波次
     *
     * @param id 波次ID
     * @return 是否成功
     */
    @PutMapping("/complete")
    @Operation(summary = "完成拣货波次")
    @Parameter(name = "id", description = "波次ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:picking-wave:assign')")
    public CommonResult<Boolean> completePickingWave(@RequestParam("id") Long id) {
        pickingWaveService.completePickingWave(id);
        return success(true);
    }

    /**
     * 取消拣货波次
     *
     * @param id 波次ID
     * @return 是否成功
     */
    @PutMapping("/cancel")
    @Operation(summary = "取消拣货波次")
    @Parameter(name = "id", description = "波次ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:picking-wave:cancel')")
    public CommonResult<Boolean> cancelPickingWave(@RequestParam("id") Long id) {
        pickingWaveService.cancelPickingWave(id);
        return success(true);
    }
}

