package com.laby.module.wms.service.warehouse;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.warehouse.vo.location.WarehouseLocationPageReqVO;
import com.laby.module.wms.controller.admin.warehouse.vo.location.WarehouseLocationSaveReqVO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseLocationDO;

import jakarta.validation.*;
import java.util.List;
import java.util.Map;

/**
 * 库位 Service 接口
 * 提供库位信息的业务逻辑处理
 * 
 * 功能说明：
 * - 库位是库区的最小存储单元，是实际存储商品的地方
 * - 库位的创建、更新、删除等基础操作
 * - 库位编码必须全局唯一
 * - 库位采用三维坐标定位（排号、列号、层号）
 * - 库位有容量和承重限制
 * - 库位状态管理（空闲、占用、锁定、禁用）
 * - 支持按库区ID查询库位列表（用于前端三级联动）
 * - 支持多租户隔离
 *
 * @author laby
 */
public interface WarehouseLocationService {

    /**
     * 创建库位
     * 
     * 业务规则：
     * - 仓库和库区必须存在
     * - 库位编码必须全局唯一
     * - 同一库区内的排-列-层组合不能重复
     * - 自动设置创建时间和创建人
     * 
     * @param createReqVO 库位创建信息，包括仓库ID、库区ID、编码、类型、坐标等
     * @return 创建成功的库位ID
     */
    Long createWarehouseLocation(@Valid WarehouseLocationSaveReqVO createReqVO);

    /**
     * 更新库位
     * 
     * 业务规则：
     * - 库位必须存在
     * - 库位编码必须全局唯一（排除自己）
     * - 如果库位已有库存，某些字段（如容量、承重）可能受限
     * - 自动更新修改时间和修改人
     * 
     * @param updateReqVO 库位更新信息，必须包含id
     * @throws com.laby.framework.common.exception.ServiceException 如果库位不存在或编码重复
     */
    void updateWarehouseLocation(@Valid WarehouseLocationSaveReqVO updateReqVO);

    /**
     * 删除库位
     * 
     * 业务规则：
     * - 库位必须存在
     * - 库位下不能有库存（需先清空库存）
     * - 逻辑删除，不会真正删除数据
     * 
     * @param id 库位ID
     * @throws com.laby.framework.common.exception.ServiceException 如果库位不存在或有库存
     */
    void deleteWarehouseLocation(Long id);

    /**
     * 批量删除库位
     * 
     * 业务规则：
     * - 所有库位必须存在
     * - 库位下不能有库存
     * - 如果任何一个库位删除失败，整个操作会回滚
     * 
     * @param ids 库位ID列表
     * @throws com.laby.framework.common.exception.ServiceException 如果任何库位删除失败
     */
    void deleteWarehouseLocationList(List<Long> ids);

    /**
     * 获取库位详情
     * 
     * @param id 库位ID
     * @return 库位信息，如果不存在返回null
     */
    WarehouseLocationDO getWarehouseLocation(Long id);

    /**
     * 获取库位分页列表
     * 
     * 支持的查询条件：
     * - 仓库ID（精确查询）
     * - 库区ID（精确查询）
     * - 库位编码（模糊查询）
     * - 库位类型（精确查询）
     * - 排号、列号、层号（精确查询）
     * - 状态（精确查询：空闲、占用、锁定、禁用）
     * - 创建时间范围
     * 
     * @param pageReqVO 分页查询参数
     * @return 库位分页数据
     */
    PageResult<WarehouseLocationDO> getWarehouseLocationPage(WarehouseLocationPageReqVO pageReqVO);

    /**
     * 获取指定库区的所有库位
     * 
     * 说明：
     * - 根据库区ID查询该库区下的所有库位
     * - 只返回启用状态的库位
     * - 主要用于前端"仓库-库区-库位"三级联动下拉框
     * 
     * @param areaId 库区ID
     * @return 该库区下的所有库位列表
     */
    List<WarehouseLocationDO> getWarehouseLocationListByAreaId(Long areaId);

    /**
     * 批量获取库位Map
     * 
     * 说明：
     * - 根据库位ID列表批量查询库位信息
     * - 返回 Map<库位ID, 库位DO>
     * - 主要用于数据关联查询优化（如库存列表关联库位编码）
     * - 如果某个ID对应的库位不存在，Map中不会包含该键值对
     * 
     * @param ids 库位ID列表
     * @return 库位Map，Key为库位ID，Value为库位DO
     */
    Map<Long, WarehouseLocationDO> getWarehouseLocationMap(List<Long> ids);

}
