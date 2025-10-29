package com.laby.module.wms.controller.admin.outbound;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundPageReqVO;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundRespVO;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundSaveReqVO;
import com.laby.module.wms.service.outbound.OutboundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * 出库单 Controller
 * 管理端出库单的 CRUD 和业务操作 API
 *
 * 功能说明：
 * 1. 基础CRUD：创建、更新、删除、查询、分页
 * 2. 业务操作：审核、拣货、发货、完成、取消
 * 3. 权限控制：所有操作都需要相应权限
 * 4. 库存管理：出库时会扣减库存并记录流水
 *
 * @author laby
 */
@Tag(name = "管理后台 - WMS出库单")
@RestController
@RequestMapping("/wms/outbound")
@Validated
@Slf4j
public class OutboundController {

    @Resource
    private OutboundService outboundService;

    /**
     * 创建出库单
     *
     * 业务说明：
     * - 创建后状态为"待审核"
     * - 自动生成出库单号（规则：OUT + yyyyMMdd + 流水号）
     * - 必须包含至少一条明细
     * - 校验仓库、客户、商品是否存在
     *
     * @param createReqVO 创建信息，包含出库类型、仓库、客户、明细等
     * @return 出库单ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建出库单")
    @PreAuthorize("@ss.hasPermission('wms:outbound:create')")
    public CommonResult<Long> createOutbound(@Valid @RequestBody OutboundSaveReqVO createReqVO) {
        return success(outboundService.createOutbound(createReqVO));
    }

    /**
     * 更新出库单
     *
     * 业务说明：
     * - 只有"待审核"状态可以修改
     * - 修改时会删除旧明细并新增新明细
     * - 重新计算总数量和总金额
     *
     * @param updateReqVO 更新信息，必须包含ID
     * @return 是否成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新出库单")
    @PreAuthorize("@ss.hasPermission('wms:outbound:update')")
    public CommonResult<Boolean> updateOutbound(@Valid @RequestBody OutboundSaveReqVO updateReqVO) {
        outboundService.updateOutbound(updateReqVO);
        return success(true);
    }

    /**
     * 删除出库单
     *
     * 业务说明：
     * - 只有"待审核"状态可以删除
     * - 删除为逻辑删除，不会物理删除数据
     * - 删除时会同时删除出库单明细
     *
     * @param id 出库单ID
     * @return 是否成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除出库单")
    @Parameter(name = "id", description = "出库单ID", required = true)
    @PreAuthorize("@ss.hasPermission('wms:outbound:delete')")
    public CommonResult<Boolean> deleteOutbound(@RequestParam("id") Long id) {
        outboundService.deleteOutbound(id);
        return success(true);
    }

    /**
     * 获得出库单详情
     *
     * 返回内容：
     * 1. 出库单主表信息（包含仓库名称等关联字段）
     * 2. 出库单明细列表（包含商品名称、SKU、库位等关联字段）
     *
     * @param id 出库单ID
     * @return 出库单详细信息
     */
    @GetMapping("/get")
    @Operation(summary = "获得出库单详情")
    @Parameter(name = "id", description = "出库单ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:outbound:query')")
    public CommonResult<OutboundRespVO> getOutbound(@RequestParam("id") Long id) {
        OutboundRespVO outbound = outboundService.getOutbound(id);
        return success(outbound);
    }

    /**
     * 获得出库单分页列表
     *
     * 查询条件：
     * 1. 支持按出库单号、出库类型、仓库、状态查询
     * 2. 支持按创建时间范围查询
     * 3. 列表会关联查询仓库名称
     *
     * @param pageVO 分页查询条件
     * @return 出库单分页列表
     */
    @GetMapping("/page")
    @Operation(summary = "获得出库单分页列表")
    @PreAuthorize("@ss.hasPermission('wms:outbound:query')")
    public CommonResult<PageResult<OutboundRespVO>> getOutboundPage(@Valid OutboundPageReqVO pageVO) {
        PageResult<OutboundRespVO> pageResult = outboundService.getOutboundPage(pageVO);
        return success(pageResult);
    }

    /**
     * 审核出库单
     *
     * 业务流程：
     * 1. 仅"待审核"状态可以审核
     * 2. 审核通过后状态变为"已审核"
     * 3. 记录审核人和审核时间
     *
     * @param id 出库单ID
     * @param auditBy 审核人ID
     * @param auditByName 审核人名称
     * @return 是否成功
     */
    @PostMapping("/audit")
    @Operation(summary = "审核出库单")
    @Parameter(name = "id", description = "出库单ID", required = true)
    @PreAuthorize("@ss.hasPermission('wms:outbound:audit')")
    public CommonResult<Boolean> auditOutbound(@RequestParam("id") Long id,
                                                 @RequestParam("auditBy") Long auditBy,
                                                 @RequestParam("auditByName") String auditByName) {
        outboundService.auditOutbound(id, auditBy, auditByName);
        return success(true);
    }

    /**
     * 开始拣货
     *
     * 业务流程：
     * 1. 仅"已审核"状态可以开始拣货
     * 2. 状态变为"拣货中"
     * 3. 记录拣货开始时间
     *
     * @param id 出库单ID
     * @return 是否成功
     */
    @PostMapping("/start-picking")
    @Operation(summary = "开始拣货")
    @Parameter(name = "id", description = "出库单ID", required = true)
    @PreAuthorize("@ss.hasPermission('wms:outbound:pick')")
    public CommonResult<Boolean> startPicking(@RequestParam("id") Long id) {
        outboundService.startPicking(id);
        return success(true);
    }

    /**
     * 完成拣货（单个明细）
     *
     * 业务流程：
     * 1. 更新明细的已拣货数量
     * 2. 累加出库单主表的已拣货数量
     * 3. 如果所有明细都拣货完成，状态不会自动变更（需手动发货）
     *
     * @param id 出库单ID
     * @param itemId 明细ID
     * @param pickedQuantity 已拣货数量
     * @return 是否成功
     */
    @PostMapping("/complete-picking")
    @Operation(summary = "完成拣货")
    @PreAuthorize("@ss.hasPermission('wms:outbound:pick')")
    public CommonResult<Boolean> completePicking(@RequestParam("id") Long id,
                                                   @RequestParam("itemId") Long itemId,
                                                   @RequestParam("pickedQuantity") BigDecimal pickedQuantity) {
        outboundService.completePicking(id, itemId, pickedQuantity);
        return success(true);
    }

    /**
     * 完成发货
     *
     * 业务流程：
     * 1. 仅"拣货中"状态可以完成发货
     * 2. 状态变为"已完成"
     * 3. 扣减库存数量（根据已拣货数量）
     * 4. 记录完成人和完成时间
     * 5. 记录库存流水
     *
     * @param id 出库单ID
     * @param completeBy 完成人ID
     * @param completeByName 完成人名称
     * @return 是否成功
     */
    @PostMapping("/complete")
    @Operation(summary = "完成发货")
    @Parameter(name = "id", description = "出库单ID", required = true)
    @PreAuthorize("@ss.hasPermission('wms:outbound:complete')")
    public CommonResult<Boolean> completeOutbound(@RequestParam("id") Long id,
                                                    @RequestParam("completeBy") Long completeBy,
                                                    @RequestParam("completeByName") String completeByName) {
        outboundService.shipOutbound(id, completeBy, completeByName);
        return success(true);
    }

    /**
     * 取消出库单
     *
     * 业务说明：
     * - "已完成"状态不能取消
     * - 已取消的出库单不会影响库存
     * - 状态变为"已取消"
     *
     * @param id 出库单ID
     * @return 是否成功
     */
    @PostMapping("/cancel")
    @Operation(summary = "取消出库单")
    @Parameter(name = "id", description = "出库单ID", required = true)
    @PreAuthorize("@ss.hasPermission('wms:outbound:cancel')")
    public CommonResult<Boolean> cancelOutbound(@RequestParam("id") Long id) {
        outboundService.cancelOutbound(id);
        return success(true);
    }

}
