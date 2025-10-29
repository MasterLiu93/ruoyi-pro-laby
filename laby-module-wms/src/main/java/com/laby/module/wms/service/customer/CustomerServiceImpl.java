package com.laby.module.wms.service.customer;

import cn.hutool.core.collection.CollUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.customer.vo.CustomerPageReqVO;
import com.laby.module.wms.controller.admin.customer.vo.CustomerRespVO;
import com.laby.module.wms.controller.admin.customer.vo.CustomerSaveReqVO;
import com.laby.module.wms.convert.customer.CustomerConvert;
import com.laby.module.wms.dal.dataobject.customer.CustomerDO;
import com.laby.module.wms.dal.mysql.customer.CustomerMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.framework.common.util.collection.CollectionUtils.convertMap;
import static com.laby.module.wms.enums.ErrorCodeConstants.*;

/**
 * 客户信息 Service 实现类
 *
 * 功能说明：
 * 1. 实现客户的增删改查
 * 2. 提供客户编码唯一性校验
 * 3. 提供客户批量查询Map接口
 * 4. 删除前校验是否有关联出库单
 *
 * @author laby
 */
@Service
@Validated
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private CustomerMapper customerMapper;

    @Override
    public Long createCustomer(CustomerSaveReqVO createReqVO) {
        // 1. 校验客户编码唯一性
        validateCustomerCodeUnique(null, createReqVO.getCustomerCode());
        
        // 2. 插入数据库
        CustomerDO customer = CustomerConvert.INSTANCE.convert(createReqVO);
        customerMapper.insert(customer);
        return customer.getId();
    }

    @Override
    public void updateCustomer(CustomerSaveReqVO updateReqVO) {
        // 1. 校验客户是否存在
        validateCustomerExists(updateReqVO.getId());
        
        // 2. 校验客户编码唯一性
        validateCustomerCodeUnique(updateReqVO.getId(), updateReqVO.getCustomerCode());
        
        // 3. 更新数据库
        CustomerDO updateObj = CustomerConvert.INSTANCE.convert(updateReqVO);
        customerMapper.updateById(updateObj);
    }

    @Override
    public void deleteCustomer(Long id) {
        // 1. 校验客户是否存在
        validateCustomerExists(id);
        
        // TODO 2. 校验客户是否有关联的出库单
        // 需要在出库模块完成后实现
        
        // 3. 删除客户（软删除）
        customerMapper.deleteById(id);
    }

    @Override
    public CustomerRespVO getCustomer(Long id) {
        CustomerDO customer = customerMapper.selectById(id);
        return CustomerConvert.INSTANCE.convert(customer);
    }

    @Override
    public PageResult<CustomerRespVO> getCustomerPage(CustomerPageReqVO pageReqVO) {
        // 1. 查询分页数据
        PageResult<CustomerDO> pageResult = customerMapper.selectPage(pageReqVO);
        
        // 2. 转换为 VO
        return CustomerConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    public List<CustomerRespVO> getCustomerSimpleList() {
        // 查询所有启用状态的客户
        List<CustomerDO> list = customerMapper.selectList(CustomerDO::getStatus, 1);
        return CustomerConvert.INSTANCE.convertList(list);
    }

    @Override
    public Map<Long, String> getCustomerMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        List<CustomerDO> list = customerMapper.selectList(CustomerDO::getId, ids);
        return convertMap(list, CustomerDO::getId, CustomerDO::getCustomerName);
    }

    /**
     * 校验客户是否存在
     *
     * @param id 客户ID
     */
    private void validateCustomerExists(Long id) {
        if (customerMapper.selectById(id) == null) {
            throw exception(CUSTOMER_NOT_EXISTS);
        }
    }

    /**
     * 校验客户编码唯一性
     *
     * @param id 客户ID（修改时传入，新增时传null）
     * @param customerCode 客户编码
     */
    private void validateCustomerCodeUnique(Long id, String customerCode) {
        CustomerDO customer = customerMapper.selectOne(CustomerDO::getCustomerCode, customerCode);
        if (customer == null) {
            return;
        }
        // 如果 id 为空，说明是新增，直接抛出异常
        if (id == null || !id.equals(customer.getId())) {
            throw exception(CUSTOMER_CODE_DUPLICATE);
        }
    }
}

