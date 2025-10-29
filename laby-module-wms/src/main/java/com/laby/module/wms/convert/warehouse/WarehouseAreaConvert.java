package com.laby.module.wms.convert.warehouse;

import cn.hutool.core.util.ObjectUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.framework.common.util.collection.MapUtils;
import com.laby.framework.common.util.object.BeanUtils;
import com.laby.module.wms.controller.admin.warehouse.vo.area.*;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseAreaDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * 库区 Convert
 *
 * @author Laby
 */
@Mapper
public interface WarehouseAreaConvert {

    WarehouseAreaConvert INSTANCE = Mappers.getMapper(WarehouseAreaConvert.class);

    WarehouseAreaDO convert(WarehouseAreaSaveReqVO bean);

    WarehouseAreaRespVO convert(WarehouseAreaDO bean);

    List<WarehouseAreaRespVO> convertList(List<WarehouseAreaDO> list);

    PageResult<WarehouseAreaRespVO> convertPage(PageResult<WarehouseAreaDO> page);

    List<WarehouseAreaSimpleRespVO> convertSimpleList(List<WarehouseAreaDO> list);

    /**
     * 转换库区列表（带仓库名称）
     */
    default List<WarehouseAreaRespVO> convertList(List<WarehouseAreaDO> list, Map<Long, WarehouseDO> warehouseMap) {
        return CollectionUtils.convertList(list, area -> {
            WarehouseAreaRespVO areaVO = BeanUtils.toBean(area, WarehouseAreaRespVO.class);
            MapUtils.findAndThen(warehouseMap, area.getWarehouseId(), warehouse -> areaVO.setWarehouseName(warehouse.getWarehouseName()));
            return areaVO;
        });
    }

    /**
     * 转换库区分页（带仓库名称）
     */
    default PageResult<WarehouseAreaRespVO> convertPage(PageResult<WarehouseAreaDO> page, Map<Long, WarehouseDO> warehouseMap) {
        return new PageResult<>(convertList(page.getList(), warehouseMap), page.getTotal());
    }

}

