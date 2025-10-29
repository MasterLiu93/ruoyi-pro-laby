package com.laby.module.wms.controller.admin.inventory;

import cn.hutool.core.collection.CollUtil;
import com.laby.framework.apilog.core.annotation.ApiAccessLog;
import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.module.wms.controller.admin.inventory.vo.InventoryPageReqVO;
import com.laby.module.wms.controller.admin.inventory.vo.InventoryRespVO;
import com.laby.module.wms.controller.admin.inventory.vo.InventorySaveReqVO;
import com.laby.module.wms.convert.inventory.InventoryConvert;
import com.laby.module.wms.dal.dataobject.inventory.InventoryDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseLocationDO;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.service.inventory.InventoryService;
import com.laby.module.wms.service.warehouse.WarehouseService;
import com.laby.module.wms.service.warehouse.WarehouseLocationService;
import com.laby.module.wms.service.goods.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.laby.framework.apilog.core.enums.OperateTypeEnum.*;
import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * 库存管理 Controller
 * 提供库存的增删改查功能
 * 
 * 功能说明：
 * - 库存用于记录商品在各仓库/库位的数量信息
 * - 支持批次号和序列号管理
 * - 支持库存锁定（预留库存）
 * - 可用数量 = 库存数量 - 锁定数量
 * - 支持多租户隔离
 * 
 * @author laby
 */
@Tag(name = "管理后台 - 库存管理")
@RestController
@RequestMapping("/wms/inventory")
@Validated
public class InventoryController {

    @Resource
    private InventoryService inventoryService;
    
    @Resource
    private WarehouseService warehouseService;
    
    @Resource
    private WarehouseLocationService warehouseLocationService;
    
    @Resource
    private GoodsService goodsService;

    /**
     * 创建库存
     * 
     * 说明：
     * - 手动录入初始库存
     * - 正常入库业务请使用入库单流程
     * 
     * 校验规则：
     * - 仓库、商品必须存在
     * - 库存数量和锁定数量必须合法
     * - 同一仓库+库位+商品+批次+序列号不能重复
     * 
     * @param createReqVO 创建信息
     * @return 库存ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建库存")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('wms:inventory:create')")
    public CommonResult<Long> createInventory(@Valid @RequestBody InventorySaveReqVO createReqVO) {
        return success(inventoryService.createInventory(createReqVO));
    }

    /**
     * 更新库存
     * 
     * 说明：
     * - 手动调整库存数量
     * - 盘点后调整库存
     * - 使用乐观锁防止并发问题
     * 
     * 校验规则：
     * - 库存必须存在
     * - 库存数量和锁定数量必须合法
     * 
     * @param updateReqVO 更新信息
     * @return 成功标识
     */
    @PutMapping("/update")
    @Operation(summary = "更新库存")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('wms:inventory:update')")
    public CommonResult<Boolean> updateInventory(@Valid @RequestBody InventorySaveReqVO updateReqVO) {
        inventoryService.updateInventory(updateReqVO);
        return success(true);
    }

    /**
     * 删除库存
     * 
     * 说明：
     * - 只有库存数量=0且锁定数量=0时才允许删除
     * - 逻辑删除，不物理删除
     * 
     * 校验规则：
     * - 库存必须存在
     * - 库存数量和锁定数量必须为0
     * 
     * @param id 库存ID
     * @return 成功标识
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除库存")
    @Parameter(name = "id", description = "库存ID", required = true)
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('wms:inventory:delete')")
    public CommonResult<Boolean> deleteInventory(@RequestParam("id") Long id) {
        inventoryService.deleteInventory(id);
        return success(true);
    }

    /**
     * 获得库存详情
     * 
     * @param id 库存ID
     * @return 库存详情（包含关联查询字段）
     */
    @GetMapping("/get")
    @Operation(summary = "获得库存详情")
    @Parameter(name = "id", description = "库存ID", required = true)
    @PreAuthorize("@ss.hasPermission('wms:inventory:query')")
    public CommonResult<InventoryRespVO> getInventory(@RequestParam("id") Long id) {
        InventoryDO inventory = inventoryService.getInventory(id);
        return success(InventoryConvert.INSTANCE.convert(inventory));
    }

    /**
     * 获得库存分页列表
     * 
     * 说明：
     * - 支持多条件搜索和分页
     * - 返回关联查询字段（仓库名、库位编码、商品名等）
     * 
     * 支持的查询条件：
     * - 仓库ID（精确查询，必填）
     * - 库位ID（精确查询）
     * - 商品ID/名称（精确/模糊查询）
     * - SKU编码（模糊查询）
     * - 批次号/序列号（精确查询）
     * - 状态（精确查询）
     * - 低库存预警
     * - 创建时间范围
     * 
     * @param pageReqVO 分页查询条件
     * @return 库存分页结果（包含关联字段）
     */
    @GetMapping("/page")
    @Operation(summary = "获得库存分页列表")
    @PreAuthorize("@ss.hasPermission('wms:inventory:query')")
    public CommonResult<PageResult<InventoryRespVO>> getInventoryPage(@Valid InventoryPageReqVO pageReqVO) {
        PageResult<InventoryDO> pageResult = inventoryService.getInventoryPage(pageReqVO);
        
        // 关联查询：仓库名、库位编码、商品名等
        List<Long> warehouseIds = CollectionUtils.convertList(pageResult.getList(), InventoryDO::getWarehouseId);
        List<Long> locationIds = CollectionUtils.convertList(pageResult.getList(), InventoryDO::getLocationId);
        List<Long> goodsIds = CollectionUtils.convertList(pageResult.getList(), InventoryDO::getGoodsId);
        
        // 批量查询关联数据
        Map<Long, WarehouseDO> warehouseMap = warehouseService.getWarehouseMap(warehouseIds);
        Map<Long, WarehouseLocationDO> locationMap = warehouseLocationService.getWarehouseLocationMap(locationIds);
        Map<Long, GoodsDO> goodsMap = goodsService.getGoodsMap(goodsIds);
        
        // 转换并填充关联字段
        PageResult<InventoryRespVO> result = InventoryConvert.INSTANCE.convertPage(pageResult, warehouseMap, locationMap, goodsMap);
        
        return success(result);
    }

}

