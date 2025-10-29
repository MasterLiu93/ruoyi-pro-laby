package com.laby.module.wms.controller.admin.inbound;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.inbound.vo.InboundPageReqVO;
import com.laby.module.wms.controller.admin.inbound.vo.InboundRespVO;
import com.laby.module.wms.controller.admin.inbound.vo.InboundSaveReqVO;
import com.laby.module.wms.service.inbound.InboundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * 入库单 Controller
 * 管理端入库单的 CRUD 和业务操作 API
 *
 * 功能说明：
 * 1. 基础CRUD：创建、更新、删除、查询、分页
 * 2. 业务操作：审核、开始收货、完成收货、取消
 * 3. 权限控制：所有操作都需要相应权限
 *
 * @author laby
 */
@Tag(name = "管理后台 - 入库单")
@RestController
@RequestMapping("/wms/inbound")
@Validated
public class InboundController {

    @Resource
    private InboundService inboundService;

    /**
     * 创建入库单
     *
     * 业务说明：
     * - 创建后状态为"待审核"
     * - 自动生成入库单号
     * - 必须包含至少一条明细
     *
     * @param createReqVO 创建信息
     * @return 入库单ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建入库单")
    @PreAuthorize("@ss.hasPermission('wms:inbound:create')")
    public CommonResult<Long> createInbound(@Valid @RequestBody InboundSaveReqVO createReqVO) {
        return success(inboundService.createInbound(createReqVO));
    }

    /**
     * 更新入库单
     *
     * 业务说明：
     * - 只有"待审核"状态可以修改
     * - 修改时会删除旧明细并新增新明细
     *
     * @param updateReqVO 更新信息
     * @return 是否成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新入库单")
    @PreAuthorize("@ss.hasPermission('wms:inbound:update')")
    public CommonResult<Boolean> updateInbound(@Valid @RequestBody InboundSaveReqVO updateReqVO) {
        inboundService.updateInbound(updateReqVO);
        return success(true);
    }

    /**
     * 删除入库单
     *
     * 业务说明：
     * - 只有"待审核"状态可以删除
     * - 删除时会同时删除明细
     *
     * @param id 入库单ID
     * @return 是否成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除入库单")
    @Parameter(name = "id", description = "入库单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:inbound:delete')")
    public CommonResult<Boolean> deleteInbound(@RequestParam("id") Long id) {
        inboundService.deleteInbound(id);
        return success(true);
    }

    /**
     * 获得入库单详情
     *
     * @param id 入库单ID
     * @return 入库单详情（包含明细和关联字段）
     */
    @GetMapping("/get")
    @Operation(summary = "获得入库单详情")
    @Parameter(name = "id", description = "入库单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:inbound:query')")
    public CommonResult<InboundRespVO> getInbound(@RequestParam("id") Long id) {
        return success(inboundService.getInbound(id));
    }

    /**
     * 获得入库单分页列表
     *
     * @param pageReqVO 分页查询条件
     * @return 分页列表（包含关联字段）
     */
    @GetMapping("/page")
    @Operation(summary = "获得入库单分页列表")
    @PreAuthorize("@ss.hasPermission('wms:inbound:query')")
    public CommonResult<PageResult<InboundRespVO>> getInboundPage(@Valid InboundPageReqVO pageReqVO) {
        return success(inboundService.getInboundPage(pageReqVO));
    }

    /**
     * 审核入库单
     *
     * 业务说明：
     * - 只有"待审核"状态可以审核
     * - 审核后状态变为"已审核"
     * - 记录审核人和审核时间
     *
     * @param id 入库单ID
     * @return 是否成功
     */
    @PutMapping("/audit")
    @Operation(summary = "审核入库单")
    @Parameter(name = "id", description = "入库单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:inbound:audit')")
    public CommonResult<Boolean> auditInbound(@RequestParam("id") Long id) {
        inboundService.auditInbound(id);
        return success(true);
    }

    /**
     * 开始收货
     *
     * 业务说明：
     * - 只有"已审核"状态可以开始收货
     * - 状态变为"收货中"
     * - 记录实际到货时间
     *
     * @param id 入库单ID
     * @return 是否成功
     */
    @PutMapping("/start-receiving")
    @Operation(summary = "开始收货")
    @Parameter(name = "id", description = "入库单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:inbound:receive')")
    public CommonResult<Boolean> startReceiving(@RequestParam("id") Long id) {
        inboundService.startReceiving(id);
        return success(true);
    }

    /**
     * 完成收货
     *
     * 业务说明：
     * - 只有"收货中"状态可以完成
     * - 状态变为"已完成"
     * - 更新库存
     * - 记录完成人和完成时间
     *
     * @param id 入库单ID
     * @return 是否成功
     */
    @PutMapping("/complete")
    @Operation(summary = "完成收货")
    @Parameter(name = "id", description = "入库单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:inbound:complete')")
    public CommonResult<Boolean> completeInbound(@RequestParam("id") Long id) {
        inboundService.completeInbound(id);
        return success(true);
    }

    /**
     * 取消入库单
     *
     * 业务说明：
     * - "已完成"状态不能取消
     * - 状态变为"已取消"
     *
     * @param id 入库单ID
     * @return 是否成功
     */
    @PutMapping("/cancel")
    @Operation(summary = "取消入库单")
    @Parameter(name = "id", description = "入库单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:inbound:delete')")
    public CommonResult<Boolean> cancelInbound(@RequestParam("id") Long id) {
        inboundService.cancelInbound(id);
        return success(true);
    }

}

