package com.laby.module.wms.controller.admin.stocktaking;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPageReqVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingRespVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingSaveReqVO;
import com.laby.module.wms.service.stocktaking.StockTakingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.math.BigDecimal;

import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * 盘点单管理 Controller
 *
 * @author laby
 */
@Tag(name = "管理后台 - 盘点单管理")
@RestController
@RequestMapping("/wms/stock-taking")
@Validated
@Slf4j
public class StockTakingController {

    @Resource
    private StockTakingService stockTakingService;

    /**
     * 创建盘点单
     */
    @PostMapping("/create")
    @Operation(summary = "创建盘点单")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking:create')")
    public CommonResult<Long> createStockTaking(@Valid @RequestBody StockTakingSaveReqVO createReqVO) {
        return success(stockTakingService.createStockTaking(createReqVO));
    }

    /**
     * 更新盘点单
     */
    @PutMapping("/update")
    @Operation(summary = "更新盘点单")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking:update')")
    public CommonResult<Boolean> updateStockTaking(@Valid @RequestBody StockTakingSaveReqVO updateReqVO) {
        stockTakingService.updateStockTaking(updateReqVO);
        return success(true);
    }

    /**
     * 删除盘点单
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除盘点单")
    @Parameter(name = "id", description = "盘点单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking:delete')")
    public CommonResult<Boolean> deleteStockTaking(@RequestParam("id") Long id) {
        stockTakingService.deleteStockTaking(id);
        return success(true);
    }

    /**
     * 获得盘点单
     */
    @GetMapping("/get")
    @Operation(summary = "获得盘点单")
    @Parameter(name = "id", description = "盘点单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking:query')")
    public CommonResult<StockTakingRespVO> getStockTaking(@RequestParam("id") Long id) {
        return success(stockTakingService.getStockTaking(id));
    }

    /**
     * 获得盘点单分页
     */
    @GetMapping("/page")
    @Operation(summary = "获得盘点单分页")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking:query')")
    public CommonResult<PageResult<StockTakingRespVO>> getStockTakingPage(@Valid StockTakingPageReqVO pageReqVO) {
        return success(stockTakingService.getStockTakingPage(pageReqVO));
    }

    /**
     * 提交盘点
     */
    @PutMapping("/submit")
    @Operation(summary = "提交盘点")
    public CommonResult<Boolean> submitStockTaking(
            @RequestParam("id") Long id,
            @RequestParam("actualQuantity") BigDecimal actualQuantity,
            @RequestParam(value = "diffReason", required = false) String diffReason) {
        stockTakingService.submitStockTaking(id, actualQuantity, diffReason);
        return success(true);
    }

    /**
     * 复核盘点
     */
    @PutMapping("/review")
    @Operation(summary = "复核盘点")
    @Parameter(name = "id", description = "盘点单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking:review')")
    public CommonResult<Boolean> reviewStockTaking(@RequestParam("id") Long id) {
        stockTakingService.reviewStockTaking(id);
        return success(true);
    }

    /**
     * 调整库存
     */
    @PutMapping("/adjust")
    @Operation(summary = "调整库存")
    @Parameter(name = "id", description = "盘点单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:stock-taking:adjust')")
    public CommonResult<Boolean> adjustStockTaking(@RequestParam("id") Long id) {
        stockTakingService.adjustStockTaking(id);
        return success(true);
    }

}

