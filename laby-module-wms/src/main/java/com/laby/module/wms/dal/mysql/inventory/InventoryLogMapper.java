package com.laby.module.wms.dal.mysql.inventory;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.inventory.vo.log.InventoryLogPageReqVO;
import com.laby.module.wms.dal.dataobject.inventory.InventoryLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存流水 Mapper
 * 
 * 功能说明：
 * - 提供库存流水的数据访问功能
 * - 支持分页查询、条件查询
 * - 支持按时间范围、操作类型、业务类型查询
 * 
 * 查询场景：
 * 1. 按仓库、商品查询流水
 * 2. 按批次号查询流水
 * 3. 按操作类型过滤（入库、出库等）
 * 4. 按业务单号追溯
 * 5. 按时间范围统计
 *
 * @author laby
 */
@Mapper
public interface InventoryLogMapper extends BaseMapperX<InventoryLogDO> {

    /**
     * 分页查询库存流水
     * 
     * 支持的查询条件：
     * - 仓库ID（精确）
     * - 商品ID（精确）
     * - 库位ID（精确）
     * - 批次号（模糊）
     * - 操作类型（精确）
     * - 业务类型（精确）
     * - 业务单号（模糊）
     * - 创建时间范围
     * 
     * 排序规则：按创建时间倒序
     *
     * @param reqVO 查询条件
     * @return 分页结果（仅DO，不包含关联字段）
     */
    default PageResult<InventoryLogDO> selectPage(InventoryLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<InventoryLogDO>()
                .eqIfPresent(InventoryLogDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(InventoryLogDO::getGoodsId, reqVO.getGoodsId())
                .eqIfPresent(InventoryLogDO::getLocationId, reqVO.getLocationId())
                .likeIfPresent(InventoryLogDO::getBatchNo, reqVO.getBatchNo())
                .eqIfPresent(InventoryLogDO::getOperationType, reqVO.getOperationType())
                .eqIfPresent(InventoryLogDO::getBusinessType, reqVO.getBusinessType())
                .likeIfPresent(InventoryLogDO::getBusinessNo, reqVO.getBusinessNo())
                .betweenIfPresent(InventoryLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(InventoryLogDO::getId));
    }

}

