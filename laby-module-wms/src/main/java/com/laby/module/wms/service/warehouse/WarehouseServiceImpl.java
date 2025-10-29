package com.laby.module.wms.service.warehouse;

import cn.hutool.core.collection.CollUtil;
import com.laby.framework.common.enums.CommonStatusEnum;
import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.module.wms.controller.admin.warehouse.vo.warehouse.WarehousePageReqVO;
import com.laby.module.wms.controller.admin.warehouse.vo.warehouse.WarehouseSaveReqVO;
import com.laby.module.wms.convert.warehouse.WarehouseConvert;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.mysql.warehouse.WarehouseMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.module.wms.enums.ErrorCodeConstants.*;

/**
 * 仓库 Service 实现类
 * 
 * 实现说明：
 * - 所有公共方法都经过参数校验（@Validated）
 * - 增删改操作都会进行业务规则校验
 * - 使用 MapStruct 进行对象转换
 * - 异常统一使用 ServiceException，错误码定义在 ErrorCodeConstants 中
 *
 * @author laby
 */
@Service
@Validated
public class WarehouseServiceImpl implements WarehouseService {

    @Resource
    private WarehouseMapper warehouseMapper;

    /**
     * 创建仓库
     * 
     * 实现步骤：
     * 1. 校验仓库编码唯一性（同一租户内不能重复）
     * 2. 使用 MapStruct 转换 VO 为 DO
     * 3. 插入数据库（MyBatis Plus 自动设置 ID、创建时间等）
     * 4. 返回新生成的仓库ID
     */
    @Override
    public Long createWarehouse(WarehouseSaveReqVO createReqVO) {
        // 1. 校验仓库编码唯一性
        validateWarehouseCodeUnique(null, createReqVO.getWarehouseCode());

        // 2. 转换并插入
        WarehouseDO warehouse = WarehouseConvert.INSTANCE.convert(createReqVO);
        warehouseMapper.insert(warehouse);
        
        // 3. 返回ID
        return warehouse.getId();
    }

    /**
     * 更新仓库
     * 
     * 实现步骤：
     * 1. 校验仓库是否存在
     * 2. 校验仓库编码唯一性（排除自己）
     * 3. 使用 MapStruct 转换 VO 为 DO
     * 4. 更新数据库（MyBatis Plus 自动更新修改时间等）
     */
    @Override
    public void updateWarehouse(WarehouseSaveReqVO updateReqVO) {
        // 1. 校验存在
        validateWarehouseExists(updateReqVO.getId());
        
        // 2. 校验仓库编码唯一性
        validateWarehouseCodeUnique(updateReqVO.getId(), updateReqVO.getWarehouseCode());

        // 3. 转换并更新
        WarehouseDO updateObj = WarehouseConvert.INSTANCE.convert(updateReqVO);
        warehouseMapper.updateById(updateObj);
    }

    /**
     * 删除仓库
     * 
     * 实现步骤：
     * 1. 校验仓库是否存在
     * 2. 校验仓库下是否有关联数据（库区、库存等）
     * 3. 执行逻辑删除
     * 
     * 注意：
     * - 当前实现中，关联数据校验部分为 TODO，需要后续补充
     * - 建议在实际使用前完善关联校验逻辑
     */
    @Override
    public void deleteWarehouse(Long id) {
        // 1. 校验存在
        validateWarehouseExists(id);
        
        // 2. 校验关联数据（TODO: 需要注入 WarehouseAreaService 进行校验）
        // TODO: 校验仓库下是否存在库区、库存等数据，存在则不允许删除

        // 3. 执行删除（逻辑删除）
        warehouseMapper.deleteById(id);
    }

    /**
     * 批量删除仓库
     * 
     * 实现步骤：
     * 1. 逐个校验仓库是否存在
     * 2. 批量执行逻辑删除
     * 
     * 注意：
     * - 使用 forEach 逐个校验，任何一个校验失败都会抛出异常
     * - 事务会自动回滚，保证数据一致性
     */
    @Override
    public void deleteWarehouseList(List<Long> ids) {
        // 1. 逐个校验存在
        ids.forEach(this::validateWarehouseExists);

        // 2. 批量删除
        warehouseMapper.deleteBatchIds(ids);
    }

    /**
     * 获取仓库详情
     * 
     * @return 仓库DO，如果不存在返回null
     */
    @Override
    public WarehouseDO getWarehouse(Long id) {
        return warehouseMapper.selectById(id);
    }

    /**
     * 获取仓库分页列表
     * 
     * 说明：
     * - 直接调用 Mapper 的分页查询方法
     * - Mapper 层使用 LambdaQueryWrapperX 构建查询条件
     * - 返回的是 DO 对象，Controller 层负责转换为 VO 和关联数据填充
     */
    @Override
    public PageResult<WarehouseDO> getWarehousePage(WarehousePageReqVO pageReqVO) {
        return warehouseMapper.selectPage(pageReqVO);
    }

    /**
     * 获取启用状态的仓库列表
     * 
     * 说明：
     * - 只查询状态为"启用"（status=1）的仓库
     * - 不分页，返回完整列表
     * - 主要用于前端下拉框选择
     */
    @Override
    public List<WarehouseDO> getEnableWarehouseList() {
        return warehouseMapper.selectListByStatus(CommonStatusEnum.ENABLE.getStatus());
    }

    /**
     * 校验仓库是否存在
     * 
     * 说明：
     * - 如果仓库不存在，抛出 WAREHOUSE_NOT_EXISTS 异常
     * - 异常会被全局异常处理器捕获，返回统一的错误响应
     */
    @Override
    public void validateWarehouseExists(Long id) {
        if (warehouseMapper.selectById(id) == null) {
            throw exception(WAREHOUSE_NOT_EXISTS);
        }
    }

    /**
     * 批量获取仓库Map
     * 
     * 实现步骤：
     * 1. 如果ID集合为空，直接返回空Map（性能优化）
     * 2. 调用 MyBatis Plus 的 selectBatchIds 批量查询
     * 3. 使用 CollectionUtils.convertMap 转换为 Map
     * 
     * 性能优化：
     * - 使用批量查询代替循环单个查询
     * - 减少数据库访问次数
     * 
     * @return Map<仓库ID, 仓库DO>，如果某个ID不存在，Map中不会包含该键值对
     */
    @Override
    public Map<Long, WarehouseDO> getWarehouseMap(Collection<Long> ids) {
        // 1. 空集合处理
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        
        // 2. 批量查询并转换为Map
        List<WarehouseDO> list = warehouseMapper.selectBatchIds(ids);
        return CollectionUtils.convertMap(list, WarehouseDO::getId);
    }

    /**
     * 校验仓库编码唯一性（私有方法）
     * 
     * 校验逻辑：
     * 1. 根据编码查询数据库
     * 2. 如果不存在同编码的仓库，校验通过
     * 3. 如果存在：
     *    - 新增时（id为null）：抛出编码重复异常
     *    - 更新时（id不为null）：如果是同一个仓库（ID相同），校验通过；否则抛出异常
     * 
     * 注意：
     * - 仓库编码在同一租户内必须唯一
     * - 多租户隔离由 MyBatis Plus 的 TenantLineHandler 自动处理
     *
     * @param id 仓库ID，新增时传null，更新时传实际ID
     * @param warehouseCode 仓库编码
     * @throws com.laby.framework.common.exception.ServiceException 如果编码重复
     */
    private void validateWarehouseCodeUnique(Long id, String warehouseCode) {
        // 1. 根据编码查询
        WarehouseDO warehouse = warehouseMapper.selectByCode(warehouseCode);
        if (warehouse == null) {
            return; // 不存在，校验通过
        }
        
        // 2. 新增时，只要存在就是重复
        if (id == null) {
            throw exception(WAREHOUSE_CODE_DUPLICATE);
        }
        
        // 3. 更新时，如果不是同一个仓库，就是重复
        if (!warehouse.getId().equals(id)) {
            throw exception(WAREHOUSE_CODE_DUPLICATE);
        }
    }

}

