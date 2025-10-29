package com.laby.module.wms.convert.inventory;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.inventory.vo.InventoryRespVO;
import com.laby.module.wms.controller.admin.inventory.vo.InventorySaveReqVO;
import com.laby.module.wms.dal.dataobject.inventory.InventoryDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseLocationDO;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * 库存信息 Convert
 * 对象转换工具，使用 MapStruct 自动生成转换代码
 * 
 * 功能说明：
 * - 将 VO 和 DO 之间进行转换
 * - 处理分页结果的转换
 * - 处理关联查询字段的填充（仓库名、库位编码、商品名等）
 * 
 * @author laby
 */
@Mapper
public interface InventoryConvert {

    InventoryConvert INSTANCE = Mappers.getMapper(InventoryConvert.class);

    /**
     * SaveReqVO 转 DO
     * 用于新增和修改
     */
    InventoryDO convert(InventorySaveReqVO bean);

    /**
     * DO 转 RespVO
     * 用于单条记录返回
     */
    InventoryRespVO convert(InventoryDO bean);

    /**
     * DO 列表转 RespVO 列表
     * 用于批量转换
     */
    List<InventoryRespVO> convertList(List<InventoryDO> list);

    /**
     * 分页结果转换（带关联数据填充）
     * 
     * 说明：
     * - 先将 DO 分页结果转换为 VO 分页结果
     * - 然后填充关联查询字段：
     *   - warehouseName（仓库名称）
     *   - locationCode（库位编码）
     *   - goodsName、skuCode、goodsUnit（商品信息）
     * 
     * @param pageResult DO分页结果
     * @param warehouseMap 仓库Map，key=仓库ID
     * @param locationMap 库位Map，key=库位ID
     * @param goodsMap 商品Map，key=商品ID
     * @return VO分页结果（已填充关联字段）
     */
    default PageResult<InventoryRespVO> convertPage(PageResult<InventoryDO> pageResult,
                                                     Map<Long, WarehouseDO> warehouseMap,
                                                     Map<Long, WarehouseLocationDO> locationMap,
                                                     Map<Long, GoodsDO> goodsMap) {
        PageResult<InventoryRespVO> voPageResult = convertPage(pageResult);
        // 填充关联查询字段
        voPageResult.getList().forEach(vo -> {
            // 填充仓库名称
            if (vo.getWarehouseId() != null && warehouseMap.containsKey(vo.getWarehouseId())) {
                vo.setWarehouseName(warehouseMap.get(vo.getWarehouseId()).getWarehouseName());
            }
            // 填充库位编码
            if (vo.getLocationId() != null && locationMap.containsKey(vo.getLocationId())) {
                vo.setLocationCode(locationMap.get(vo.getLocationId()).getLocationCode());
            }
            // 填充商品信息
            if (vo.getGoodsId() != null && goodsMap.containsKey(vo.getGoodsId())) {
                GoodsDO goods = goodsMap.get(vo.getGoodsId());
                vo.setGoodsName(goods.getGoodsName());
                vo.setSkuCode(goods.getSkuCode());
                vo.setGoodsUnit(goods.getUnit());
            }
        });
        return voPageResult;
    }

    /**
     * 分页结果转换（基础转换，不填充关联数据）
     * MapStruct 自动生成
     */
    PageResult<InventoryRespVO> convertPage(PageResult<InventoryDO> page);

}

