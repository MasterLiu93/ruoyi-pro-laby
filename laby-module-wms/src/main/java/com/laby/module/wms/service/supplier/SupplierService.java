package com.laby.module.wms.service.supplier;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.supplier.vo.SupplierPageReqVO;
import com.laby.module.wms.controller.admin.supplier.vo.SupplierRespVO;
import com.laby.module.wms.controller.admin.supplier.vo.SupplierSaveReqVO;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 供应商信息 Service 接口
 *
 * 功能说明：
 * 1. 供应商的CRUD操作
 * 2. 供应商编码唯一性校验
 * 3. 供应商与入库单关联校验
 * 4. 批量查询供应商Map（用于关联查询）
 *
 * @author laby
 */
public interface SupplierService {

    /**
     * 创建供应商
     *
     * 业务流程：
     * 1. 校验供应商编码唯一性
     * 2. 插入供应商数据
     * 3. 返回供应商ID
     *
     * @param createReqVO 创建信息
     * @return 供应商ID
     */
    Long createSupplier(@Valid SupplierSaveReqVO createReqVO);

    /**
     * 更新供应商
     *
     * 业务流程：
     * 1. 校验供应商是否存在
     * 2. 校验供应商编码唯一性
     * 3. 更新供应商数据
     *
     * @param updateReqVO 更新信息
     */
    void updateSupplier(@Valid SupplierSaveReqVO updateReqVO);

    /**
     * 删除供应商
     *
     * 业务流程：
     * 1. 校验供应商是否存在
     * 2. 校验供应商是否有关联的入库单
     * 3. 删除供应商（软删除）
     *
     * @param id 供应商ID
     */
    void deleteSupplier(Long id);

    /**
     * 获得供应商详情
     *
     * @param id 供应商ID
     * @return 供应商详情
     */
    SupplierRespVO getSupplier(Long id);

    /**
     * 获得供应商分页列表
     *
     * @param pageReqVO 分页查询条件
     * @return 分页列表
     */
    PageResult<SupplierRespVO> getSupplierPage(SupplierPageReqVO pageReqVO);

    /**
     * 获得供应商简化列表（用于下拉框）
     *
     * @return 供应商简化列表
     */
    List<SupplierRespVO> getSupplierSimpleList();

    /**
     * 获得供应商Map（用于批量关联查询）
     * Key: 供应商ID，Value: 供应商名称
     *
     * @param ids 供应商ID列表
     * @return 供应商Map
     */
    Map<Long, String> getSupplierMap(Collection<Long> ids);
}

