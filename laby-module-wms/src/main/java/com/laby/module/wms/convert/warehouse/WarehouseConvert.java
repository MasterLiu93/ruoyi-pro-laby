package com.laby.module.wms.convert.warehouse;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.warehouse.vo.warehouse.WarehouseRespVO;
import com.laby.module.wms.controller.admin.warehouse.vo.warehouse.WarehouseSaveReqVO;
import com.laby.module.wms.controller.admin.warehouse.vo.warehouse.WarehouseSimpleRespVO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 仓库 Convert
 *
 * @author Laby
 */
@Mapper
public interface WarehouseConvert {

    WarehouseConvert INSTANCE = Mappers.getMapper(WarehouseConvert.class);

    WarehouseDO convert(WarehouseSaveReqVO bean);

    WarehouseRespVO convert(WarehouseDO bean);

    List<WarehouseRespVO> convertList(List<WarehouseDO> list);

    PageResult<WarehouseRespVO> convertPage(PageResult<WarehouseDO> page);

    List<WarehouseSimpleRespVO> convertSimpleList(List<WarehouseDO> list);

}

