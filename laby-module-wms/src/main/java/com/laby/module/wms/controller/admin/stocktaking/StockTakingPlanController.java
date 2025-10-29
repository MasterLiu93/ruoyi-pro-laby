package com.laby.module.wms.controller.admin.stocktaking;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPlanPageReqVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPlanRespVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPlanSaveReqVO;
import com.laby.module.wms.service.stocktaking.StockTakingPlanService;
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
 * 盘点计划管理 Controller
 *
 * @author laby
 */
@Tag(name = "管理后台 - 盘点计划管理")
@RestController
@RequestMapping("/wms/stock-taking-plan")
@Validated
@Slf4j
public class StockTakingPlanController {

    @Resource
    private StockTakingPlanService stockTakingPlanService;

    /**
     * 创建盘点计划
     *
     * @param createReqVO 创建信息
     * @return 盘点计划ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建盘点计划")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking-plan:create')")
    public CommonResult<Long> createStockTakingPlan(@Valid @RequestBody StockTakingPlanSaveReqVO createReqVO) {
        return success(stockTakingPlanService.createStockTakingPlan(createReqVO));
    }

    /**
     * 更新盘点计划
     *
     * @param updateReqVO 更新信息
     * @return 是否成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新盘点计划")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking-plan:update')")
    public CommonResult<Boolean> updateStockTakingPlan(@Valid @RequestBody StockTakingPlanSaveReqVO updateReqVO) {
        stockTakingPlanService.updateStockTakingPlan(updateReqVO);
        return success(true);
    }

    /**
     * 删除盘点计划
     *
     * @param id 盘点计划ID
     * @return 是否成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除盘点计划")
    @Parameter(name = "id", description = "盘点计划ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking-plan:delete')")
    public CommonResult<Boolean> deleteStockTakingPlan(@RequestParam("id") Long id) {
        stockTakingPlanService.deleteStockTakingPlan(id);
        return success(true);
    }

    /**
     * 获得盘点计划
     *
     * @param id 盘点计划ID
     * @return 盘点计划
     */
    @GetMapping("/get")
    @Operation(summary = "获得盘点计划")
    @Parameter(name = "id", description = "盘点计划ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking-plan:query')")
    public CommonResult<StockTakingPlanRespVO> getStockTakingPlan(@RequestParam("id") Long id) {
        return success(stockTakingPlanService.getStockTakingPlan(id));
    }

    /**
     * 获得盘点计划分页
     *
     * @param pageReqVO 分页查询
     * @return 盘点计划分页
     */
    @GetMapping("/page")
    @Operation(summary = "获得盘点计划分页")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking-plan:query')")
    public CommonResult<PageResult<StockTakingPlanRespVO>> getStockTakingPlanPage(@Valid StockTakingPlanPageReqVO pageReqVO) {
        return success(stockTakingPlanService.getStockTakingPlanPage(pageReqVO));
    }

    /**
     * 审核盘点计划
     *
     * @param id 盘点计划ID
     * @return 是否成功
     */
    @PutMapping("/audit")
    @Operation(summary = "审核盘点计划")
    @Parameter(name = "id", description = "盘点计划ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking-plan:audit')")
    public CommonResult<Boolean> auditStockTakingPlan(@RequestParam("id") Long id) {
        stockTakingPlanService.auditStockTakingPlan(id);
        return success(true);
    }

    /**
     * 取消盘点计划
     *
     * @param id 盘点计划ID
     * @return 是否成功
     */
    @PutMapping("/cancel")
    @Operation(summary = "取消盘点计划")
    @Parameter(name = "id", description = "盘点计划ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking-plan:cancel')")
    public CommonResult<Boolean> cancelStockTakingPlan(@RequestParam("id") Long id) {
        stockTakingPlanService.cancelStockTakingPlan(id);
        return success(true);
    }

}

