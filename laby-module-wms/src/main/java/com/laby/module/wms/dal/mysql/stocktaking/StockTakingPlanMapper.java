package com.laby.module.wms.dal.mysql.stocktaking;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPlanPageReqVO;
import com.laby.module.wms.dal.dataobject.stocktaking.StockTakingPlanDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 盘点计划 Mapper
 *
 * @author laby
 */
@Mapper
public interface StockTakingPlanMapper extends BaseMapperX<StockTakingPlanDO> {

    /**
     * 分页查询盘点计划列表
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<StockTakingPlanDO> selectPage(StockTakingPlanPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StockTakingPlanDO>()
                .likeIfPresent(StockTakingPlanDO::getPlanNo, reqVO.getPlanNo())
                .likeIfPresent(StockTakingPlanDO::getPlanName, reqVO.getPlanName())
                .eqIfPresent(StockTakingPlanDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(StockTakingPlanDO::getTakingType, reqVO.getTakingType())
                .eqIfPresent(StockTakingPlanDO::getScopeType, reqVO.getScopeType())
                .eqIfPresent(StockTakingPlanDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(StockTakingPlanDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(StockTakingPlanDO::getId));
    }

    /**
     * 根据计划编号查询
     *
     * @param planNo 计划编号
     * @return 盘点计划
     */
    default StockTakingPlanDO selectByPlanNo(String planNo) {
        return selectOne(StockTakingPlanDO::getPlanNo, planNo);
    }

}

