package com.laby.module.wms.convert.carrier;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.carrier.vo.CarrierRespVO;
import com.laby.module.wms.controller.admin.carrier.vo.CarrierSaveReqVO;
import com.laby.module.wms.dal.dataobject.carrier.CarrierDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 承运商信息 Convert
 * 
 * 功能说明：
 * - 使用 MapStruct 进行对象转换
 * - 支持 DO、VO 之间的转换
 * - 支持批量转换和分页转换
 * 
 * @author laby
 */
@Mapper
public interface CarrierConvert {

    CarrierConvert INSTANCE = Mappers.getMapper(CarrierConvert.class);

    /**
     * SaveReqVO -> DO
     * 用于新增和修改承运商
     */
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "transMap", ignore = true)
    CarrierDO convert(CarrierSaveReqVO bean);

    /**
     * DO -> RespVO
     * 用于查询返回
     */
    CarrierRespVO convert(CarrierDO bean);

    /**
     * DO List -> RespVO List
     * 用于批量转换
     */
    List<CarrierRespVO> convertList(List<CarrierDO> list);

    /**
     * PageResult<DO> -> PageResult<RespVO>
     * 用于分页查询
     */
    @Mapping(target = "list", source = "list")
    PageResult<CarrierRespVO> convertPage(PageResult<CarrierDO> page);
}

