package com.laby.module.wms.dal.mysql.customer;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.customer.vo.CustomerPageReqVO;
import com.laby.module.wms.dal.dataobject.customer.CustomerDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户信息 Mapper
 * 负责客户数据的数据库操作
 *
 * 功能说明：
 * 1. 提供客户分页查询
 * 2. 继承 BaseMapperX 获得基础 CRUD 能力
 * 3. 仅负责数据库操作，不做业务逻辑
 *
 * @author laby
 */
@Mapper
public interface CustomerMapper extends BaseMapperX<CustomerDO> {

    /**
     * 查询客户分页列表
     * 支持按客户编码、名称、类型、等级、状态等条件查询
     *
     * @param reqVO 查询条件
     * @return 分页数据（仅 DO，不包含关联字段）
     */
    default PageResult<CustomerDO> selectPage(CustomerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CustomerDO>()
                .likeIfPresent(CustomerDO::getCustomerCode, reqVO.getCustomerCode())
                .likeIfPresent(CustomerDO::getCustomerName, reqVO.getCustomerName())
                .eqIfPresent(CustomerDO::getCustomerType, reqVO.getCustomerType())
                .eqIfPresent(CustomerDO::getCustomerLevel, reqVO.getCustomerLevel())
                .likeIfPresent(CustomerDO::getContactPerson, reqVO.getContactPerson())
                .likeIfPresent(CustomerDO::getContactPhone, reqVO.getContactPhone())
                .eqIfPresent(CustomerDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(CustomerDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CustomerDO::getId));
    }
}

