package com.laby.module.wms.controller.admin.warehouse;

import com.laby.framework.apilog.core.annotation.ApiAccessLog;
import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageParam;
import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.framework.excel.core.util.ExcelUtils;
import com.laby.module.wms.controller.admin.warehouse.vo.area.*;
import com.laby.module.wms.convert.warehouse.WarehouseAreaConvert;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseAreaDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.service.warehouse.WarehouseAreaService;
import com.laby.module.wms.service.warehouse.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.laby.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * 库区管理 Controller
 * 提供库区信息的增删改查、批量删除、导出等功能
 * 
 * 功能说明：
 * - 库区是仓库的子区域划分，一个仓库可以包含多个库区
 * - 库区类型包括：存储区、拣货区、暂存区、收货区、发货区
 * - 库区信息包括编码、名称、类型、楼层、面积等
 * - 每个库区属于一个仓库，支持仓库与库区的关联查询
 * - 支持多租户隔离
 * 
 * @author laby
 */
@Tag(name = "管理后台 - 库区管理")
@RestController
@RequestMapping("/wms/warehouse-area")
@Validated
public class WarehouseAreaController {

    @Resource
    private WarehouseAreaService warehouseAreaService;

    @Resource
    private WarehouseService warehouseService;

    /**
     * 创建库区
     * 
     * 注意事项：
     * - 创建前会校验仓库是否存在
     * - 同一仓库内的库区编码不能重复
     * 
     * @param createReqVO 库区创建信息，包括仓库ID、编码、名称、类型等
     * @return 创建成功的库区ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建库区")
    @PreAuthorize("@ss.hasPermission('wms:area:create')")
    public CommonResult<Long> createWarehouseArea(@Valid @RequestBody WarehouseAreaSaveReqVO createReqVO) {
        return success(warehouseAreaService.createWarehouseArea(createReqVO));
    }

    /**
     * 更新库区
     * 
     * 注意事项：
     * - 更新前会校验库区是否存在
     * - 同一仓库内的库区编码不能重复
     * 
     * @param updateReqVO 库区更新信息，必须包含id
     * @return 是否更新成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新库区")
    @PreAuthorize("@ss.hasPermission('wms:area:update')")
    public CommonResult<Boolean> updateWarehouseArea(@Valid @RequestBody WarehouseAreaSaveReqVO updateReqVO) {
        warehouseAreaService.updateWarehouseArea(updateReqVO);
        return success(true);
    }

    /**
     * 删除库区
     * 
     * 注意事项：
     * - 删除前会校验库区是否存在关联数据（库位）
     * - 逻辑删除，不会真正删除数据
     * 
     * @param id 库区ID
     * @return 是否删除成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除库区")
    @Parameter(name = "id", description = "库区ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:area:delete')")
    public CommonResult<Boolean> deleteWarehouseArea(@RequestParam("id") Long id) {
        warehouseAreaService.deleteWarehouseArea(id);
        return success(true);
    }

    /**
     * 批量删除库区
     * 
     * 注意事项：
     * - 删除前会校验每个库区是否存在关联数据
     * - 逻辑删除，不会真正删除数据
     * - 如果任何一个库区删除失败，整个操作会回滚
     * 
     * @param ids 库区ID列表
     * @return 是否删除成功
     */
    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除库区")
    @Parameter(name = "ids", description = "库区ID列表", required = true, example = "[1,2,3]")
    @PreAuthorize("@ss.hasPermission('wms:area:delete')")
    public CommonResult<Boolean> deleteWarehouseAreaList(@RequestParam("ids") List<Long> ids) {
        warehouseAreaService.deleteWarehouseAreaList(ids);
        return success(true);
    }

    /**
     * 获取库区详情
     * 
     * @param id 库区ID
     * @return 库区详细信息
     */
    @GetMapping("/get")
    @Operation(summary = "获得库区详情")
    @Parameter(name = "id", description = "库区ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:area:query')")
    public CommonResult<WarehouseAreaRespVO> getWarehouseArea(@RequestParam("id") Long id) {
        WarehouseAreaDO warehouseArea = warehouseAreaService.getWarehouseArea(id);
        return success(WarehouseAreaConvert.INSTANCE.convert(warehouseArea));
    }

    /**
     * 获取库区分页列表
     * 
     * 支持的查询条件：
     * - 仓库ID（精确查询）
     * - 库区编码（模糊查询）
     * - 库区名称（模糊查询）
     * - 库区类型（精确查询）
     * - 楼层（精确查询）
     * - 状态（精确查询）
     * - 创建时间范围
     * 
     * 数据关联：
     * - 自动查询并填充仓库名称（warehouseName）
     * - 使用批量查询优化性能
     * 
     * @param pageReqVO 分页查询参数
     * @return 库区分页数据（包含仓库名称）
     */
    @GetMapping("/page")
    @Operation(summary = "获得库区分页列表")
    @PreAuthorize("@ss.hasPermission('wms:area:query')")
    public CommonResult<PageResult<WarehouseAreaRespVO>> getWarehouseAreaPage(@Valid WarehouseAreaPageReqVO pageReqVO) {
        PageResult<WarehouseAreaDO> pageResult = warehouseAreaService.getWarehouseAreaPage(pageReqVO);
        // 查询关联的仓库信息
        Map<Long, WarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                CollectionUtils.convertSet(pageResult.getList(), WarehouseAreaDO::getWarehouseId));
        return success(WarehouseAreaConvert.INSTANCE.convertPage(pageResult, warehouseMap));
    }

    /**
     * 获取指定仓库的所有库区（用于下拉框联动）
     * 
     * 说明：
     * - 根据仓库ID查询该仓库下的所有库区
     * - 只返回启用状态的库区
     * - 只包含id、库区编码、库区名称三个字段
     * - 主要用于前端"仓库-库区"联动下拉框
     * - 例如：在库位管理页面，选择仓库后，自动加载该仓库的库区列表
     * 
     * @param warehouseId 仓库ID
     * @return 该仓库下的所有库区列表
     */
    @GetMapping("/list-by-warehouse-id")
    @Operation(summary = "获得指定仓库的所有库区", description = "用于前端下拉框联动，根据仓库ID获取库区列表")
    @Parameter(name = "warehouseId", description = "仓库ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:area:query')")
    public CommonResult<List<WarehouseAreaSimpleRespVO>> getWarehouseAreaListByWarehouseId(@RequestParam("warehouseId") Long warehouseId) {
        List<WarehouseAreaDO> list = warehouseAreaService.getWarehouseAreaListByWarehouseId(warehouseId);
        return success(WarehouseAreaConvert.INSTANCE.convertSimpleList(list));
    }

    /**
     * 导出库区Excel
     * 
     * 说明：
     * - 根据查询条件导出库区数据
     * - 导出所有符合条件的数据（不分页）
     * - 自动关联并导出仓库名称
     * - 文件名：库区.xls
     * - 会记录导出操作日志
     * 
     * @param pageReqVO 查询条件（分页参数会被忽略）
     * @param response HTTP响应对象
     * @throws IOException IO异常
     */
    @GetMapping("/export-excel")
    @Operation(summary = "导出库区 Excel")
    @PreAuthorize("@ss.hasPermission('wms:area:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWarehouseAreaExcel(@Valid WarehouseAreaPageReqVO pageReqVO,
                                          HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<WarehouseAreaDO> pageResult = warehouseAreaService.getWarehouseAreaPage(pageReqVO);
        // 查询关联的仓库信息
        Map<Long, WarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                CollectionUtils.convertSet(pageResult.getList(), WarehouseAreaDO::getWarehouseId));
        List<WarehouseAreaRespVO> list = WarehouseAreaConvert.INSTANCE.convertList(pageResult.getList(), warehouseMap);
        // 导出 Excel
        ExcelUtils.write(response, "库区.xls", "数据", WarehouseAreaRespVO.class, list);
    }

}

