package com.laby.module.wms.convert.warehouse;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.framework.common.util.collection.MapUtils;
import com.laby.framework.common.util.object.BeanUtils;
import com.laby.module.wms.controller.admin.warehouse.vo.location.*;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseAreaDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseLocationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * 库位 Convert
 *
 * @author Laby
 */
@Mapper
public interface WarehouseLocationConvert {

    WarehouseLocationConvert INSTANCE = Mappers.getMapper(WarehouseLocationConvert.class);

    WarehouseLocationDO convert(WarehouseLocationSaveReqVO bean);

    WarehouseLocationRespVO convert(WarehouseLocationDO bean);

    List<WarehouseLocationRespVO> convertList(List<WarehouseLocationDO> list);

    PageResult<WarehouseLocationRespVO> convertPage(PageResult<WarehouseLocationDO> page);

    List<WarehouseLocationSimpleRespVO> convertSimpleList(List<WarehouseLocationDO> list);

    /**
     * 转换库位列表（带仓库名称和库区名称）
     */
    default List<WarehouseLocationRespVO> convertList(List<WarehouseLocationDO> list,
                                                       Map<Long, WarehouseDO> warehouseMap,
                                                       Map<Long, WarehouseAreaDO> areaMap) {
        return CollectionUtils.convertList(list, location -> {
            WarehouseLocationRespVO locationVO = BeanUtils.toBean(location, WarehouseLocationRespVO.class);
            MapUtils.findAndThen(warehouseMap, location.getWarehouseId(), warehouse -> locationVO.setWarehouseName(warehouse.getWarehouseName()));
            MapUtils.findAndThen(areaMap, location.getAreaId(), area -> locationVO.setAreaName(area.getAreaName()));
            return locationVO;
        });
    }

    /**
     * 转换库位分页（带仓库名称和库区名称）
     */
    default PageResult<WarehouseLocationRespVO> convertPage(PageResult<WarehouseLocationDO> page,
                                                             Map<Long, WarehouseDO> warehouseMap,
                                                             Map<Long, WarehouseAreaDO> areaMap) {
        return new PageResult<>(convertList(page.getList(), warehouseMap, areaMap), page.getTotal());
    }

}

