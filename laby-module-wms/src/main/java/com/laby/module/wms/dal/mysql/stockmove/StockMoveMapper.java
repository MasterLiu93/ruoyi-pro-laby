package com.laby.module.wms.dal.mysql.stockmove;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.stockmove.vo.StockMovePageReqVO;
import com.laby.module.wms.dal.dataobject.stockmove.StockMoveDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 移库单 Mapper
 *
 * @author laby
 */
@Mapper
public interface StockMoveMapper extends BaseMapperX<StockMoveDO> {

    /**
     * 分页查询移库单列表
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<StockMoveDO> selectPage(StockMovePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StockMoveDO>()
                .likeIfPresent(StockMoveDO::getMoveNo, reqVO.getMoveNo())
                .eqIfPresent(StockMoveDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(StockMoveDO::getMoveType, reqVO.getMoveType())
                .likeIfPresent(StockMoveDO::getGoodsName, reqVO.getGoodsName())
                .likeIfPresent(StockMoveDO::getSkuCode, reqVO.getSkuCode())
                .eqIfPresent(StockMoveDO::getBatchNo, reqVO.getBatchNo())
                .eqIfPresent(StockMoveDO::getFromLocationId, reqVO.getFromLocationId())
                .eqIfPresent(StockMoveDO::getToLocationId, reqVO.getToLocationId())
                .eqIfPresent(StockMoveDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(StockMoveDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(StockMoveDO::getId));
    }

    /**
     * 根据移库单号查询移库单
     *
     * @param moveNo 移库单号
     * @return 移库单
     */
    default StockMoveDO selectByMoveNo(String moveNo) {
        return selectOne(StockMoveDO::getMoveNo, moveNo);
    }

}

