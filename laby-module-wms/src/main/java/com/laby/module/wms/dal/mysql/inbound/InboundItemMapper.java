package com.laby.module.wms.dal.mysql.inbound;

import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.dal.dataobject.inbound.InboundItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 入库明细 Mapper
 * 仅负责数据库操作，不做业务逻辑
 *
 * @author laby
 */
@Mapper
public interface InboundItemMapper extends BaseMapperX<InboundItemDO> {

    /**
     * 根据入库单ID查询明细列表
     *
     * @param inboundId 入库单ID
     * @return 明细列表（仅 DO，不包含关联字段）
     */
    default List<InboundItemDO> selectListByInboundId(Long inboundId) {
        return selectList(new LambdaQueryWrapperX<InboundItemDO>()
                .eq(InboundItemDO::getInboundId, inboundId)
                .orderByAsc(InboundItemDO::getId));
    }

    /**
     * 根据入库单ID删除明细
     *
     * @param inboundId 入库单ID
     * @return 删除数量
     */
    default int deleteByInboundId(Long inboundId) {
        return delete(new LambdaQueryWrapperX<InboundItemDO>()
                .eq(InboundItemDO::getInboundId, inboundId));
    }

}

