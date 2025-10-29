package com.laby.module.wms.dal.mysql.picking;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.picking.vo.PickingTaskPageReqVO;
import com.laby.module.wms.dal.dataobject.picking.PickingTaskDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 拣货任务 Mapper
 *
 * @author laby
 */
@Mapper
public interface PickingTaskMapper extends BaseMapperX<PickingTaskDO> {

    /**
     * 分页查询拣货任务
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<PickingTaskDO> selectPage(PickingTaskPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PickingTaskDO>()
                .likeIfPresent(PickingTaskDO::getTaskNo, reqVO.getTaskNo())
                .eqIfPresent(PickingTaskDO::getWaveId, reqVO.getWaveId())
                .likeIfPresent(PickingTaskDO::getWaveNo, reqVO.getWaveNo())
                .eqIfPresent(PickingTaskDO::getOutboundId, reqVO.getOutboundId())
                .likeIfPresent(PickingTaskDO::getOutboundNo, reqVO.getOutboundNo())
                .eqIfPresent(PickingTaskDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(PickingTaskDO::getGoodsId, reqVO.getGoodsId())
                .eqIfPresent(PickingTaskDO::getLocationId, reqVO.getLocationId())
                .eqIfPresent(PickingTaskDO::getPickerId, reqVO.getPickerId())
                .eqIfPresent(PickingTaskDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PickingTaskDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PickingTaskDO::getId));
    }
}

