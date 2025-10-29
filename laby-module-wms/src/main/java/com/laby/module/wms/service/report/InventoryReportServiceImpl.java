package com.laby.module.wms.service.report;

import cn.hutool.core.collection.CollUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.report.vo.InventoryReportReqVO;
import com.laby.module.wms.controller.admin.report.vo.InventoryReportRespVO;
import com.laby.module.wms.controller.admin.report.vo.InventoryReportSummaryVO;
import com.laby.module.wms.dal.dataobject.goods.GoodsCategoryDO;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.dal.dataobject.inventory.InventoryDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.mysql.goods.GoodsMapper;
import com.laby.module.wms.dal.mysql.inventory.InventoryMapper;
import com.laby.module.wms.dal.mysql.warehouse.WarehouseMapper;
import com.laby.module.wms.service.goods.GoodsCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayList;

import static com.laby.framework.common.util.collection.CollectionUtils.convertMap;
import static com.laby.framework.common.util.collection.CollectionUtils.convertSet;

/**
 * 库存报表 Service 实现类
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class InventoryReportServiceImpl implements InventoryReportService {

    @Resource
    private InventoryMapper inventoryMapper;

    @Resource
    private WarehouseMapper warehouseMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private GoodsCategoryService goodsCategoryService;

    @Override
    public PageResult<InventoryReportRespVO> getInventoryReportPage(InventoryReportReqVO reqVO) {
        // 1. 查询库存数据
        PageResult<InventoryDO> pageResult = inventoryMapper.selectPage(reqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }

        // 2. 提取关联ID
        Set<Long> warehouseIds = convertSet(pageResult.getList(), InventoryDO::getWarehouseId);
        Set<Long> goodsIds = convertSet(pageResult.getList(), InventoryDO::getGoodsId);

        // 3. 批量查询关联数据
        Map<Long, WarehouseDO> warehouseMap = convertMap(warehouseMapper.selectBatchIds(warehouseIds), WarehouseDO::getId);
        Map<Long, GoodsDO> goodsMap = convertMap(goodsMapper.selectBatchIds(goodsIds), GoodsDO::getId);
        
        Set<Long> categoryIds = goodsMap.values().stream()
                .map(GoodsDO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, GoodsCategoryDO> categoryDOMap = goodsCategoryService.getGoodsCategoryMap(new ArrayList<>(categoryIds));
        Map<Long, String> categoryMap = categoryDOMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getCategoryName()));

        // 4. 转换为VO并填充关联字段
        List<InventoryReportRespVO> resultList = pageResult.getList().stream().map(inventory -> {
            InventoryReportRespVO vo = new InventoryReportRespVO();
            vo.setWarehouseId(inventory.getWarehouseId());
            vo.setLocationId(inventory.getLocationId());
            vo.setLocationCode(""); // TODO: 需要关联查询库位表
            vo.setGoodsId(inventory.getGoodsId());
            vo.setBatchNo(inventory.getBatchNo());
            vo.setTotalQuantity(inventory.getQuantity());
            // 计算可用数量 = 总数量 - 锁定数量
            BigDecimal availableQty = inventory.getQuantity().subtract(inventory.getLockQuantity() != null ? inventory.getLockQuantity() : BigDecimal.ZERO);
            vo.setAvailableQuantity(availableQty);
            vo.setLockedQuantity(inventory.getLockQuantity() != null ? inventory.getLockQuantity() : BigDecimal.ZERO);

            // 填充仓库信息
            WarehouseDO warehouse = warehouseMap.get(inventory.getWarehouseId());
            if (warehouse != null) {
                vo.setWarehouseName(warehouse.getWarehouseName());
            }

            // 填充商品信息
            GoodsDO goods = goodsMap.get(inventory.getGoodsId());
            if (goods != null) {
                vo.setSkuCode(goods.getSkuCode());
                vo.setGoodsName(goods.getGoodsName());
                vo.setCategoryId(goods.getCategoryId());
                vo.setSafetyStock(goods.getSafetyStock() != null ? goods.getSafetyStock() : BigDecimal.ZERO);
                
                // 填充分类信息
                if (goods.getCategoryId() != null) {
                    vo.setCategoryName(categoryMap.get(goods.getCategoryId()));
                }

                // 判断是否低库存
                if (goods.getSafetyStock() != null && availableQty.compareTo(goods.getSafetyStock()) < 0) {
                    vo.setIsLowStock(true);
                    vo.setStockStatus("低库存");
                } else if (availableQty.compareTo(BigDecimal.ZERO) == 0) {
                    vo.setIsLowStock(true);
                    vo.setStockStatus("零库存");
                } else {
                    vo.setIsLowStock(false);
                    vo.setStockStatus("正常");
                }
            }

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(resultList, pageResult.getTotal());
    }

    @Override
    public InventoryReportSummaryVO getInventoryReportSummary(InventoryReportReqVO reqVO) {
        // 1. 查询所有库存数据（不分页）
        InventoryReportReqVO allReqVO = new InventoryReportReqVO();
        allReqVO.setWarehouseId(reqVO.getWarehouseId());
        allReqVO.setCategoryId(reqVO.getCategoryId());
        allReqVO.setGoodsName(reqVO.getGoodsName());
        allReqVO.setSkuCode(reqVO.getSkuCode());
        allReqVO.setPageNo(1);
        allReqVO.setPageSize(Integer.MAX_VALUE);
        
        PageResult<InventoryDO> allInventory = inventoryMapper.selectPage(allReqVO);

        // 2. 统计数据
        InventoryReportSummaryVO summary = new InventoryReportSummaryVO();
        
        if (CollUtil.isEmpty(allInventory.getList())) {
            summary.setTotalGoodsCount(0);
            summary.setTotalQuantity(BigDecimal.ZERO);
            summary.setAvailableQuantity(BigDecimal.ZERO);
            summary.setLockedQuantity(BigDecimal.ZERO);
            summary.setLowStockCount(0);
            summary.setZeroStockCount(0);
            return summary;
        }

        // 按商品ID分组统计
        Map<Long, List<InventoryDO>> goodsInventoryMap = allInventory.getList().stream()
                .collect(Collectors.groupingBy(InventoryDO::getGoodsId));

        summary.setTotalGoodsCount(goodsInventoryMap.size());

        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal availableQty = BigDecimal.ZERO;
        BigDecimal lockedQty = BigDecimal.ZERO;
        int lowStockCount = 0;
        int zeroStockCount = 0;

        // 查询商品信息（用于获取安全库存）
        Set<Long> goodsIds = goodsInventoryMap.keySet();
        Map<Long, GoodsDO> goodsMap = convertMap(goodsMapper.selectBatchIds(goodsIds), GoodsDO::getId);

        for (Map.Entry<Long, List<InventoryDO>> entry : goodsInventoryMap.entrySet()) {
            Long goodsId = entry.getKey();
            List<InventoryDO> inventories = entry.getValue();

            // 汇总该商品的所有库存
            BigDecimal goodsTotalQty = inventories.stream()
                    .map(InventoryDO::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal goodsLockedQty = inventories.stream()
                    .map(inv -> inv.getLockQuantity() != null ? inv.getLockQuantity() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal goodsAvailableQty = goodsTotalQty.subtract(goodsLockedQty);

            totalQty = totalQty.add(goodsTotalQty);
            availableQty = availableQty.add(goodsAvailableQty);
            lockedQty = lockedQty.add(goodsLockedQty);

            // 判断是否低库存或零库存
            GoodsDO goods = goodsMap.get(goodsId);
            if (goods != null) {
                if (goodsAvailableQty.compareTo(BigDecimal.ZERO) == 0) {
                    zeroStockCount++;
                } else if (goods.getSafetyStock() != null && goodsAvailableQty.compareTo(goods.getSafetyStock()) < 0) {
                    lowStockCount++;
                }
            }
        }

        summary.setTotalQuantity(totalQty);
        summary.setAvailableQuantity(availableQty);
        summary.setLockedQuantity(lockedQty);
        summary.setLowStockCount(lowStockCount);
        summary.setZeroStockCount(zeroStockCount);

        return summary;
    }

}

