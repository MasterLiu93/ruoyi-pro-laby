package com.laby.module.wms.service.inventory;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.inventory.vo.InventoryPageReqVO;
import com.laby.module.wms.controller.admin.inventory.vo.InventorySaveReqVO;
import com.laby.module.wms.dal.dataobject.inventory.InventoryDO;

import jakarta.validation.Valid;

/**
 * 库存信息 Service 接口
 * 
 * 功能说明：
 * - 库存的增删改查
 * - 库存锁定/解锁（预留库存）
 * - 库存调整（盘盈盘亏）
 * - 库存预警查询
 * 
 * 业务规则：
 * - 同一仓库+库位+商品+批次+序列号，只能有一条库存记录（唯一约束）
 * - 库存数量不能为负数
 * - 锁定数量不能大于库存数量
 * - 使用乐观锁防止并发问题
 * 
 * @author laby
 */
public interface InventoryService {

    /**
     * 创建库存
     * 
     * 业务规则：
     * 1. 校验仓库、库位、商品是否存在
     * 2. 校验库存数量和锁定数量合法性
     * 3. 校验唯一性（同一仓库+库位+商品+批次+序列号不能重复）
     * 4. 如果商品需要批次/序列号管理，必须填写对应字段
     * 
     * @param createReqVO 创建信息
     * @return 库存ID
     * @throws com.laby.framework.common.exception.ServiceException 校验失败时抛出异常
     */
    Long createInventory(@Valid InventorySaveReqVO createReqVO);

    /**
     * 更新库存
     * 
     * 业务规则：
     * 1. 校验库存是否存在
     * 2. 校验库存数量和锁定数量合法性
     * 3. 使用乐观锁更新，防止并发问题
     * 4. 不允许修改：仓库ID、商品ID、批次号、序列号
     * 
     * @param updateReqVO 更新信息
     * @throws com.laby.framework.common.exception.ServiceException 校验失败时抛出异常
     */
    void updateInventory(@Valid InventorySaveReqVO updateReqVO);

    /**
     * 删除库存
     * 
     * 业务规则：
     * 1. 校验库存是否存在
     * 2. 只有库存数量=0且锁定数量=0时才允许删除
     * 3. 逻辑删除，不物理删除
     * 
     * @param id 库存ID
     * @throws com.laby.framework.common.exception.ServiceException 校验失败时抛出异常
     */
    void deleteInventory(Long id);

    /**
     * 获得库存详情
     * 
     * @param id 库存ID
     * @return 库存信息（包含关联查询字段）
     */
    InventoryDO getInventory(Long id);

    /**
     * 获得库存分页列表
     * 
     * 说明：
     * - 返回分页结果，包含关联查询字段
     * - 关联查询：仓库名、库位编码、商品名等
     * 
     * 支持的查询条件：
     * - 仓库ID（精确查询，必填）
     * - 库位ID（精确查询，可选）
     * - 商品ID（精确查询，可选）
     * - 商品名称（模糊查询，可选）
     * - SKU编码（模糊查询，可选）
     * - 批次号（精确查询，可选）
     * - 序列号（精确查询，可选）
     * - 状态（精确查询，可选）
     * - 低库存预警（可选）
     * - 创建时间范围（区间查询，可选）
     * 
     * @param pageReqVO 分页查询条件
     * @return 库存分页结果（包含关联字段）
     */
    PageResult<InventoryDO> getInventoryPage(InventoryPageReqVO pageReqVO);

}

