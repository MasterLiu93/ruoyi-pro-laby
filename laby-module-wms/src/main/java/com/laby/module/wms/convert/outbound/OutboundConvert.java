package com.laby.module.wms.convert.outbound;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundItemRespVO;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundItemSaveReqVO;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundRespVO;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundSaveReqVO;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.dal.dataobject.outbound.OutboundDO;
import com.laby.module.wms.dal.dataobject.outbound.OutboundItemDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseLocationDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * WMS出库单 Convert
 *
 * @author laby
 */
@Mapper
public interface OutboundConvert {

    OutboundConvert INSTANCE = Mappers.getMapper(OutboundConvert.class);

    /**
     * DO 转 RespVO
     */
    OutboundRespVO convert(OutboundDO bean);

    /**
     * DO 列表转 RespVO 列表
     */
    List<OutboundRespVO> convertList(List<OutboundDO> list);

    /**
     * 分页结果转换
     */
    PageResult<OutboundRespVO> convertPage(PageResult<OutboundDO> page);

    /**
     * SaveReqVO 转 DO
     */
    OutboundDO convert(OutboundSaveReqVO bean);

    /**
     * 出库单明细：DO 转 RespVO
     */
    @Mapping(target = "locationCode", ignore = true)
    @Mapping(target = "goodsName", ignore = true)
    @Mapping(target = "skuCode", ignore = true)
    @Mapping(target = "goodsUnit", ignore = true)
    OutboundItemRespVO convertItem(OutboundItemDO bean);

    /**
     * 出库单明细：DO 列表转 RespVO 列表
     */
    List<OutboundItemRespVO> convertItemList(List<OutboundItemDO> list);

    /**
     * 出库单明细：SaveReqVO 转 DO
     */
    OutboundItemDO convertItem(OutboundItemSaveReqVO bean);

    /**
     * 出库单明细：SaveReqVO 列表转 DO 列表
     */
    List<OutboundItemDO> convertItemReqList(List<OutboundItemSaveReqVO> list);

    /**
     * 填充出库单响应对象的关联信息
     *
     * @param respVO 出库单响应对象
     * @param warehouseMap 仓库Map（key: warehouseId, value: WarehouseDO）
     */
    default void fillOutboundRespVO(OutboundRespVO respVO, Map<Long, WarehouseDO> warehouseMap) {
        if (respVO == null || respVO.getWarehouseId() == null) {
            return;
        }
        WarehouseDO warehouse = warehouseMap.get(respVO.getWarehouseId());
        if (warehouse != null) {
            respVO.setWarehouseName(warehouse.getWarehouseName());
        }
    }

    /**
     * 填充出库单明细响应对象的关联信息
     *
     * @param itemRespVO 出库单明细响应对象
     * @param goodsMap 商品Map（key: goodsId, value: GoodsDO）
     * @param locationMap 库位Map（key: locationId, value: WarehouseLocationDO）
     */
    default void fillOutboundItemRespVO(OutboundItemRespVO itemRespVO,
                                         Map<Long, GoodsDO> goodsMap,
                                         Map<Long, WarehouseLocationDO> locationMap) {
        if (itemRespVO == null) {
            return;
        }

        // 填充商品信息
        if (itemRespVO.getGoodsId() != null) {
            GoodsDO goods = goodsMap.get(itemRespVO.getGoodsId());
            if (goods != null) {
                itemRespVO.setGoodsName(goods.getGoodsName());
                itemRespVO.setSkuCode(goods.getSkuCode());
                itemRespVO.setGoodsUnit(goods.getUnit());
            }
        }

        // 填充库位信息
        if (itemRespVO.getLocationId() != null) {
            WarehouseLocationDO location = locationMap.get(itemRespVO.getLocationId());
            if (location != null) {
                itemRespVO.setLocationCode(location.getLocationCode());
            }
        }
    }
}

