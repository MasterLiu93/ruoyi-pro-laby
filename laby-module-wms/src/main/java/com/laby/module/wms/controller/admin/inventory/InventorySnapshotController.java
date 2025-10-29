package com.laby.module.wms.controller.admin.inventory;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.inventory.vo.snapshot.InventorySnapshotPageReqVO;
import com.laby.module.wms.controller.admin.inventory.vo.snapshot.InventorySnapshotRespVO;
import com.laby.module.wms.service.inventory.InventorySnapshotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.List;

import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * 库存快照 Controller
 * 
 * 功能说明：
 * - 提供库存快照的查询API
 * - 支持库存趋势分析
 * - 用于历史库存查询
 * 
 * 接口列表：
 * 1. GET /page - 获取库存快照分页列表
 * 2. GET /trend - 获取库存趋势数据
 * 
 * 权限控制：
 * - wms:inventory-snapshot:query - 查询权限
 *
 * @author laby
 */
@Tag(name = "管理后台 - 库存快照")
@RestController
@RequestMapping("/wms/inventory-snapshot")
@Validated
public class InventorySnapshotController {

    @Resource
    private InventorySnapshotService inventorySnapshotService;

    /**
     * 获得库存快照分页列表
     * 
     * 接口说明：
     * - 查询库存快照数据
     * - 支持按日期范围、仓库、商品过滤
     * - 返回关联的仓库名、商品名
     * 
     * @param pageReqVO 分页查询条件
     * @return 库存快照分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "获得库存快照分页列表")
    @PreAuthorize("@ss.hasPermission('wms:inventory-snapshot:query')")
    public CommonResult<PageResult<InventorySnapshotRespVO>> getInventorySnapshotPage(@Valid InventorySnapshotPageReqVO pageReqVO) {
        PageResult<InventorySnapshotRespVO> pageResult = inventorySnapshotService.getInventorySnapshotPage(pageReqVO);
        return success(pageResult);
    }

    /**
     * 获得库存趋势数据（不分页）
     * 
     * 接口说明：
     * - 用于生成库存趋势图表
     * - 返回指定时间范围内的所有快照数据
     * - 按快照日期升序排列
     * 
     * @param reqVO 查询条件（必须包含日期范围）
     * @return 库存趋势数据列表
     */
    @GetMapping("/trend")
    @Operation(summary = "获得库存趋势数据")
    @PreAuthorize("@ss.hasPermission('wms:inventory-snapshot:query')")
    public CommonResult<List<InventorySnapshotRespVO>> getInventoryTrendList(@Valid InventorySnapshotPageReqVO reqVO) {
        List<InventorySnapshotRespVO> list = inventorySnapshotService.getInventoryTrendList(reqVO);
        return success(list);
    }

}

