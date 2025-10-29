package com.laby.module.wms.controller.admin.report;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.report.vo.InventoryReportReqVO;
import com.laby.module.wms.controller.admin.report.vo.InventoryReportRespVO;
import com.laby.module.wms.controller.admin.report.vo.InventoryReportSummaryVO;
import com.laby.module.wms.service.report.InventoryReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * 库存报表 Controller
 *
 * @author laby
 */
@Tag(name = "管理后台 - 库存报表")
@RestController
@RequestMapping("/wms/inventory-report")
@Validated
@Slf4j
public class InventoryReportController {

    @Resource
    private InventoryReportService inventoryReportService;

    /**
     * 获得库存报表分页
     */
    @GetMapping("/page")
    @Operation(summary = "获得库存报表分页")
    @PreAuthorize("@ss.hasPermission('wms:inventory-report:query')")
    public CommonResult<PageResult<InventoryReportRespVO>> getInventoryReportPage(@Valid InventoryReportReqVO reqVO) {
        return success(inventoryReportService.getInventoryReportPage(reqVO));
    }

    /**
     * 获得库存报表汇总
     */
    @GetMapping("/summary")
    @Operation(summary = "获得库存报表汇总")
    @PreAuthorize("@ss.hasPermission('wms:inventory-report:query')")
    public CommonResult<InventoryReportSummaryVO> getInventoryReportSummary(@Valid InventoryReportReqVO reqVO) {
        return success(inventoryReportService.getInventoryReportSummary(reqVO));
    }

}

