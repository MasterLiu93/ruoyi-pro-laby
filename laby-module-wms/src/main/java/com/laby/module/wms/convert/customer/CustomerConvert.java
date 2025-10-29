package com.laby.module.wms.convert.customer;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.customer.vo.CustomerRespVO;
import com.laby.module.wms.controller.admin.customer.vo.CustomerSaveReqVO;
import com.laby.module.wms.dal.dataobject.customer.CustomerDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 客户信息 Convert
 * 使用 MapStruct 进行对象转换
 *
 * 功能说明：
 * 1. SaveReqVO -> DO：用于新增和修改
 * 2. DO -> RespVO：用于查询返回
 * 3. PageResult 转换：用于分页查询
 *
 * @author laby
 */
@Mapper
public interface CustomerConvert {

    CustomerConvert INSTANCE = Mappers.getMapper(CustomerConvert.class);

    /**
     * SaveReqVO -> DO
     * 用于新增和修改客户
     * 
     * 注意：以下字段由框架自动填充或业务逻辑计算，不从VO映射
     * - creator, createTime, updater, updateTime, deleted: 由BaseDO自动管理
     * - totalOrders, totalAmount: 业务统计字段，由订单完成时自动累加
     */
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "totalOrders", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "transMap", ignore = true)
    CustomerDO convert(CustomerSaveReqVO bean);

    /**
     * DO -> RespVO
     * 用于查询返回
     */
    CustomerRespVO convert(CustomerDO bean);

    /**
     * DO List -> RespVO List
     * 用于批量转换
     */
    List<CustomerRespVO> convertList(List<CustomerDO> list);

    /**
     * PageResult<DO> -> PageResult<RespVO>
     * 用于分页查询
     */
    @Mapping(target = "list", source = "list")
    PageResult<CustomerRespVO> convertPage(PageResult<CustomerDO> page);
}

