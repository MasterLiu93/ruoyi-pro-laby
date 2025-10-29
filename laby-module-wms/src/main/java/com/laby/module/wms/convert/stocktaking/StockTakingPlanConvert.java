package com.laby.module.wms.convert.stocktaking;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPlanRespVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPlanSaveReqVO;
import com.laby.module.wms.dal.dataobject.stocktaking.StockTakingPlanDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 盘点计划 Convert
 *
 * @author laby
 */
@Mapper
public interface StockTakingPlanConvert {

    StockTakingPlanConvert INSTANCE = Mappers.getMapper(StockTakingPlanConvert.class);

    /**
     * SaveReqVO 转换为 DO
     */
    @Mapping(target = "planNo", ignore = true)
    @Mapping(target = "actualStartTime", ignore = true)
    @Mapping(target = "actualEndTime", ignore = true)
    @Mapping(target = "totalCount", ignore = true)
    @Mapping(target = "completedCount", ignore = true)
    @Mapping(target = "diffCount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "auditUser", ignore = true)
    @Mapping(target = "auditTime", ignore = true)
    StockTakingPlanDO convert(StockTakingPlanSaveReqVO bean);

    /**
     * DO 转换为 RespVO
     */
    @Mapping(target = "warehouseName", ignore = true)
    StockTakingPlanRespVO convert(StockTakingPlanDO bean);

    /**
     * DO 列表转换为 RespVO 列表
     */
    List<StockTakingPlanRespVO> convertList(List<StockTakingPlanDO> list);

    /**
     * 分页结果转换
     */
    @Mapping(target = "list", source = "list")
    PageResult<StockTakingPlanRespVO> convertPage(PageResult<StockTakingPlanDO> page);

}

