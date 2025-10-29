package com.laby.module.wms.dal.mysql.inbound;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.inbound.vo.InboundPageReqVO;
import com.laby.module.wms.dal.dataobject.inbound.InboundDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入库单 Mapper
 * 仅负责数据库操作，不做业务逻辑
 *
 * @author laby
 */
@Mapper
public interface InboundMapper extends BaseMapperX<InboundDO> {

    /**
     * 查询入库单分页列表
     *
     * 查询条件：
     * - 入库单号（模糊查询）
     * - 入库类型（精确查询）
     * - 仓库ID（精确查询）
     * - 供应商ID（精确查询）
     * - 入库状态（精确查询）
     * - 创建时间范围
     * - 预计到货时间范围
     *
     * 排序规则：
     * - 按创建时间倒序
     *
     * @param reqVO 查询条件
     * @return 分页数据（仅 DO，不包含关联字段）
     */
    default PageResult<InboundDO> selectPage(InboundPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<InboundDO>()
                .likeIfPresent(InboundDO::getInboundNo, reqVO.getInboundNo())
                .eqIfPresent(InboundDO::getInboundType, reqVO.getInboundType())
                .eqIfPresent(InboundDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(InboundDO::getSupplierId, reqVO.getSupplierId())
                .eqIfPresent(InboundDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(InboundDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(InboundDO::getExpectedArrivalTime, reqVO.getExpectedArrivalTime())
                .orderByDesc(InboundDO::getId));
    }

}

