package com.laby.module.wms.dal.mysql.picking;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.picking.vo.PickingWavePageReqVO;
import com.laby.module.wms.dal.dataobject.picking.PickingWaveDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 拣货波次 Mapper
 *
 * @author laby
 */
@Mapper
public interface PickingWaveMapper extends BaseMapperX<PickingWaveDO> {

    /**
     * 分页查询拣货波次
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<PickingWaveDO> selectPage(PickingWavePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PickingWaveDO>()
                .likeIfPresent(PickingWaveDO::getWaveNo, reqVO.getWaveNo())
                .eqIfPresent(PickingWaveDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(PickingWaveDO::getWaveType, reqVO.getWaveType())
                .eqIfPresent(PickingWaveDO::getPickerId, reqVO.getPickerId())
                .eqIfPresent(PickingWaveDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PickingWaveDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PickingWaveDO::getId));
    }
}

