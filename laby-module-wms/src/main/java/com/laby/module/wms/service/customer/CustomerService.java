package com.laby.module.wms.service.customer;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.customer.vo.CustomerPageReqVO;
import com.laby.module.wms.controller.admin.customer.vo.CustomerRespVO;
import com.laby.module.wms.controller.admin.customer.vo.CustomerSaveReqVO;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 客户信息 Service 接口
 *
 * 功能说明：
 * 1. 客户的CRUD操作
 * 2. 客户编码唯一性校验
 * 3. 客户与出库单关联校验
 * 4. 批量查询客户Map（用于关联查询）
 *
 * @author laby
 */
public interface CustomerService {

    /**
     * 创建客户
     *
     * 业务流程：
     * 1. 校验客户编码唯一性
     * 2. 插入客户数据
     * 3. 返回客户ID
     *
     * @param createReqVO 创建信息
     * @return 客户ID
     */
    Long createCustomer(@Valid CustomerSaveReqVO createReqVO);

    /**
     * 更新客户
     *
     * 业务流程：
     * 1. 校验客户是否存在
     * 2. 校验客户编码唯一性
     * 3. 更新客户数据
     *
     * @param updateReqVO 更新信息
     */
    void updateCustomer(@Valid CustomerSaveReqVO updateReqVO);

    /**
     * 删除客户
     *
     * 业务流程：
     * 1. 校验客户是否存在
     * 2. 校验客户是否有关联的出库单
     * 3. 删除客户（软删除）
     *
     * @param id 客户ID
     */
    void deleteCustomer(Long id);

    /**
     * 获得客户详情
     *
     * @param id 客户ID
     * @return 客户详情
     */
    CustomerRespVO getCustomer(Long id);

    /**
     * 获得客户分页列表
     *
     * @param pageReqVO 分页查询条件
     * @return 分页列表
     */
    PageResult<CustomerRespVO> getCustomerPage(CustomerPageReqVO pageReqVO);

    /**
     * 获得客户简化列表（用于下拉框）
     *
     * @return 客户简化列表
     */
    List<CustomerRespVO> getCustomerSimpleList();

    /**
     * 获得客户Map（用于批量关联查询）
     * Key: 客户ID，Value: 客户名称
     *
     * @param ids 客户ID列表
     * @return 客户Map
     */
    Map<Long, String> getCustomerMap(Collection<Long> ids);
}

