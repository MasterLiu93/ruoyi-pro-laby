package com.laby.module.wms.service.inventory;

import cn.hutool.core.collection.CollUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.module.wms.controller.admin.inventory.vo.log.InventoryLogPageReqVO;
import com.laby.module.wms.controller.admin.inventory.vo.log.InventoryLogRespVO;
import com.laby.module.wms.convert.inventory.InventoryLogConvert;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.dal.dataobject.inventory.InventoryLogDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseLocationDO;
import com.laby.module.wms.dal.mysql.inventory.InventoryLogMapper;
import com.laby.module.wms.service.goods.GoodsService;
import com.laby.module.wms.service.warehouse.WarehouseLocationService;
import com.laby.module.wms.service.warehouse.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 库存流水 Service 实现类
 * 
 * 功能说明：
 * - 实现库存流水的查询功能
 * - 提供关联数据查询和填充
 * - 支持多维度统计分析
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class InventoryLogServiceImpl implements InventoryLogService {

    @Resource
    private InventoryLogMapper inventoryLogMapper;

    @Resource
    private WarehouseService warehouseService;

    @Resource
    private GoodsService goodsService;

    @Resource
    private WarehouseLocationService warehouseLocationService;

    /**
     * 获得库存流水分页列表
     * 
     * 实现步骤：
     * 1. 查询分页数据（Mapper 返回 DO）
     * 2. 提取关联ID（仓库ID、商品ID、库位ID）
     * 3. 批量查询关联数据
     * 4. 转换 VO 并填充关联字段
     * 
     * @param pageReqVO 分页查询条件
     * @return 分页列表（已填充关联字段）
     */
    @Override
    public PageResult<InventoryLogRespVO> getInventoryLogPage(InventoryLogPageReqVO pageReqVO) {
        // 1. 查询分页数据
        PageResult<InventoryLogDO> pageResult = inventoryLogMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }

        // 2. 提取关联ID
        List<Long> warehouseIds = CollectionUtils.convertList(pageResult.getList(), InventoryLogDO::getWarehouseId);
        List<Long> goodsIds = CollectionUtils.convertList(pageResult.getList(), InventoryLogDO::getGoodsId);
        List<Long> locationIds = CollectionUtils.convertList(pageResult.getList(), InventoryLogDO::getLocationId);

        // 3. 批量查询关联数据
        Map<Long, WarehouseDO> warehouseMap = warehouseService.getWarehouseMap(warehouseIds);
        Map<Long, GoodsDO> goodsMap = goodsService.getGoodsMap(goodsIds);
        Map<Long, WarehouseLocationDO> locationMap = warehouseLocationService.getWarehouseLocationMap(locationIds);

        // 4. 转换 VO 并填充关联字段
        return InventoryLogConvert.INSTANCE.convertPage(pageResult, warehouseMap, goodsMap, locationMap);
    }

}

