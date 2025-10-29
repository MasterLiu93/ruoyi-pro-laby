package com.laby.module.wms.convert.inventory;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.inventory.vo.snapshot.InventorySnapshotRespVO;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.dal.dataobject.inventory.InventorySnapshotDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 库存快照 Convert
 * 
 * 功能说明：
 * - 使用 MapStruct 进行对象转换
 * - 处理 DO 和 VO 之间的转换
 * - 填充关联字段（仓库名、商品名）
 * - 计算可用数量
 *
 * @author laby
 */
@Mapper
public interface InventorySnapshotConvert {

    InventorySnapshotConvert INSTANCE = Mappers.getMapper(InventorySnapshotConvert.class);

    /**
     * DO -> RespVO（不含关联字段）
     */
    InventorySnapshotRespVO convert(InventorySnapshotDO bean);

    /**
     * DO List -> RespVO List（不含关联字段）
     */
    List<InventorySnapshotRespVO> convertList(List<InventorySnapshotDO> list);

    /**
     * PageResult<DO> -> PageResult<RespVO>（含关联字段）
     * 
     * 实现步骤：
     * 1. 转换分页数据
     * 2. 填充仓库名称
     * 3. 填充商品名称和SKU编码
     * 4. 计算可用数量
     * 
     * @param page DO 分页数据
     * @param warehouseMap 仓库Map（Key: 仓库ID，Value: 仓库DO）
     * @param goodsMap 商品Map（Key: 商品ID，Value: 商品DO）
     * @return VO 分页数据（已填充关联字段）
     */
    default PageResult<InventorySnapshotRespVO> convertPage(PageResult<InventorySnapshotDO> page,
                                                             Map<Long, WarehouseDO> warehouseMap,
                                                             Map<Long, GoodsDO> goodsMap) {
        List<InventorySnapshotRespVO> list = convertList(page.getList());
        list.forEach(vo -> {
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
            
            // 计算可用数量
            BigDecimal quantity = vo.getQuantity() != null ? vo.getQuantity() : BigDecimal.ZERO;
            BigDecimal lockQuantity = vo.getLockQuantity() != null ? vo.getLockQuantity() : BigDecimal.ZERO;
            vo.setAvailableQuantity(quantity.subtract(lockQuantity));
        });
        
        return new PageResult<>(list, page.getTotal());
    }

}

