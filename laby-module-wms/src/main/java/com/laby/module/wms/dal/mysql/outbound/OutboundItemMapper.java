package com.laby.module.wms.dal.mysql.outbound;

import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.module.wms.dal.dataobject.outbound.OutboundItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * WMS出库单明细 Mapper
 *
 * @author laby
 */
@Mapper
public interface OutboundItemMapper extends BaseMapperX<OutboundItemDO> {

    /**
     * 根据出库单ID查询明细列表
     *
     * @param outboundId 出库单ID
     * @return 明细列表
     */
    default List<OutboundItemDO> selectListByOutboundId(Long outboundId) {
        return selectList(OutboundItemDO::getOutboundId, outboundId);
    }

    /**
     * 根据出库单ID删除明细
     *
     * @param outboundId 出库单ID
     */
    default void deleteByOutboundId(Long outboundId) {
        delete(OutboundItemDO::getOutboundId, outboundId);
    }

    /**
     * 根据出库单ID批量删除明细
     *
     * @param outboundIds 出库单ID列表
     */
    default void deleteByOutboundIds(List<Long> outboundIds) {
        delete(OutboundItemDO::getOutboundId, outboundIds);
    }
}

