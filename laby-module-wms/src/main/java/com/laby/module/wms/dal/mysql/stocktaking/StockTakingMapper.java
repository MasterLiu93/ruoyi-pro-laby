package com.laby.module.wms.dal.mysql.stocktaking;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPageReqVO;
import com.laby.module.wms.dal.dataobject.stocktaking.StockTakingDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 盘点单 Mapper
 *
 * @author laby
 */
@Mapper
public interface StockTakingMapper extends BaseMapperX<StockTakingDO> {

    /**
     * 分页查询盘点单列表
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<StockTakingDO> selectPage(StockTakingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StockTakingDO>()
                .likeIfPresent(StockTakingDO::getTakingNo, reqVO.getTakingNo())
                .eqIfPresent(StockTakingDO::getPlanId, reqVO.getPlanId())
                .likeIfPresent(StockTakingDO::getPlanNo, reqVO.getPlanNo())
                .eqIfPresent(StockTakingDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(StockTakingDO::getLocationId, reqVO.getLocationId())
                .likeIfPresent(StockTakingDO::getGoodsName, reqVO.getGoodsName())
                .likeIfPresent(StockTakingDO::getSkuCode, reqVO.getSkuCode())
                .eqIfPresent(StockTakingDO::getBatchNo, reqVO.getBatchNo())
                .eqIfPresent(StockTakingDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(StockTakingDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(StockTakingDO::getId));
    }

    /**
     * 根据盘点单号查询
     *
     * @param takingNo 盘点单号
     * @return 盘点单
     */
    default StockTakingDO selectByTakingNo(String takingNo) {
        return selectOne(StockTakingDO::getTakingNo, takingNo);
    }

}

