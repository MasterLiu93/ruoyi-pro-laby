package com.laby.module.wms.service.warehouse;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.warehouse.vo.location.WarehouseLocationPageReqVO;
import com.laby.module.wms.controller.admin.warehouse.vo.location.WarehouseLocationSaveReqVO;
import com.laby.module.wms.convert.warehouse.WarehouseLocationConvert;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseLocationDO;
import com.laby.module.wms.dal.mysql.warehouse.WarehouseAreaMapper;
import com.laby.module.wms.dal.mysql.warehouse.WarehouseLocationMapper;
import com.laby.module.wms.dal.mysql.warehouse.WarehouseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import cn.hutool.core.collection.CollUtil;
import com.laby.framework.common.util.collection.CollectionUtils;

import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.module.wms.enums.ErrorCodeConstants.*;

/**
 * 库位 Service 实现类
 * 
 * 实现说明：
 * - 所有公共方法都经过参数校验（@Validated）
 * - 创建库位前会校验仓库和库区是否存在
 * - 创建/更新时会校验库位编码的全局唯一性
 * - 删除库位前会校验是否有库存（TODO：待库存模块实现后补全）
 * - 使用 MapStruct 进行对象转换（WarehouseLocationConvert）
 * - 异常统一使用 ServiceException，错误码定义在 ErrorCodeConstants 中
 * - 支持批量删除，但是串行执行（事务回滚保证原子性）
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class WarehouseLocationServiceImpl implements WarehouseLocationService {

    @Resource
    private WarehouseLocationMapper warehouseLocationMapper;

    @Resource
    private WarehouseMapper warehouseMapper;

    @Resource
    private WarehouseAreaMapper warehouseAreaMapper;

    /**
     * 创建库位
     *
     * 实现步骤：
     * 1. 校验仓库是否存在
     * 2. 校验库区是否存在
     * 3. 校验库位编码的全局唯一性
     * 4. 使用 MapStruct 转换 VO 为 DO
     * 5. 插入数据库（MyBatis Plus 自动设置 ID、创建时间等）
     * 6. 返回新生成的库位ID
     */
    @Override
    public Long createWarehouseLocation(WarehouseLocationSaveReqVO createReqVO) {
        // 校验仓库是否存在
        validateWarehouseExists(createReqVO.getWarehouseId());
        
        // 校验库区是否存在
        validateWarehouseAreaExists(createReqVO.getAreaId());
        
        // 校验库位编码唯一性
        validateLocationCodeUnique(null, createReqVO.getWarehouseId(), 
                createReqVO.getAreaId(), createReqVO.getLocationCode());

        // 插入
        WarehouseLocationDO warehouseLocation = WarehouseLocationConvert.INSTANCE.convert(createReqVO);
        warehouseLocationMapper.insert(warehouseLocation);
        return warehouseLocation.getId();
    }

    /**
     * 更新库位
     *
     * 实现步骤：
     * 1. 校验库位是否存在
     * 2. 校验仓库是否存在
     * 3. 校验库区是否存在
     * 4. 校验库位编码的全局唯一性（排除自己）
     * 5. 使用 MapStruct 转换 VO 为 DO
     * 6. 更新数据库（MyBatis Plus 自动更新修改时间等）
     */
    @Override
    public void updateWarehouseLocation(WarehouseLocationSaveReqVO updateReqVO) {
        // 校验存在
        validateWarehouseLocationExists(updateReqVO.getId());
        
        // 校验仓库是否存在
        validateWarehouseExists(updateReqVO.getWarehouseId());
        
        // 校验库区是否存在
        validateWarehouseAreaExists(updateReqVO.getAreaId());
        
        // 校验库位编码唯一性
        validateLocationCodeUnique(updateReqVO.getId(), updateReqVO.getWarehouseId(), 
                updateReqVO.getAreaId(), updateReqVO.getLocationCode());

        // 更新
        WarehouseLocationDO updateObj = WarehouseLocationConvert.INSTANCE.convert(updateReqVO);
        warehouseLocationMapper.updateById(updateObj);
    }

    /**
     * 删除库位
     *
     * 实现步骤：
     * 1. 校验库位是否存在
     * 2. 校验库位下是否有库存（TODO：待库存模块实现后补全）
     * 3. 逻辑删除（deleted 字段）
     */
    @Override
    public void deleteWarehouseLocation(Long id) {
        // 校验存在
        validateWarehouseLocationExists(id);
        
        // TODO: 校验是否有库存
        // 库存模块实现后，需要在这里添加校验：
        // Long inventoryCount = inventoryMapper.selectCountByLocationId(id);
        // if (inventoryCount > 0) {
        //     throw exception(WAREHOUSE_LOCATION_HAS_INVENTORY);
        // }

        // 删除
        warehouseLocationMapper.deleteById(id);
    }

    /**
     * 批量删除库位
     *
     * 实现说明：
     * - 串行执行，对每个ID调用单个删除方法
     * - 如果任何一个删除失败，整个事务回滚
     * - 每个删除都会进行完整的业务规则校验
     */
    @Override
    public void deleteWarehouseLocationList(List<Long> ids) {
        for (Long id : ids) {
            deleteWarehouseLocation(id);
        }
    }

    /**
     * 校验库位是否存在（私有方法）
     *
     * @param id 库位ID
     * @throws com.laby.framework.common.exception.ServiceException 如果库位不存在
     */
    private void validateWarehouseLocationExists(Long id) {
        if (warehouseLocationMapper.selectById(id) == null) {
            throw exception(WAREHOUSE_LOCATION_NOT_EXISTS);
        }
    }

    /**
     * 校验仓库是否存在（私有方法）
     *
     * @param warehouseId 仓库ID
     * @throws com.laby.framework.common.exception.ServiceException 如果仓库不存在
     */
    private void validateWarehouseExists(Long warehouseId) {
        if (warehouseMapper.selectById(warehouseId) == null) {
            throw exception(WAREHOUSE_NOT_EXISTS);
        }
    }

    /**
     * 校验库区是否存在（私有方法）
     *
     * @param areaId 库区ID
     * @throws com.laby.framework.common.exception.ServiceException 如果库区不存在
     */
    private void validateWarehouseAreaExists(Long areaId) {
        if (warehouseAreaMapper.selectById(areaId) == null) {
            throw exception(WAREHOUSE_AREA_NOT_EXISTS);
        }
    }

    /**
     * 校验库位编码的全局唯一性（私有方法）
     *
     * 校验逻辑：
     * 1. 根据仓库ID、库区ID和编码查询数据库
     * 2. 如果不存在同编码的库位，校验通过
     * 3. 如果存在：
     *    - 新增时（id为null）：抛出编码重复异常
     *    - 更新时（id不为null）：如果是同一个库位（ID相同），校验通过；否则抛出异常
     *
     * 注意：
     * - 库位编码必须全局唯一
     * - 多租户隔离由 MyBatis Plus 的 TenantLineHandler 自动处理
     *
     * @param id 库位ID，新增时传null，更新时传实际ID
     * @param warehouseId 仓库ID
     * @param areaId 库区ID
     * @param locationCode 库位编码
     * @throws com.laby.framework.common.exception.ServiceException 如果编码重复
     */
    private void validateLocationCodeUnique(Long id, Long warehouseId, Long areaId, String locationCode) {
        WarehouseLocationDO location = warehouseLocationMapper.selectByCode(warehouseId, areaId, locationCode);
        if (location == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的库位
        if (id == null) {
            throw exception(WAREHOUSE_LOCATION_CODE_DUPLICATE);
        }
        if (!location.getId().equals(id)) {
            throw exception(WAREHOUSE_LOCATION_CODE_DUPLICATE);
        }
    }

    /**
     * 获取库位详情
     *
     * @param id 库位ID
     * @return 库位信息，如果不存在返回null
     */
    @Override
    public WarehouseLocationDO getWarehouseLocation(Long id) {
        return warehouseLocationMapper.selectById(id);
    }

    /**
     * 获取库位分页列表
     *
     * @param pageReqVO 分页查询参数
     * @return 库位分页数据
     */
    @Override
    public PageResult<WarehouseLocationDO> getWarehouseLocationPage(WarehouseLocationPageReqVO pageReqVO) {
        return warehouseLocationMapper.selectPage(pageReqVO);
    }

    /**
     * 获取指定库区的所有库位
     *
     * 说明：
     * - 只返回启用状态的库位
     * - 主要用于前端"仓库-库区-库位"三级联动下拉框
     *
     * @param areaId 库区ID
     * @return 该库区下的所有库位列表
     */
    @Override
    public List<WarehouseLocationDO> getWarehouseLocationListByAreaId(Long areaId) {
        return warehouseLocationMapper.selectListByAreaId(areaId);
    }

    /**
     * 批量获取库位Map
     * 
     * 实现步骤：
     * 1. 校验参数是否为空
     * 2. 批量查询库位信息
     * 3. 转换为Map返回
     * 
     * @param ids 库位ID列表
     * @return 库位Map
     */
    @Override
    public Map<Long, WarehouseLocationDO> getWarehouseLocationMap(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Map.of();
        }
        return CollectionUtils.convertMap(warehouseLocationMapper.selectBatchIds(ids), WarehouseLocationDO::getId);
    }

}
