package com.laby.module.wms.controller.admin.warehouse;

import com.laby.framework.apilog.core.annotation.ApiAccessLog;
import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageParam;
import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.object.BeanUtils;
import com.laby.framework.excel.core.util.ExcelUtils;
import com.laby.module.wms.controller.admin.warehouse.vo.warehouse.WarehousePageReqVO;
import com.laby.module.wms.controller.admin.warehouse.vo.warehouse.WarehouseRespVO;
import com.laby.module.wms.controller.admin.warehouse.vo.warehouse.WarehouseSaveReqVO;
import com.laby.module.wms.controller.admin.warehouse.vo.warehouse.WarehouseSimpleRespVO;
import com.laby.module.wms.convert.warehouse.WarehouseConvert;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
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

import static com.laby.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * 仓库管理 Controller
 * 提供仓库信息的增删改查、批量删除、导出等功能
 * 
 * 功能说明：
 * - 仓库是WMS系统的基础数据，用于管理不同的存储地点
 * - 一个仓库可以包含多个库区
 * - 仓库信息包括编码、名称、类型、地址、联系方式等
 * - 支持多租户隔离
 * 
 * @author laby
 */
@Tag(name = "管理后台 - 仓库管理")
@RestController
@RequestMapping("/wms/warehouse")
@Validated
public class WarehouseController {

    @Resource
    private WarehouseService warehouseService;

    /**
     * 创建仓库
     * 
     * @param createReqVO 仓库创建信息，包括编码、名称、类型、地址等
     * @return 创建成功的仓库ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建仓库")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:create')")
    public CommonResult<Long> createWarehouse(@Valid @RequestBody WarehouseSaveReqVO createReqVO) {
        return success(warehouseService.createWarehouse(createReqVO));
    }

    /**
     * 更新仓库
     * 
     * @param updateReqVO 仓库更新信息，必须包含id
     * @return 是否更新成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新仓库")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:update')")
    public CommonResult<Boolean> updateWarehouse(@Valid @RequestBody WarehouseSaveReqVO updateReqVO) {
        warehouseService.updateWarehouse(updateReqVO);
        return success(true);
    }

    /**
     * 删除仓库
     * 
     * 注意事项：
     * - 删除前会校验仓库是否存在关联数据（库区）
     * - 逻辑删除，不会真正删除数据
     * 
     * @param id 仓库ID
     * @return 是否删除成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除仓库")
    @Parameter(name = "id", description = "仓库ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:delete')")
    public CommonResult<Boolean> deleteWarehouse(@RequestParam("id") Long id) {
        warehouseService.deleteWarehouse(id);
        return success(true);
    }

    /**
     * 批量删除仓库
     * 
     * 注意事项：
     * - 删除前会校验每个仓库是否存在关联数据
     * - 逻辑删除，不会真正删除数据
     * - 如果任何一个仓库删除失败，整个操作会回滚
     * 
     * @param ids 仓库ID列表
     * @return 是否删除成功
     */
    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除仓库")
    @Parameter(name = "ids", description = "仓库ID列表", required = true, example = "[1,2,3]")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:delete')")
    public CommonResult<Boolean> deleteWarehouseList(@RequestParam("ids") List<Long> ids) {
        warehouseService.deleteWarehouseList(ids);
        return success(true);
    }

    /**
     * 获取仓库详情
     * 
     * @param id 仓库ID
     * @return 仓库详细信息
     */
    @GetMapping("/get")
    @Operation(summary = "获得仓库详情")
    @Parameter(name = "id", description = "仓库ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:query')")
    public CommonResult<WarehouseRespVO> getWarehouse(@RequestParam("id") Long id) {
        WarehouseDO warehouse = warehouseService.getWarehouse(id);
        return success(WarehouseConvert.INSTANCE.convert(warehouse));
    }

    /**
     * 获取仓库分页列表
     * 
     * 支持的查询条件：
     * - 仓库编码（模糊查询）
     * - 仓库名称（模糊查询）
     * - 仓库类型（精确查询）
     * - 省份、城市（精确查询）
     * - 状态（精确查询）
     * - 创建时间范围
     * 
     * @param pageReqVO 分页查询参数
     * @return 仓库分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "获得仓库分页列表")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:query')")
    public CommonResult<PageResult<WarehouseRespVO>> getWarehousePage(@Valid WarehousePageReqVO pageReqVO) {
        PageResult<WarehouseDO> pageResult = warehouseService.getWarehousePage(pageReqVO);
        return success(WarehouseConvert.INSTANCE.convertPage(pageResult));
    }

    /**
     * 获取仓库精简列表
     * 
     * 说明：
     * - 只返回启用状态的仓库
     * - 只包含id、仓库编码、仓库名称三个字段
     * - 主要用于前端下拉框选择
     * - 不需要权限控制，所有用户都可以调用
     * 
     * @return 仓库精简列表
     */
    @GetMapping("/list-all-simple")
    @Operation(summary = "获得仓库精简信息列表", description = "只包含被开启的仓库，主要用于前端的下拉选项")
    public CommonResult<List<WarehouseSimpleRespVO>> getSimpleWarehouseList() {
        List<WarehouseDO> list = warehouseService.getEnableWarehouseList();
        return success(WarehouseConvert.INSTANCE.convertSimpleList(list));
    }

    /**
     * 导出仓库Excel
     * 
     * 说明：
     * - 根据查询条件导出仓库数据
     * - 导出所有符合条件的数据（不分页）
     * - 文件名：仓库.xls
     * - 会记录导出操作日志
     * 
     * @param pageReqVO 查询条件（分页参数会被忽略）
     * @param response HTTP响应对象
     * @throws IOException IO异常
     */
    @GetMapping("/export-excel")
    @Operation(summary = "导出仓库 Excel")
    @PreAuthorize("@ss.hasPermission('wms:warehouse:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWarehouseExcel(@Valid WarehousePageReqVO pageReqVO,
                                      HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<WarehouseDO> list = warehouseService.getWarehousePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "仓库.xls", "数据", WarehouseRespVO.class,
                WarehouseConvert.INSTANCE.convertList(list));
    }

}

