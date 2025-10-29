package com.laby.module.wms.controller.admin.inventory;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.module.wms.controller.admin.inventory.vo.warning.InventoryWarningRespVO;
import com.laby.module.wms.service.inventory.InventoryWarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import java.util.List;

import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * 库存预警 Controller
 * 
 * 功能说明：
 * - 提供库存预警的查询API
 * - 支持低库存预警和即将过期预警
 * - 用于库存预警管理
 * 
 * 接口列表：
 * 1. GET /low-stock - 获取低库存预警列表
 * 2. GET /expiring - 获取即将过期预警列表
 * 3. GET /all - 获取所有预警列表
 * 
 * 权限控制：
 * - wms:inventory-warning:query - 查询权限
 *
 * @author laby
 */
@Tag(name = "管理后台 - 库存预警")
@RestController
@RequestMapping("/wms/inventory-warning")
@Validated
public class InventoryWarningController {

    @Resource
    private InventoryWarningService inventoryWarningService;

    /**
     * 获取低库存预警列表
     * 
     * 接口说明：
     * - 查询可用数量低于安全库存的商品
     * - 按仓库维度统计
     * - 返回关联的仓库名、商品名
     * 
     * @return 低库存预警列表
     */
    @GetMapping("/low-stock")
    @Operation(summary = "获取低库存预警列表")
    @PreAuthorize("@ss.hasPermission('wms:inventory-warning:query')")
    public CommonResult<List<InventoryWarningRespVO>> getLowStockWarnings() {
        List<InventoryWarningRespVO> list = inventoryWarningService.getLowStockWarnings();
        return success(list);
    }

    /**
     * 获取即将过期预警列表
     * 
     * 接口说明：
     * - 查询距离过期日期 <= 7天的批次商品
     * - 按仓库和批次维度统计
     * - 返回关联的仓库名、商品名
     * 
     * @return 即将过期预警列表
     */
    @GetMapping("/expiring")
    @Operation(summary = "获取即将过期预警列表")
    @PreAuthorize("@ss.hasPermission('wms:inventory-warning:query')")
    public CommonResult<List<InventoryWarningRespVO>> getExpiringWarnings() {
        List<InventoryWarningRespVO> list = inventoryWarningService.getExpiringWarnings();
        return success(list);
    }

    /**
     * 获取所有预警列表
     * 
     * 接口说明：
     * - 合并低库存预警和即将过期预警
     * - 去除重复数据
     * - 按预警类型和商品分组
     * 
     * @return 所有预警列表
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有预警列表")
    @PreAuthorize("@ss.hasPermission('wms:inventory-warning:query')")
    public CommonResult<List<InventoryWarningRespVO>> getAllWarnings() {
        List<InventoryWarningRespVO> list = inventoryWarningService.getAllWarnings();
        return success(list);
    }

}

