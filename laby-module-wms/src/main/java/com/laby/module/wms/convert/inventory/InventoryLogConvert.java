package com.laby.module.wms.convert.inventory;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.inventory.vo.log.InventoryLogRespVO;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.dal.dataobject.inventory.InventoryLogDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseLocationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * 库存流水 Convert
 * 
 * 功能说明：
 * - 使用 MapStruct 进行对象转换
 * - 处理 DO 和 VO 之间的转换
 * - 填充关联字段（仓库名、商品名、库位编码）
 *
 * @author laby
 */
@Mapper
public interface InventoryLogConvert {

    InventoryLogConvert INSTANCE = Mappers.getMapper(InventoryLogConvert.class);

    /**
     * DO -> RespVO（不含关联字段）
     */
    InventoryLogRespVO convert(InventoryLogDO bean);

    /**
     * DO List -> RespVO List（不含关联字段）
     */
    List<InventoryLogRespVO> convertList(List<InventoryLogDO> list);

    /**
     * PageResult<DO> -> PageResult<RespVO>（含关联字段）
     * 
     * 实现步骤：
     * 1. 转换分页数据
     * 2. 填充仓库名称
     * 3. 填充商品名称和SKU编码
     * 4. 填充库位编码
     * 
     * @param page DO 分页数据
     * @param warehouseMap 仓库Map（Key: 仓库ID，Value: 仓库DO）
     * @param goodsMap 商品Map（Key: 商品ID，Value: 商品DO）
     * @param locationMap 库位Map（Key: 库位ID，Value: 库位DO）
     * @return VO 分页数据（已填充关联字段）
     */
    default PageResult<InventoryLogRespVO> convertPage(PageResult<InventoryLogDO> page,
                                                        Map<Long, WarehouseDO> warehouseMap,
                                                        Map<Long, GoodsDO> goodsMap,
                                                        Map<Long, WarehouseLocationDO> locationMap) {
        List<InventoryLogRespVO> list = convertList(page.getList());
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
            
            // 填充库位编码
            if (vo.getLocationId() != null) {
                WarehouseLocationDO location = locationMap.get(vo.getLocationId());
                if (location != null) {
                    vo.setLocationCode(location.getLocationCode());
                }
            }
        });
        
        return new PageResult<>(list, page.getTotal());
    }

}

