package com.laby.module.wms.dal.mysql.inventory;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.inventory.vo.snapshot.InventorySnapshotPageReqVO;
import com.laby.module.wms.dal.dataobject.inventory.InventorySnapshotDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 库存快照 Mapper
 * 
 * 功能说明：
 * - 提供库存快照的数据访问功能
 * - 支持按日期范围查询
 * - 支持按仓库、商品查询
 * - 支持库存趋势分析数据查询
 * 
 * 查询场景：
 * 1. 按仓库、商品查询快照
 * 2. 按日期范围查询快照
 * 3. 查询某个时间点的库存状态
 * 4. 库存趋势分析（获取一段时间内的快照数据）
 *
 * @author laby
 */
@Mapper
public interface InventorySnapshotMapper extends BaseMapperX<InventorySnapshotDO> {

    /**
     * 分页查询库存快照
     * 
     * 支持的查询条件：
     * - 快照日期范围
     * - 仓库ID（精确）
     * - 商品ID（精确）
     * 
     * 排序规则：按快照日期倒序
     *
     * @param reqVO 查询条件
     * @return 分页结果（仅DO，不包含关联字段）
     */
    default PageResult<InventorySnapshotDO> selectPage(InventorySnapshotPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<InventorySnapshotDO>()
                .betweenIfPresent(InventorySnapshotDO::getSnapshotDate, reqVO.getSnapshotDateRange())
                .eqIfPresent(InventorySnapshotDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(InventorySnapshotDO::getGoodsId, reqVO.getGoodsId())
                .orderByDesc(InventorySnapshotDO::getSnapshotDate)
                .orderByDesc(InventorySnapshotDO::getId));
    }

    /**
     * 查询库存趋势数据（不分页）
     * 
     * 用途：用于生成库存趋势图表
     * 
     * 支持的查询条件：
     * - 快照日期范围（必填）
     * - 仓库ID（可选）
     * - 商品ID（可选）
     * 
     * 排序规则：按快照日期升序
     *
     * @param reqVO 查询条件
     * @return 快照数据列表
     */
    default List<InventorySnapshotDO> selectTrendList(InventorySnapshotPageReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<InventorySnapshotDO>()
                .betweenIfPresent(InventorySnapshotDO::getSnapshotDate, reqVO.getSnapshotDateRange())
                .eqIfPresent(InventorySnapshotDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(InventorySnapshotDO::getGoodsId, reqVO.getGoodsId())
                .orderByAsc(InventorySnapshotDO::getSnapshotDate));
    }

}

