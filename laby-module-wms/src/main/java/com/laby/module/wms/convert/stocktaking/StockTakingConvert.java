package com.laby.module.wms.convert.stocktaking;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingRespVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingSaveReqVO;
import com.laby.module.wms.dal.dataobject.stocktaking.StockTakingDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 盘点单 Convert
 *
 * @author laby
 */
@Mapper
public interface StockTakingConvert {

    StockTakingConvert INSTANCE = Mappers.getMapper(StockTakingConvert.class);

    /**
     * SaveReqVO 转换为 DO
     */
    @Mapping(target = "takingNo", ignore = true)
    @Mapping(target = "operator", ignore = true)
    @Mapping(target = "operateTime", ignore = true)
    @Mapping(target = "reviewer", ignore = true)
    @Mapping(target = "reviewTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    StockTakingDO convert(StockTakingSaveReqVO bean);

    /**
     * DO 转换为 RespVO
     */
    @Mapping(target = "warehouseName", ignore = true)
    @Mapping(target = "diffQuantity", expression = "java(bean.getActualQuantity() != null ? bean.getActualQuantity().subtract(bean.getBookQuantity()) : null)")
    StockTakingRespVO convert(StockTakingDO bean);

    /**
     * DO 列表转换为 RespVO 列表
     */
    List<StockTakingRespVO> convertList(List<StockTakingDO> list);

    /**
     * 分页结果转换
     */
    @Mapping(target = "list", source = "list")
    PageResult<StockTakingRespVO> convertPage(PageResult<StockTakingDO> page);

}

