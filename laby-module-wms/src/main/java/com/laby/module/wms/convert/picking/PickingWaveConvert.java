package com.laby.module.wms.convert.picking;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.picking.vo.PickingWaveRespVO;
import com.laby.module.wms.controller.admin.picking.vo.PickingWaveSaveReqVO;
import com.laby.module.wms.dal.dataobject.picking.PickingWaveDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 拣货波次 Convert
 * 
 * 用于 DO、VO 之间的转换
 *
 * @author laby
 */
@Mapper
public interface PickingWaveConvert {

    PickingWaveConvert INSTANCE = Mappers.getMapper(PickingWaveConvert.class);

    /**
     * SaveReqVO 转 DO
     *
     * @param bean SaveReqVO对象
     * @return DO对象
     */
    @Mapping(target = "waveNo", ignore = true)
    @Mapping(target = "orderCount", ignore = true)
    @Mapping(target = "itemCount", ignore = true)
    @Mapping(target = "totalQuantity", ignore = true)
    @Mapping(target = "pickerId", ignore = true)
    @Mapping(target = "pickerName", ignore = true)
    @Mapping(target = "estimatedTime", ignore = true)
    @Mapping(target = "actualTime", ignore = true)
    @Mapping(target = "startTime", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    PickingWaveDO convert(PickingWaveSaveReqVO bean);

    /**
     * DO 转 RespVO
     *
     * @param bean DO对象
     * @return RespVO对象
     */
    @Mapping(target = "warehouseName", ignore = true)
    @Mapping(target = "outboundNos", ignore = true)
    PickingWaveRespVO convert(PickingWaveDO bean);

    /**
     * DO列表 转 RespVO列表
     *
     * @param list DO列表
     * @return RespVO列表
     */
    List<PickingWaveRespVO> convertList(List<PickingWaveDO> list);

    /**
     * DO分页结果 转 RespVO分页结果
     *
     * @param page DO分页结果
     * @return RespVO分页结果
     */
    PageResult<PickingWaveRespVO> convertPage(PageResult<PickingWaveDO> page);
}

