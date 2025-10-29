package com.laby.module.wms.convert.inbound;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.inbound.vo.InboundItemRespVO;
import com.laby.module.wms.controller.admin.inbound.vo.InboundItemSaveReqVO;
import com.laby.module.wms.controller.admin.inbound.vo.InboundRespVO;
import com.laby.module.wms.controller.admin.inbound.vo.InboundSaveReqVO;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.dal.dataobject.inbound.InboundDO;
import com.laby.module.wms.dal.dataobject.inbound.InboundItemDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseLocationDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * 入库单 Convert
 * 使用 MapStruct 进行对象转换
 *
 * @author laby
 */
@Mapper
public interface InboundConvert {

    InboundConvert INSTANCE = Mappers.getMapper(InboundConvert.class);

    /**
     * SaveReqVO -> DO（不含明细）
     */
    InboundDO convert(InboundSaveReqVO bean);

    /**
     * DO -> RespVO（不含关联字段）
     */
    InboundRespVO convert(InboundDO bean);

    /**
     * DO List -> RespVO List（不含关联字段）
     */
    List<InboundRespVO> convertList(List<InboundDO> list);

    /**
     * PageResult<DO> -> PageResult<RespVO>（含关联字段）
     *
     * @param page 入库单分页数据
     * @param warehouseMap 仓库Map（Key: 仓库ID，Value: 仓库DO）
     * @return VO 分页数据（已填充关联字段）
     */
    default PageResult<InboundRespVO> convertPage(PageResult<InboundDO> page,
                                                   Map<Long, WarehouseDO> warehouseMap) {
        PageResult<InboundRespVO> result = convertPage(page);
        // 填充关联字段
        result.getList().forEach(vo -> {
            if (vo.getWarehouseId() != null && warehouseMap.containsKey(vo.getWarehouseId())) {
                vo.setWarehouseName(warehouseMap.get(vo.getWarehouseId()).getWarehouseName());
            }
        });
        return result;
    }

    /**
     * PageResult<DO> -> PageResult<RespVO>（不含关联字段）
     */
    @Mapping(target = "list", source = "list")
    PageResult<InboundRespVO> convertPage(PageResult<InboundDO> page);

    // ========== 入库明细转换 ==========

    /**
     * ItemSaveReqVO -> ItemDO
     */
    InboundItemDO convert(InboundItemSaveReqVO bean);

    /**
     * ItemSaveReqVO List -> ItemDO List
     */
    List<InboundItemDO> convertItemList(List<InboundItemSaveReqVO> list);

    /**
     * ItemDO -> ItemRespVO（不含关联字段）
     */
    InboundItemRespVO convertItem(InboundItemDO bean);

    /**
     * ItemDO List -> ItemRespVO List（含关联字段）
     *
     * @param list 明细DO列表
     * @param goodsMap 商品Map（Key: 商品ID，Value: 商品DO）
     * @param locationMap 库位Map（Key: 库位ID，Value: 库位DO）
     * @return VO列表（已填充关联字段）
     */
    default List<InboundItemRespVO> convertItemList(List<InboundItemDO> list,
                                                      Map<Long, GoodsDO> goodsMap,
                                                      Map<Long, WarehouseLocationDO> locationMap) {
        List<InboundItemRespVO> result = convertItemList0(list);
        // 填充关联字段
        result.forEach(vo -> {
            if (vo.getGoodsId() != null && goodsMap.containsKey(vo.getGoodsId())) {
                GoodsDO goods = goodsMap.get(vo.getGoodsId());
                vo.setGoodsName(goods.getGoodsName());
                vo.setSkuCode(goods.getSkuCode());
                vo.setGoodsUnit(goods.getUnit());
            }
            if (vo.getLocationId() != null && locationMap.containsKey(vo.getLocationId())) {
                vo.setLocationCode(locationMap.get(vo.getLocationId()).getLocationCode());
            }
        });
        return result;
    }

    /**
     * ItemDO List -> ItemRespVO List（不含关联字段）
     */
    List<InboundItemRespVO> convertItemList0(List<InboundItemDO> list);

}

