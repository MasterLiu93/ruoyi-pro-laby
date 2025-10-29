package com.laby.module.wms.service.inventory;

import cn.hutool.core.collection.CollUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.module.wms.controller.admin.inventory.vo.InventoryPageReqVO;
import com.laby.module.wms.controller.admin.inventory.vo.InventorySaveReqVO;
import com.laby.module.wms.convert.inventory.InventoryConvert;
import com.laby.module.wms.dal.dataobject.inventory.InventoryDO;
import com.laby.module.wms.dal.mysql.inventory.InventoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.math.BigDecimal;

import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.module.wms.enums.ErrorCodeConstants.*;

/**
 * 库存信息 Service 实现类
 * 
 * 实现说明：
 * - 所有公共方法都经过参数校验（@Validated）
 * - 创建/更新时会校验数据合法性
 * - 删除前会校验库存数量
 * - 使用 MapStruct 进行对象转换（InventoryConvert）
 * - 异常统一使用 ServiceException，错误码定义在 ErrorCodeConstants 中
 * 
 * @author laby
 */
@Service
@Validated
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    @Resource
    private InventoryMapper inventoryMapper;

    /**
     * 创建库存
     * 
     * 实现步骤：
     * 1. 校验库存数量和锁定数量合法性
     * 2. 转换VO为DO
     * 3. 插入数据库
     * 
     * @param createReqVO 创建信息
     * @return 库存ID
     */
    @Override
    public Long createInventory(InventorySaveReqVO createReqVO) {
        // 校验数据合法性
        validateInventoryData(createReqVO);
        
        // 转换并插入
        InventoryDO inventory = InventoryConvert.INSTANCE.convert(createReqVO);
        // 初始化版本号
        inventory.setVersion(0);
        inventoryMapper.insert(inventory);
        
        return inventory.getId();
    }

    /**
     * 更新库存
     * 
     * 实现步骤：
     * 1. 校验库存是否存在
     * 2. 校验库存数量和锁定数量合法性
     * 3. 转换VO为DO
     * 4. 使用乐观锁更新数据库
     * 
     * @param updateReqVO 更新信息
     */
    @Override
    public void updateInventory(InventorySaveReqVO updateReqVO) {
        // 校验存在
        validateInventoryExists(updateReqVO.getId());
        
        // 校验数据合法性
        validateInventoryData(updateReqVO);
        
        // 转换并更新
        InventoryDO updateObj = InventoryConvert.INSTANCE.convert(updateReqVO);
        inventoryMapper.updateById(updateObj);
    }

    /**
     * 删除库存
     * 
     * 实现步骤：
     * 1. 校验库存是否存在
     * 2. 校验库存数量和锁定数量是否为0
     * 3. 逻辑删除
     * 
     * @param id 库存ID
     */
    @Override
    public void deleteInventory(Long id) {
        // 校验存在
        InventoryDO inventory = validateInventoryExists(id);
        
        // 校验库存数量
        if (inventory.getQuantity().compareTo(BigDecimal.ZERO) > 0 
                || inventory.getLockQuantity().compareTo(BigDecimal.ZERO) > 0) {
            throw exception(INVENTORY_HAS_QUANTITY);
        }
        
        // 删除
        inventoryMapper.deleteById(id);
    }

    /**
     * 获得库存详情
     * 
     * @param id 库存ID
     * @return 库存信息
     */
    @Override
    public InventoryDO getInventory(Long id) {
        return inventoryMapper.selectById(id);
    }

    /**
     * 获得库存分页列表
     * 
     * 实现步骤：
     * 1. 调用 Mapper 分页查询
     * 2. 返回分页结果
     * 
     * 注意：
     * - 关联查询由 Controller 层处理
     * 
     * @param pageReqVO 分页查询条件
     * @return 库存分页结果
     */
    @Override
    public PageResult<InventoryDO> getInventoryPage(InventoryPageReqVO pageReqVO) {
        return inventoryMapper.selectPage(pageReqVO);
    }

    // ==================== 私有方法 ====================

    /**
     * 校验库存是否存在
     * 
     * @param id 库存ID
     * @return 库存DO
     * @throws com.laby.framework.common.exception.ServiceException 不存在时抛出异常
     */
    private InventoryDO validateInventoryExists(Long id) {
        InventoryDO inventory = inventoryMapper.selectById(id);
        if (inventory == null) {
            throw exception(INVENTORY_NOT_EXISTS);
        }
        return inventory;
    }

    /**
     * 校验库存数据合法性
     * 
     * 校验规则：
     * 1. 库存数量不能为负数
     * 2. 锁定数量不能为负数
     * 3. 锁定数量不能大于库存数量
     * 
     * @param reqVO 库存数据
     * @throws com.laby.framework.common.exception.ServiceException 校验失败时抛出异常
     */
    private void validateInventoryData(InventorySaveReqVO reqVO) {
        // 校验库存数量
        if (reqVO.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw exception(INVENTORY_QUANTITY_INVALID);
        }
        
        // 校验锁定数量
        BigDecimal lockQuantity = reqVO.getLockQuantity() != null ? reqVO.getLockQuantity() : BigDecimal.ZERO;
        if (lockQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw exception(INVENTORY_LOCK_QUANTITY_INVALID);
        }
        
        // 校验锁定数量不能大于库存数量
        if (lockQuantity.compareTo(reqVO.getQuantity()) > 0) {
            throw exception(INVENTORY_LOCK_QUANTITY_EXCEED);
        }
    }

}

