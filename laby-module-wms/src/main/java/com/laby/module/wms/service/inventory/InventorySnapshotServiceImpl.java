package com.laby.module.wms.service.inventory;

import cn.hutool.core.collection.CollUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.module.wms.controller.admin.inventory.vo.snapshot.InventorySnapshotPageReqVO;
import com.laby.module.wms.controller.admin.inventory.vo.snapshot.InventorySnapshotRespVO;
import com.laby.module.wms.convert.inventory.InventorySnapshotConvert;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.dal.dataobject.inventory.InventorySnapshotDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.mysql.inventory.InventorySnapshotMapper;
import com.laby.module.wms.service.goods.GoodsService;
import com.laby.module.wms.service.warehouse.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 库存快照 Service 实现类
 * 
 * 功能说明：
 * - 实现库存快照的查询功能
 * - 提供关联数据查询和填充
 * - 支持库存趋势分析
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class InventorySnapshotServiceImpl implements InventorySnapshotService {

    @Resource
    private InventorySnapshotMapper inventorySnapshotMapper;

    @Resource
    private WarehouseService warehouseService;

    @Resource
    private GoodsService goodsService;

    /**
     * 获得库存快照分页列表
     * 
     * 实现步骤：
     * 1. 查询分页数据（Mapper 返回 DO）
     * 2. 提取关联ID（仓库ID、商品ID）
     * 3. 批量查询关联数据
     * 4. 转换 VO 并填充关联字段
     * 
     * @param pageReqVO 分页查询条件
     * @return 分页列表（已填充关联字段）
     */
    @Override
    public PageResult<InventorySnapshotRespVO> getInventorySnapshotPage(InventorySnapshotPageReqVO pageReqVO) {
        // 1. 查询分页数据
        PageResult<InventorySnapshotDO> pageResult = inventorySnapshotMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }

        // 2. 提取关联ID
        List<Long> warehouseIds = CollectionUtils.convertList(pageResult.getList(), InventorySnapshotDO::getWarehouseId);
        List<Long> goodsIds = CollectionUtils.convertList(pageResult.getList(), InventorySnapshotDO::getGoodsId);

        // 3. 批量查询关联数据
        Map<Long, WarehouseDO> warehouseMap = warehouseService.getWarehouseMap(warehouseIds);
        Map<Long, GoodsDO> goodsMap = goodsService.getGoodsMap(goodsIds);

        // 4. 转换 VO 并填充关联字段
        return InventorySnapshotConvert.INSTANCE.convertPage(pageResult, warehouseMap, goodsMap);
    }

    /**
     * 获得库存趋势数据（不分页）
     * 
     * 实现步骤：
     * 1. 查询趋势数据（Mapper 返回 DO 列表）
     * 2. 提取关联ID（仓库ID、商品ID）
     * 3. 批量查询关联数据
     * 4. 转换 VO 并填充关联字段
     * 
     * @param reqVO 查询条件
     * @return 趋势数据列表（已填充关联字段）
     */
    @Override
    public List<InventorySnapshotRespVO> getInventoryTrendList(InventorySnapshotPageReqVO reqVO) {
        // 1. 查询趋势数据
        List<InventorySnapshotDO> list = inventorySnapshotMapper.selectTrendList(reqVO);
        if (CollUtil.isEmpty(list)) {
            return List.of();
        }

        // 2. 提取关联ID
        List<Long> warehouseIds = CollectionUtils.convertList(list, InventorySnapshotDO::getWarehouseId);
        List<Long> goodsIds = CollectionUtils.convertList(list, InventorySnapshotDO::getGoodsId);

        // 3. 批量查询关联数据
        Map<Long, WarehouseDO> warehouseMap = warehouseService.getWarehouseMap(warehouseIds);
        Map<Long, GoodsDO> goodsMap = goodsService.getGoodsMap(goodsIds);

        // 4. 转换 VO 并填充关联字段
        List<InventorySnapshotRespVO> result = InventorySnapshotConvert.INSTANCE.convertList(list);
        result.forEach(vo -> {
            // 填充仓库名称
            if (vo.getWarehouseId() != null) {
                WarehouseDO warehouse = warehouseMap.get(vo.getWarehouseId());
                if (warehouse != null) {
                    vo.setWarehouseName(warehouse.getWarehouseName());
                }
            }
            
            // 填充商品信息
            if (vo.getGoodsId() != null) {
                GoodsDO goods = goodsMap.get(vo.getGoodsId());
                if (goods != null) {
                    vo.setGoodsName(goods.getGoodsName());
                    vo.setSkuCode(goods.getSkuCode());
                }
            }
        });

        return result;
    }

}

