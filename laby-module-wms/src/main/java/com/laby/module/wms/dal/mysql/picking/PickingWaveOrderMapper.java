package com.laby.module.wms.dal.mysql.picking;

import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.module.wms.dal.dataobject.picking.PickingWaveOrderDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 拣货波次订单关联 Mapper
 *
 * @author laby
 */
@Mapper
public interface PickingWaveOrderMapper extends BaseMapperX<PickingWaveOrderDO> {

    /**
     * 根据波次ID查询关联的出库单ID列表
     *
     * @param waveId 波次ID
     * @return 出库单ID列表
     */
    default List<Long> selectOutboundIdsByWaveId(Long waveId) {
        return selectList(PickingWaveOrderDO::getWaveId, waveId)
                .stream()
                .map(PickingWaveOrderDO::getOutboundId)
                .toList();
    }

    /**
     * 根据出库单ID查询波次ID
     *
     * @param outboundId 出库单ID
     * @return 波次ID
     */
    default Long selectWaveIdByOutboundId(Long outboundId) {
        PickingWaveOrderDO waveOrder = selectOne(PickingWaveOrderDO::getOutboundId, outboundId);
        return waveOrder != null ? waveOrder.getWaveId() : null;
    }

    /**
     * 根据波次ID删除关联关系
     *
     * @param waveId 波次ID
     */
    default void deleteByWaveId(Long waveId) {
        delete(PickingWaveOrderDO::getWaveId, waveId);
    }
}

