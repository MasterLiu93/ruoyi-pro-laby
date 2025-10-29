package com.laby.module.wms.dal.mysql.carrier;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.carrier.vo.CarrierPageReqVO;
import com.laby.module.wms.dal.dataobject.carrier.CarrierDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 承运商信息 Mapper
 * 
 * 功能说明：
 * - 提供承运商信息的数据库操作接口
 * - 支持分页查询、条件查询等
 * 
 * @author laby
 */
@Mapper
public interface CarrierMapper extends BaseMapperX<CarrierDO> {

    /**
     * 查询承运商分页列表
     * 
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    default PageResult<CarrierDO> selectPage(CarrierPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarrierDO>()
                .likeIfPresent(CarrierDO::getCarrierCode, reqVO.getCarrierCode())
                .likeIfPresent(CarrierDO::getCarrierName, reqVO.getCarrierName())
                .eqIfPresent(CarrierDO::getCarrierType, reqVO.getCarrierType())
                .eqIfPresent(CarrierDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(CarrierDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CarrierDO::getId));
    }
}

