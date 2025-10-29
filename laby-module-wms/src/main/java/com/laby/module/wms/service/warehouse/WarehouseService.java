package com.laby.module.wms.service.warehouse;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.warehouse.vo.warehouse.WarehousePageReqVO;
import com.laby.module.wms.controller.admin.warehouse.vo.warehouse.WarehouseSaveReqVO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 仓库 Service 接口
 * 提供仓库信息的业务逻辑处理
 * 
 * 功能说明：
 * - 仓库的创建、更新、删除等基础操作
 * - 仓库编码唯一性校验
 * - 仓库关联数据校验（库区、库存等）
 * - 仓库数据查询和批量查询
 * - 支持多租户隔离
 *
 * @author laby
 */
public interface WarehouseService {

    /**
     * 创建仓库
     * 
     * 业务规则：
     * - 仓库编码必须唯一（同一租户内）
     * - 自动设置创建时间和创建人
     * 
     * @param createReqVO 仓库创建信息，包括编码、名称、类型、地址等
     * @return 创建成功的仓库ID
     */
    Long createWarehouse(@Valid WarehouseSaveReqVO createReqVO);

    /**
     * 更新仓库
     * 
     * 业务规则：
     * - 仓库必须存在
     * - 仓库编码必须唯一（同一租户内）
     * - 自动更新修改时间和修改人
     * 
     * @param updateReqVO 仓库更新信息，必须包含id
     * @throws com.laby.framework.common.exception.ServiceException 如果仓库不存在或编码重复
     */
    void updateWarehouse(@Valid WarehouseSaveReqVO updateReqVO);

    /**
     * 删除仓库
     * 
     * 业务规则：
     * - 仓库必须存在
     * - 仓库下不能有库区（需先删除库区）
     * - 仓库下不能有库存（需先清空库存）
     * - 逻辑删除，不会真正删除数据
     * 
     * @param id 仓库ID
     * @throws com.laby.framework.common.exception.ServiceException 如果仓库不存在或有关联数据
     */
    void deleteWarehouse(Long id);

    /**
     * 批量删除仓库
     * 
     * 业务规则：
     * - 所有仓库必须存在
     * - 仓库下不能有库区或库存
     * - 如果任何一个仓库删除失败，整个操作会回滚
     * 
     * @param ids 仓库ID列表
     * @throws com.laby.framework.common.exception.ServiceException 如果任何仓库删除失败
     */
    void deleteWarehouseList(List<Long> ids);

    /**
     * 获取仓库详情
     * 
     * @param id 仓库ID
     * @return 仓库信息，如果不存在返回null
     */
    WarehouseDO getWarehouse(Long id);

    /**
     * 获取仓库分页列表
     * 
     * 支持的查询条件：
     * - 仓库编码（模糊查询）
     * - 仓库名称（模糊查询）
     * - 仓库类型（精确查询）
     * - 省份、城市（精确查询）
     * - 状态（精确查询）
     * - 创建时间范围
     * 
     * @param pageReqVO 分页查询参数
     * @return 仓库分页数据
     */
    PageResult<WarehouseDO> getWarehousePage(WarehousePageReqVO pageReqVO);

    /**
     * 获取启用状态的仓库列表
     * 
     * 说明：
     * - 只返回状态为"启用"的仓库
     * - 不分页，返回完整列表
     * - 主要用于前端下拉框选择
     * 
     * @return 启用的仓库列表
     */
    List<WarehouseDO> getEnableWarehouseList();

    /**
     * 校验仓库是否存在
     * 
     * 说明：
     * - 如果仓库不存在，抛出业务异常
     * - 主要用于其他模块的关联校验
     * 
     * @param id 仓库ID
     * @throws com.laby.framework.common.exception.ServiceException 如果仓库不存在
     */
    void validateWarehouseExists(Long id);

    /**
     * 批量获取仓库Map
     * 
     * 说明：
     * - 根据仓库ID列表批量查询仓库信息
     * - 返回 Map<仓库ID, 仓库DO>
     * - 主要用于数据关联查询优化（如库区列表关联仓库名称）
     * - 如果某个ID对应的仓库不存在，Map中不会包含该键值对
     * 
     * @param ids 仓库ID集合
     * @return 仓库Map，Key为仓库ID，Value为仓库DO
     */
    Map<Long, WarehouseDO> getWarehouseMap(Collection<Long> ids);

}

