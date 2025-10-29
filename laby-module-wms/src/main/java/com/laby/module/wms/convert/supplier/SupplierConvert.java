package com.laby.module.wms.convert.supplier;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.supplier.vo.SupplierRespVO;
import com.laby.module.wms.controller.admin.supplier.vo.SupplierSaveReqVO;
import com.laby.module.wms.dal.dataobject.supplier.SupplierDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 供应商信息 Convert
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
public interface SupplierConvert {

    SupplierConvert INSTANCE = Mappers.getMapper(SupplierConvert.class);

    /**
     * SaveReqVO -> DO
     * 用于新增和修改供应商
     */
    SupplierDO convert(SupplierSaveReqVO bean);

    /**
     * DO -> RespVO
     * 用于查询返回
     */
    SupplierRespVO convert(SupplierDO bean);

    /**
     * DO List -> RespVO List
     * 用于批量转换
     */
    List<SupplierRespVO> convertList(List<SupplierDO> list);

    /**
     * PageResult<DO> -> PageResult<RespVO>
     * 用于分页查询
     */
    @Mapping(target = "list", source = "list")
    PageResult<SupplierRespVO> convertPage(PageResult<SupplierDO> page);
}

