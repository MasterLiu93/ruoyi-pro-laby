package com.laby.module.wms.dal.mysql.outbound;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundPageReqVO;
import com.laby.module.wms.dal.dataobject.outbound.OutboundDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * WMS出库单 Mapper
 *
 * @author laby
 */
@Mapper
public interface OutboundMapper extends BaseMapperX<OutboundDO> {

    /**
     * 分页查询出库单列表
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<OutboundDO> selectPage(OutboundPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OutboundDO>()
                .likeIfPresent(OutboundDO::getOutboundNo, reqVO.getOutboundNo())
                .eqIfPresent(OutboundDO::getOutboundType, reqVO.getOutboundType())
                .eqIfPresent(OutboundDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(OutboundDO::getCustomerId, reqVO.getCustomerId())
                .eqIfPresent(OutboundDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(OutboundDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(OutboundDO::getExpectedShipmentTime, reqVO.getExpectedShipmentTime())
                .orderByDesc(OutboundDO::getId));
    }
}

