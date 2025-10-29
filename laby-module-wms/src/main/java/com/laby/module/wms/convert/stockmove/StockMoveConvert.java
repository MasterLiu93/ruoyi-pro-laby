package com.laby.module.wms.convert.stockmove;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.stockmove.vo.StockMoveRespVO;
import com.laby.module.wms.controller.admin.stockmove.vo.StockMoveSaveReqVO;
import com.laby.module.wms.dal.dataobject.stockmove.StockMoveDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 移库单 Convert
 *
 * @author laby
 */
@Mapper
public interface StockMoveConvert {

    StockMoveConvert INSTANCE = Mappers.getMapper(StockMoveConvert.class);

    /**
     * SaveReqVO 转换为 DO
     * 
     * @param bean SaveReqVO
     * @return DO
     */
    @Mapping(target = "moveNo", ignore = true)
    @Mapping(target = "operator", ignore = true)
    @Mapping(target = "operateTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    StockMoveDO convert(StockMoveSaveReqVO bean);

    /**
     * DO 转换为 RespVO
     *
     * @param bean DO
     * @return RespVO
     */
    @Mapping(target = "warehouseName", ignore = true)
    StockMoveRespVO convert(StockMoveDO bean);

    /**
     * DO 列表转换为 RespVO 列表
     *
     * @param list DO 列表
     * @return RespVO 列表
     */
    List<StockMoveRespVO> convertList(List<StockMoveDO> list);

    /**
     * 分页结果转换
     *
     * @param page DO 分页结果
     * @return RespVO 分页结果
     */
    @Mapping(target = "list", source = "list")
    PageResult<StockMoveRespVO> convertPage(PageResult<StockMoveDO> page);

}

