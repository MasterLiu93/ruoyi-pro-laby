package com.laby.module.wms.service.warehouse;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.warehouse.vo.area.WarehouseAreaPageReqVO;
import com.laby.module.wms.controller.admin.warehouse.vo.area.WarehouseAreaSaveReqVO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseAreaDO;

import jakarta.validation.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 库区 Service 接口
 * 提供库区信息的业务逻辑处理
 * 
 * 功能说明：
 * - 库区是仓库的子区域划分，一个仓库可包含多个库区
 * - 库区的创建、更新、删除等基础操作
 * - 库区编码在同一仓库内必须唯一
 * - 库区关联数据校验（库位、库存等）
 * - 支持按仓库ID查询库区列表（用于前端联动下拉框）
 * - 支持批量查询优化（getWarehouseAreaMap）
 * - 支持多租户隔离
 *
 * @author laby
 */
public interface WarehouseAreaService {

    /**
     * 创建库区
     * 
     * 业务规则：
     * - 仓库必须存在
     * - 库区编码在同一仓库内必须唯一
     * - 自动设置创建时间和创建人
     * 
     * @param createReqVO 库区创建信息，包括仓库ID、编码、名称、类型等
     * @return 创建成功的库区ID
     */
    Long createWarehouseArea(@Valid WarehouseAreaSaveReqVO createReqVO);

    /**
     * 更新库区
     * 
     * 业务规则：
     * - 库区必须存在
     * - 库区编码在同一仓库内必须唯一（排除自己）
     * - 自动更新修改时间和修改人
     * 
     * @param updateReqVO 库区更新信息，必须包含id
     * @throws com.laby.framework.common.exception.ServiceException 如果库区不存在或编码重复
     */
    void updateWarehouseArea(@Valid WarehouseAreaSaveReqVO updateReqVO);

    /**
     * 删除库区
     * 
     * 业务规则：
     * - 库区必须存在
     * - 库区下不能有库位（需先删除库位）
     * - 逻辑删除，不会真正删除数据
     * 
     * @param id 库区ID
     * @throws com.laby.framework.common.exception.ServiceException 如果库区不存在或有关联数据
     */
    void deleteWarehouseArea(Long id);

    /**
     * 批量删除库区
     * 
     * 业务规则：
     * - 所有库区必须存在
     * - 库区下不能有库位
     * - 如果任何一个库区删除失败，整个操作会回滚
     * 
     * @param ids 库区ID列表
     * @throws com.laby.framework.common.exception.ServiceException 如果任何库区删除失败
     */
    void deleteWarehouseAreaList(List<Long> ids);

    /**
     * 获取库区详情
     * 
     * @param id 库区ID
     * @return 库区信息，如果不存在返回null
     */
    WarehouseAreaDO getWarehouseArea(Long id);

    /**
     * 获取库区分页列表
     * 
     * 支持的查询条件：
     * - 仓库ID（精确查询）
     * - 库区编码（模糊查询）
     * - 库区名称（模糊查询）
     * - 库区类型（精确查询）
     * - 楼层（精确查询）
     * - 状态（精确查询）
     * - 创建时间范围
     * 
     * @param pageReqVO 分页查询参数
     * @return 库区分页数据
     */
    PageResult<WarehouseAreaDO> getWarehouseAreaPage(WarehouseAreaPageReqVO pageReqVO);

    /**
     * 获取指定仓库的所有库区
     * 
     * 说明：
     * - 根据仓库ID查询该仓库下的所有库区
     * - 只返回启用状态的库区
     * - 主要用于前端"仓库-库区"联动下拉框
     * 
     * @param warehouseId 仓库ID
     * @return 该仓库下的所有库区列表
     */
    List<WarehouseAreaDO> getWarehouseAreaListByWarehouseId(Long warehouseId);

    /**
     * 批量获取库区Map
     * 
     * 说明：
     * - 根据库区ID列表批量查询库区信息
     * - 返回 Map<库区ID, 库区DO>
     * - 主要用于数据关联查询优化（如库位列表关联库区名称）
     * - 如果某个ID对应的库区不存在，Map中不会包含该键值对
     * 
     * @param ids 库区ID集合
     * @return 库区Map，Key为库区ID，Value为库区DO
     */
    Map<Long, WarehouseAreaDO> getWarehouseAreaMap(Collection<Long> ids);

}
