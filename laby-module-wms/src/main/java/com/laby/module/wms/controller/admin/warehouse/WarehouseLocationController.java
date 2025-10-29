package com.laby.module.wms.controller.admin.warehouse;

import com.laby.framework.apilog.core.annotation.ApiAccessLog;
import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageParam;
import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.framework.excel.core.util.ExcelUtils;
import com.laby.module.wms.controller.admin.warehouse.vo.location.*;
import com.laby.module.wms.convert.warehouse.WarehouseLocationConvert;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseAreaDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseLocationDO;
import com.laby.module.wms.service.warehouse.WarehouseAreaService;
import com.laby.module.wms.service.warehouse.WarehouseLocationService;
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
 * 库位管理 Controller
 * 提供库位信息的增删改查、批量删除、导出等功能
 * 
 * 功能说明：
 * - 库位是库区的最小存储单元，是实际存储商品的地方
 * - 一个库区可以包含多个库位
 * - 库位采用三维坐标定位：排号（rowNo）、列号（columnNo）、层号（layerNo）
 * - 库位类型包括：普通库位、临时库位、残次品库位、冷冻库位
 * - 每个库位有容量和承重限制
 * - 库位状态：禁用、空闲、占用、锁定
 * - 支持仓库→库区→库位的三级关联查询
 * - 支持多租户隔离
 * 
 * @author laby
 */
@Tag(name = "管理后台 - 库位管理")
@RestController
@RequestMapping("/wms/warehouse-location")
@Validated
public class WarehouseLocationController {

    @Resource
    private WarehouseLocationService warehouseLocationService;

    @Resource
    private WarehouseService warehouseService;

    @Resource
    private WarehouseAreaService warehouseAreaService;

    /**
     * 创建库位
     * 
     * 注意事项：
     * - 创建前会校验仓库和库区是否存在
     * - 库位编码必须全局唯一
     * - 同一库区内的排-列-层组合不能重复
     * 
     * @param createReqVO 库位创建信息，包括仓库ID、库区ID、编码、类型、坐标等
     * @return 创建成功的库位ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建库位")
    @PreAuthorize("@ss.hasPermission('wms:location:create')")
    public CommonResult<Long> createWarehouseLocation(@Valid @RequestBody WarehouseLocationSaveReqVO createReqVO) {
        return success(warehouseLocationService.createWarehouseLocation(createReqVO));
    }

    /**
     * 更新库位
     * 
     * 注意事项：
     * - 更新前会校验库位是否存在
     * - 库位编码必须全局唯一
     * - 如果库位已有库存，某些字段（如容量、承重）可能受限
     * 
     * @param updateReqVO 库位更新信息，必须包含id
     * @return 是否更新成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新库位")
    @PreAuthorize("@ss.hasPermission('wms:location:update')")
    public CommonResult<Boolean> updateWarehouseLocation(@Valid @RequestBody WarehouseLocationSaveReqVO updateReqVO) {
        warehouseLocationService.updateWarehouseLocation(updateReqVO);
        return success(true);
    }

    /**
     * 删除库位
     * 
     * 注意事项：
     * - 删除前会校验库位是否有库存
     * - 有库存的库位不能删除
     * - 逻辑删除，不会真正删除数据
     * 
     * @param id 库位ID
     * @return 是否删除成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除库位")
    @Parameter(name = "id", description = "库位ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:location:delete')")
    public CommonResult<Boolean> deleteWarehouseLocation(@RequestParam("id") Long id) {
        warehouseLocationService.deleteWarehouseLocation(id);
        return success(true);
    }

    /**
     * 批量删除库位
     * 
     * 注意事项：
     * - 删除前会校验每个库位是否有库存
     * - 有库存的库位不能删除
     * - 逻辑删除，不会真正删除数据
     * - 如果任何一个库位删除失败，整个操作会回滚
     * 
     * @param ids 库位ID列表
     * @return 是否删除成功
     */
    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除库位")
    @Parameter(name = "ids", description = "库位ID列表", required = true, example = "[1,2,3]")
    @PreAuthorize("@ss.hasPermission('wms:location:delete')")
    public CommonResult<Boolean> deleteWarehouseLocationList(@RequestParam("ids") List<Long> ids) {
        warehouseLocationService.deleteWarehouseLocationList(ids);
        return success(true);
    }

    /**
     * 获取库位详情
     * 
     * @param id 库位ID
     * @return 库位详细信息
     */
    @GetMapping("/get")
    @Operation(summary = "获得库位详情")
    @Parameter(name = "id", description = "库位ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:location:query')")
    public CommonResult<WarehouseLocationRespVO> getWarehouseLocation(@RequestParam("id") Long id) {
        WarehouseLocationDO warehouseLocation = warehouseLocationService.getWarehouseLocation(id);
        return success(WarehouseLocationConvert.INSTANCE.convert(warehouseLocation));
    }

    /**
     * 获取库位分页列表
     * 
     * 支持的查询条件：
     * - 仓库ID（精确查询）
     * - 库区ID（精确查询）
     * - 库位编码（模糊查询）
     * - 库位类型（精确查询）
     * - 排号、列号、层号（精确查询）
     * - 状态（精确查询：空闲、占用、锁定、禁用）
     * - 创建时间范围
     * 
     * 数据关联：
     * - 自动查询并填充仓库名称（warehouseName）
     * - 自动查询并填充库区名称（areaName）
     * - 使用批量查询优化性能
     * 
     * @param pageReqVO 分页查询参数
     * @return 库位分页数据（包含仓库名称、库区名称）
     */
    @GetMapping("/page")
    @Operation(summary = "获得库位分页列表")
    @PreAuthorize("@ss.hasPermission('wms:location:query')")
    public CommonResult<PageResult<WarehouseLocationRespVO>> getWarehouseLocationPage(@Valid WarehouseLocationPageReqVO pageReqVO) {
        PageResult<WarehouseLocationDO> pageResult = warehouseLocationService.getWarehouseLocationPage(pageReqVO);
        // 查询关联的仓库和库区信息
        Map<Long, WarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                CollectionUtils.convertSet(pageResult.getList(), WarehouseLocationDO::getWarehouseId));
        Map<Long, WarehouseAreaDO> areaMap = warehouseAreaService.getWarehouseAreaMap(
                CollectionUtils.convertSet(pageResult.getList(), WarehouseLocationDO::getAreaId));
        return success(WarehouseLocationConvert.INSTANCE.convertPage(pageResult, warehouseMap, areaMap));
    }

    /**
     * 获取指定库区的所有库位（用于下拉框联动）
     * 
     * 说明：
     * - 根据库区ID查询该库区下的所有库位
     * - 只返回启用状态的库位
     * - 只包含id、库位编码、库位类型三个字段
     * - 主要用于前端"仓库-库区-库位"三级联动下拉框
     * - 例如：在库存管理页面，选择库区后，自动加载该库区的库位列表
     * 
     * @param areaId 库区ID
     * @return 该库区下的所有库位列表
     */
    @GetMapping("/list-by-area-id")
    @Operation(summary = "获得指定库区的所有库位", description = "用于前端下拉框联动，根据库区ID获取库位列表")
    @Parameter(name = "areaId", description = "库区ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:location:query')")
    public CommonResult<List<WarehouseLocationSimpleRespVO>> getWarehouseLocationListByAreaId(@RequestParam("areaId") Long areaId) {
        List<WarehouseLocationDO> list = warehouseLocationService.getWarehouseLocationListByAreaId(areaId);
        return success(WarehouseLocationConvert.INSTANCE.convertSimpleList(list));
    }

    /**
     * 导出库位Excel
     * 
     * 说明：
     * - 根据查询条件导出库位数据
     * - 导出所有符合条件的数据（不分页）
     * - 自动关联并导出仓库名称、库区名称
     * - 文件名：库位.xls
     * - 会记录导出操作日志
     * 
     * @param pageReqVO 查询条件（分页参数会被忽略）
     * @param response HTTP响应对象
     * @throws IOException IO异常
     */
    @GetMapping("/export-excel")
    @Operation(summary = "导出库位 Excel")
    @PreAuthorize("@ss.hasPermission('wms:location:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWarehouseLocationExcel(@Valid WarehouseLocationPageReqVO pageReqVO,
                                              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<WarehouseLocationDO> pageResult = warehouseLocationService.getWarehouseLocationPage(pageReqVO);
        // 查询关联的仓库和库区信息
        Map<Long, WarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                CollectionUtils.convertSet(pageResult.getList(), WarehouseLocationDO::getWarehouseId));
        Map<Long, WarehouseAreaDO> areaMap = warehouseAreaService.getWarehouseAreaMap(
                CollectionUtils.convertSet(pageResult.getList(), WarehouseLocationDO::getAreaId));
        List<WarehouseLocationRespVO> list = WarehouseLocationConvert.INSTANCE.convertList(pageResult.getList(), warehouseMap, areaMap);
        // 导出 Excel
        ExcelUtils.write(response, "库位.xls", "数据", WarehouseLocationRespVO.class, list);
    }

}

