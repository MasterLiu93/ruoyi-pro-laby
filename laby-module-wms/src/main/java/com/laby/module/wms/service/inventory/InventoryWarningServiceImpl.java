package com.laby.module.wms.service.inventory;

import cn.hutool.core.collection.CollUtil;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.module.wms.controller.admin.inventory.vo.warning.InventoryWarningRespVO;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.dal.dataobject.inventory.InventoryDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.mysql.inventory.InventoryMapper;
import com.laby.module.wms.service.goods.GoodsService;
import com.laby.module.wms.service.warehouse.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 库存预警 Service 实现类
 * 
 * 功能说明：
 * - 实现库存预警功能
 * - 检测低库存和即将过期商品
 * - 提供预警数据查询
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class InventoryWarningServiceImpl implements InventoryWarningService {

    @Resource
    private InventoryMapper inventoryMapper;

    @Resource
    private WarehouseService warehouseService;

    @Resource
    private GoodsService goodsService;

    /**
     * 获取低库存预警列表
     * 
     * 实现步骤：
     * 1. 查询所有库存数据
     * 2. 批量获取商品信息（含安全库存）
     * 3. 过滤低库存商品（可用数量 < 安全库存）
     * 4. 填充关联字段
     * 
     * @return 低库存预警列表
     */
    @Override
    public List<InventoryWarningRespVO> getLowStockWarnings() {
        // 1. 查询所有库存
        List<InventoryDO> inventoryList = inventoryMapper.selectList();
        if (CollUtil.isEmpty(inventoryList)) {
            return List.of();
        }

        // 2. 按仓库和商品分组汇总
        Map<String, BigDecimal> quantityMap = new java.util.HashMap<>();
        Map<String, BigDecimal> lockQuantityMap = new java.util.HashMap<>();
        
        for (InventoryDO inventory : inventoryList) {
            String key = inventory.getWarehouseId() + "_" + inventory.getGoodsId();
            BigDecimal currentQty = quantityMap.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal currentLockQty = lockQuantityMap.getOrDefault(key, BigDecimal.ZERO);
            
            quantityMap.put(key, currentQty.add(inventory.getQuantity() != null ? inventory.getQuantity() : BigDecimal.ZERO));
            lockQuantityMap.put(key, currentLockQty.add(inventory.getLockQuantity() != null ? inventory.getLockQuantity() : BigDecimal.ZERO));
        }

        // 3. 获取商品和仓库信息
        List<Long> goodsIds = CollectionUtils.convertList(inventoryList, InventoryDO::getGoodsId);
        List<Long> warehouseIds = CollectionUtils.convertList(inventoryList, InventoryDO::getWarehouseId);
        
        Map<Long, GoodsDO> goodsMap = goodsService.getGoodsMap(goodsIds);
        Map<Long, WarehouseDO> warehouseMap = warehouseService.getWarehouseMap(warehouseIds);

        // 4. 检查低库存
        List<InventoryWarningRespVO> warnings = new ArrayList<>();
        java.util.Set<String> processed = new java.util.HashSet<>();
        
        for (InventoryDO inventory : inventoryList) {
            String key = inventory.getWarehouseId() + "_" + inventory.getGoodsId();
            if (processed.contains(key)) {
                continue;
            }
            processed.add(key);
            
            GoodsDO goods = goodsMap.get(inventory.getGoodsId());
            if (goods == null || goods.getSafetyStock() == null || goods.getSafetyStock().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            
            BigDecimal totalQty = quantityMap.get(key);
            BigDecimal totalLockQty = lockQuantityMap.get(key);
            BigDecimal availableQty = totalQty.subtract(totalLockQty);
            
            // 可用数量 < 安全库存
            if (availableQty.compareTo(goods.getSafetyStock()) < 0) {
                InventoryWarningRespVO warning = new InventoryWarningRespVO();
                warning.setWarehouseId(inventory.getWarehouseId());
                warning.setGoodsId(inventory.getGoodsId());
                warning.setWarningType("LOW_STOCK");
                warning.setQuantity(totalQty);
                warning.setLockQuantity(totalLockQty);
                warning.setAvailableQuantity(availableQty);
                warning.setSafetyStock(goods.getSafetyStock());
                
                // 填充关联字段
                WarehouseDO warehouse = warehouseMap.get(inventory.getWarehouseId());
                if (warehouse != null) {
                    warning.setWarehouseName(warehouse.getWarehouseName());
                }
                warning.setGoodsName(goods.getGoodsName());
                warning.setSkuCode(goods.getSkuCode());
                
                warnings.add(warning);
            }
        }
        
        return warnings;
    }

    /**
     * 获取即将过期预警列表
     * 
     * 实现步骤：
     * 1. 查询有过期日期的库存
     * 2. 过滤距离过期日期 <= 7天的商品
     * 3. 填充关联字段
     * 
     * @return 即将过期预警列表
     */
    @Override
    public List<InventoryWarningRespVO> getExpiringWarnings() {
        // 1. 查询所有库存
        List<InventoryDO> inventoryList = inventoryMapper.selectList();
        if (CollUtil.isEmpty(inventoryList)) {
            return List.of();
        }

        // 2. 获取关联信息
        List<Long> goodsIds = CollectionUtils.convertList(inventoryList, InventoryDO::getGoodsId);
        List<Long> warehouseIds = CollectionUtils.convertList(inventoryList, InventoryDO::getWarehouseId);
        
        Map<Long, GoodsDO> goodsMap = goodsService.getGoodsMap(goodsIds);
        Map<Long, WarehouseDO> warehouseMap = warehouseService.getWarehouseMap(warehouseIds);

        // 3. 检查即将过期
        List<InventoryWarningRespVO> warnings = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (InventoryDO inventory : inventoryList) {
            if (inventory.getExpireDate() == null) {
                continue;
            }
            
            // 计算距离过期天数
            long daysToExpire = ChronoUnit.DAYS.between(today, inventory.getExpireDate());
            
            // 距离过期 <= 7天且未过期
            if (daysToExpire >= 0 && daysToExpire <= 7) {
                InventoryWarningRespVO warning = new InventoryWarningRespVO();
                warning.setWarehouseId(inventory.getWarehouseId());
                warning.setGoodsId(inventory.getGoodsId());
                warning.setBatchNo(inventory.getBatchNo());
                warning.setWarningType("EXPIRING");
                warning.setQuantity(inventory.getQuantity());
                warning.setLockQuantity(inventory.getLockQuantity());
                warning.setAvailableQuantity(
                    (inventory.getQuantity() != null ? inventory.getQuantity() : BigDecimal.ZERO)
                        .subtract(inventory.getLockQuantity() != null ? inventory.getLockQuantity() : BigDecimal.ZERO)
                );
                warning.setExpireDate(inventory.getExpireDate());
                warning.setDaysToExpire((int) daysToExpire);
                
                // 填充关联字段
                GoodsDO goods = goodsMap.get(inventory.getGoodsId());
                if (goods != null) {
                    warning.setGoodsName(goods.getGoodsName());
                    warning.setSkuCode(goods.getSkuCode());
                }
                
                WarehouseDO warehouse = warehouseMap.get(inventory.getWarehouseId());
                if (warehouse != null) {
                    warning.setWarehouseName(warehouse.getWarehouseName());
                }
                
                warnings.add(warning);
            }
        }
        
        return warnings;
    }

    /**
     * 获取所有预警列表
     * 
     * 实现步骤：
     * 1. 获取低库存预警
     * 2. 获取即将过期预警
     * 3. 合并结果
     * 
     * @return 所有预警列表
     */
    @Override
    public List<InventoryWarningRespVO> getAllWarnings() {
        List<InventoryWarningRespVO> allWarnings = new ArrayList<>();
        allWarnings.addAll(getLowStockWarnings());
        allWarnings.addAll(getExpiringWarnings());
        return allWarnings;
    }

}

