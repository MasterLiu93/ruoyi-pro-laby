package com.laby.module.wms.dal.mysql.supplier;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.supplier.vo.SupplierPageReqVO;
import com.laby.module.wms.dal.dataobject.supplier.SupplierDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 供应商信息 Mapper
 * 负责供应商数据的数据库操作
 *
 * 功能说明：
 * 1. 提供供应商分页查询
 * 2. 继承 BaseMapperX 获得基础 CRUD 能力
 * 3. 仅负责数据库操作，不做业务逻辑
 *
 * @author laby
 */
@Mapper
public interface SupplierMapper extends BaseMapperX<SupplierDO> {

    /**
     * 查询供应商分页列表
     * 支持按供应商编码、名称、类型、信用等级、状态等条件查询
     *
     * @param reqVO 查询条件
     * @return 分页数据（仅 DO，不包含关联字段）
     */
    default PageResult<SupplierDO> selectPage(SupplierPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SupplierDO>()
                .likeIfPresent(SupplierDO::getSupplierCode, reqVO.getSupplierCode())
                .likeIfPresent(SupplierDO::getSupplierName, reqVO.getSupplierName())
                .eqIfPresent(SupplierDO::getSupplierType, reqVO.getSupplierType())
                .eqIfPresent(SupplierDO::getCreditLevel, reqVO.getCreditLevel())
                .likeIfPresent(SupplierDO::getContactPerson, reqVO.getContactPerson())
                .likeIfPresent(SupplierDO::getContactPhone, reqVO.getContactPhone())
                .eqIfPresent(SupplierDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(SupplierDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SupplierDO::getId));
    }
}

