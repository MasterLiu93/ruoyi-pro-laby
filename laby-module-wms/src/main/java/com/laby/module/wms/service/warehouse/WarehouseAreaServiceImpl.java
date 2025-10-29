package com.laby.module.wms.service.warehouse;

import cn.hutool.core.collection.CollUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.module.wms.controller.admin.warehouse.vo.area.WarehouseAreaPageReqVO;
import com.laby.module.wms.controller.admin.warehouse.vo.area.WarehouseAreaSaveReqVO;
import com.laby.module.wms.convert.warehouse.WarehouseAreaConvert;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseAreaDO;
import com.laby.module.wms.dal.mysql.warehouse.WarehouseAreaMapper;
import com.laby.module.wms.dal.mysql.warehouse.WarehouseLocationMapper;
import com.laby.module.wms.dal.mysql.warehouse.WarehouseMapper;
import lombok.extern.slf4j.Slf4j;
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
 * 库区 Service 实现类
 * 
 * 实现说明：
 * - 所有公共方法都经过参数校验（@Validated）
 * - 创建库区前会校验仓库是否存在
 * - 创建/更新时会校验库区编码在同一仓库内的唯一性
 * - 删除库区前会校验是否有关联的库位
 * - 使用 MapStruct 进行对象转换（WarehouseAreaConvert）
 * - 异常统一使用 ServiceException，错误码定义在 ErrorCodeConstants 中
 * - 支持批量删除，但是串行执行（事务回滚保证原子性）
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class WarehouseAreaServiceImpl implements WarehouseAreaService {

    @Resource
    private WarehouseAreaMapper warehouseAreaMapper;

    @Resource
    private WarehouseMapper warehouseMapper;

    @Resource
    private WarehouseLocationMapper warehouseLocationMapper;

    /**
     * 创建库区
     *
     * 实现步骤：
     * 1. 校验仓库是否存在
     * 2. 校验库区编码在同一仓库内的唯一性
     * 3. 使用 MapStruct 转换 VO 为 DO
     * 4. 插入数据库（MyBatis Plus 自动设置 ID、创建时间等）
     * 5. 返回新生成的库区ID
     */
    @Override
    public Long createWarehouseArea(WarehouseAreaSaveReqVO createReqVO) {
        // 校验仓库是否存在
        validateWarehouseExists(createReqVO.getWarehouseId());
        
        // 校验库区编码唯一性
        validateAreaCodeUnique(null, createReqVO.getWarehouseId(), createReqVO.getAreaCode());

        // 插入
        WarehouseAreaDO warehouseArea = WarehouseAreaConvert.INSTANCE.convert(createReqVO);
        warehouseAreaMapper.insert(warehouseArea);
        return warehouseArea.getId();
    }

    /**
     * 更新库区
     *
     * 实现步骤：
     * 1. 校验库区是否存在
     * 2. 校验仓库是否存在
     * 3. 校验库区编码在同一仓库内的唯一性（排除自己）
     * 4. 使用 MapStruct 转换 VO 为 DO
     * 5. 更新数据库（MyBatis Plus 自动更新修改时间等）
     */
    @Override
    public void updateWarehouseArea(WarehouseAreaSaveReqVO updateReqVO) {
        // 校验存在
        validateWarehouseAreaExists(updateReqVO.getId());
        
        // 校验仓库是否存在
        validateWarehouseExists(updateReqVO.getWarehouseId());
        
        // 校验库区编码唯一性
        validateAreaCodeUnique(updateReqVO.getId(), updateReqVO.getWarehouseId(), updateReqVO.getAreaCode());

        // 更新
        WarehouseAreaDO updateObj = WarehouseAreaConvert.INSTANCE.convert(updateReqVO);
        warehouseAreaMapper.updateById(updateObj);
    }

    /**
     * 删除库区
     *
     * 实现步骤：
     * 1. 校验库区是否存在
     * 2. 校验库区下是否有库位（如果有，不允许删除）
     * 3. 逻辑删除（deleted 字段）
     */
    @Override
    public void deleteWarehouseArea(Long id) {
        // 校验存在
        validateWarehouseAreaExists(id);
        
        // 校验是否有库位
        Long locationCount = warehouseLocationMapper.selectCountByAreaId(id);
        if (locationCount > 0) {
            throw exception(WAREHOUSE_AREA_HAS_LOCATION);
        }

        // 删除
        warehouseAreaMapper.deleteById(id);
    }

    /**
     * 批量删除库区
     *
     * 实现说明：
     * - 串行执行，对每个ID调用单个删除方法
     * - 如果任何一个删除失败，整个事务回滚
     * - 每个删除都会进行完整的业务规则校验
     */
    @Override
    public void deleteWarehouseAreaList(List<Long> ids) {
        for (Long id : ids) {
            deleteWarehouseArea(id);
        }
    }

    /**
     * 校验库区是否存在（私有方法）
     *
     * @param id 库区ID
     * @throws com.laby.framework.common.exception.ServiceException 如果库区不存在
     */
    private void validateWarehouseAreaExists(Long id) {
        if (warehouseAreaMapper.selectById(id) == null) {
            throw exception(WAREHOUSE_AREA_NOT_EXISTS);
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
     * 校验库区编码在同一仓库内的唯一性（私有方法）
     *
     * 校验逻辑：
     * 1. 根据仓库ID和编码查询数据库
     * 2. 如果不存在同编码的库区，校验通过
     * 3. 如果存在：
     *    - 新增时（id为null）：抛出编码重复异常
     *    - 更新时（id不为null）：如果是同一个库区（ID相同），校验通过；否则抛出异常
     *
     * 注意：
     * - 库区编码在同一仓库内必须唯一
     * - 多租户隔离由 MyBatis Plus 的 TenantLineHandler 自动处理
     *
     * @param id 库区ID，新增时传null，更新时传实际ID
     * @param warehouseId 仓库ID
     * @param areaCode 库区编码
     * @throws com.laby.framework.common.exception.ServiceException 如果编码重复
     */
    private void validateAreaCodeUnique(Long id, Long warehouseId, String areaCode) {
        WarehouseAreaDO area = warehouseAreaMapper.selectByCode(warehouseId, areaCode);
        if (area == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的库区
        if (id == null) {
            throw exception(WAREHOUSE_AREA_CODE_DUPLICATE);
        }
        if (!area.getId().equals(id)) {
            throw exception(WAREHOUSE_AREA_CODE_DUPLICATE);
        }
    }

    /**
     * 获取库区详情
     *
     * @param id 库区ID
     * @return 库区信息，如果不存在返回null
     */
    @Override
    public WarehouseAreaDO getWarehouseArea(Long id) {
        return warehouseAreaMapper.selectById(id);
    }

    /**
     * 获取库区分页列表
     *
     * @param pageReqVO 分页查询参数
     * @return 库区分页数据
     */
    @Override
    public PageResult<WarehouseAreaDO> getWarehouseAreaPage(WarehouseAreaPageReqVO pageReqVO) {
        return warehouseAreaMapper.selectPage(pageReqVO);
    }

    /**
     * 获取指定仓库的所有库区
     *
     * 说明：
     * - 只返回启用状态的库区
     * - 主要用于前端"仓库-库区"联动下拉框
     *
     * @param warehouseId 仓库ID
     * @return 该仓库下的所有库区列表
     */
    @Override
    public List<WarehouseAreaDO> getWarehouseAreaListByWarehouseId(Long warehouseId) {
        return warehouseAreaMapper.selectListByWarehouseId(warehouseId);
    }

    /**
     * 批量获取库区Map
     *
     * 实现说明：
     * - 如果ID集合为空，返回空Map
     * - 使用 MyBatis Plus 的 selectBatchIds 批量查询
     * - 使用 CollectionUtils.convertMap 转换为 Map
     * - 主要用于数据关联查询优化（如库位列表关联库区名称）
     *
     * @param ids 库区ID集合
     * @return 库区Map，Key为库区ID，Value为库区DO
     */
    @Override
    public Map<Long, WarehouseAreaDO> getWarehouseAreaMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        List<WarehouseAreaDO> list = warehouseAreaMapper.selectBatchIds(ids);
        return CollectionUtils.convertMap(list, WarehouseAreaDO::getId);
    }

}

