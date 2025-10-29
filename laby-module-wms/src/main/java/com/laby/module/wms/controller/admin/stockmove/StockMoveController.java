package com.laby.module.wms.controller.admin.stockmove;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.stockmove.vo.StockMovePageReqVO;
import com.laby.module.wms.controller.admin.stockmove.vo.StockMoveRespVO;
import com.laby.module.wms.controller.admin.stockmove.vo.StockMoveSaveReqVO;
import com.laby.module.wms.service.stockmove.StockMoveService;
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
 * 移库管理 Controller
 *
 * @author laby
 */
@Tag(name = "管理后台 - 移库管理")
@RestController
@RequestMapping("/wms/stock-move")
@Validated
@Slf4j
public class StockMoveController {

    @Resource
    private StockMoveService stockMoveService;

    /**
     * 创建移库单
     *
     * @param createReqVO 创建信息
     * @return 移库单ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建移库单")
    @PreAuthorize("@ss.hasPermission('wms:stock-move:create')")
    public CommonResult<Long> createStockMove(@Valid @RequestBody StockMoveSaveReqVO createReqVO) {
        return success(stockMoveService.createStockMove(createReqVO));
    }

    /**
     * 更新移库单
     *
     * @param updateReqVO 更新信息
     * @return 是否成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新移库单")
    @PreAuthorize("@ss.hasPermission('wms:stock-move:update')")
    public CommonResult<Boolean> updateStockMove(@Valid @RequestBody StockMoveSaveReqVO updateReqVO) {
        stockMoveService.updateStockMove(updateReqVO);
        return success(true);
    }

    /**
     * 删除移库单
     *
     * @param id 移库单ID
     * @return 是否成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除移库单")
    @Parameter(name = "id", description = "移库单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:stock-move:delete')")
    public CommonResult<Boolean> deleteStockMove(@RequestParam("id") Long id) {
        stockMoveService.deleteStockMove(id);
        return success(true);
    }

    /**
     * 获得移库单
     *
     * @param id 移库单ID
     * @return 移库单
     */
    @GetMapping("/get")
    @Operation(summary = "获得移库单")
    @Parameter(name = "id", description = "移库单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:stock-move:query')")
    public CommonResult<StockMoveRespVO> getStockMove(@RequestParam("id") Long id) {
        return success(stockMoveService.getStockMove(id));
    }

    /**
     * 获得移库单分页
     *
     * @param pageReqVO 分页查询
     * @return 移库单分页
     */
    @GetMapping("/page")
    @Operation(summary = "获得移库单分页")
    @PreAuthorize("@ss.hasPermission('wms:stock-move:query')")
    public CommonResult<PageResult<StockMoveRespVO>> getStockMovePage(@Valid StockMovePageReqVO pageReqVO) {
        return success(stockMoveService.getStockMovePage(pageReqVO));
    }

    /**
     * 执行移库
     *
     * @param id 移库单ID
     * @return 是否成功
     */
    @PutMapping("/execute")
    @Operation(summary = "执行移库")
    @Parameter(name = "id", description = "移库单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:stock-move:execute')")
    public CommonResult<Boolean> executeStockMove(@RequestParam("id") Long id) {
        stockMoveService.executeStockMove(id);
        return success(true);
    }

    /**
     * 完成移库
     *
     * @param id 移库单ID
     * @return 是否成功
     */
    @PutMapping("/complete")
    @Operation(summary = "完成移库")
    @Parameter(name = "id", description = "移库单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:stock-move:complete')")
    public CommonResult<Boolean> completeStockMove(@RequestParam("id") Long id) {
        stockMoveService.completeStockMove(id);
        return success(true);
    }

    /**
     * 取消移库
     *
     * @param id 移库单ID
     * @return 是否成功
     */
    @PutMapping("/cancel")
    @Operation(summary = "取消移库")
    @Parameter(name = "id", description = "移库单ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:stock-move:cancel')")
    public CommonResult<Boolean> cancelStockMove(@RequestParam("id") Long id) {
        stockMoveService.cancelStockMove(id);
        return success(true);
    }

}

