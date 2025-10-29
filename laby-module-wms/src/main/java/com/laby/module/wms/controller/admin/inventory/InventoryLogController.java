package com.laby.module.wms.controller.admin.inventory;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.inventory.vo.log.InventoryLogPageReqVO;
import com.laby.module.wms.controller.admin.inventory.vo.log.InventoryLogRespVO;
import com.laby.module.wms.service.inventory.InventoryLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * 库存流水 Controller
 * 
 * 功能说明：
 * - 提供库存流水的查询API
 * - 支持多条件查询和分页
 * - 用于库存变动追溯和审计
 * 
 * 接口列表：
 * 1. GET /page - 获取库存流水分页列表
 * 
 * 权限控制：
 * - wms:inventory-log:query - 查询权限
 *
 * @author laby
 */
@Tag(name = "管理后台 - 库存流水")
@RestController
@RequestMapping("/wms/inventory-log")
@Validated
public class InventoryLogController {

    @Resource
    private InventoryLogService inventoryLogService;

    /**
     * 获得库存流水分页列表
     * 
     * 接口说明：
     * - 查询库存变动流水
     * - 支持按仓库、商品、批次、操作类型、业务类型过滤
     * - 支持按时间范围查询
     * - 返回关联的仓库名、商品名、库位编码
     * 
     * @param pageReqVO 分页查询条件
     * @return 库存流水分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "获得库存流水分页列表")
    @PreAuthorize("@ss.hasPermission('wms:inventory-log:query')")
    public CommonResult<PageResult<InventoryLogRespVO>> getInventoryLogPage(@Valid InventoryLogPageReqVO pageReqVO) {
        PageResult<InventoryLogRespVO> pageResult = inventoryLogService.getInventoryLogPage(pageReqVO);
        return success(pageResult);
    }

}

