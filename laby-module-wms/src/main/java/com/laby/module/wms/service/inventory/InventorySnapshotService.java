package com.laby.module.wms.service.inventory;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.inventory.vo.snapshot.InventorySnapshotPageReqVO;
import com.laby.module.wms.controller.admin.inventory.vo.snapshot.InventorySnapshotRespVO;

import java.util.List;

/**
 * 库存快照 Service 接口
 * 
 * 功能说明：
 * - 提供库存快照的查询功能
 * - 支持库存趋势分析
 * - 用于历史库存查询
 * 
 * 业务场景：
 * 1. 库存快照查询
 * 2. 库存趋势图表
 * 3. 库存周转率统计
 * 4. 历史库存对比
 *
 * @author laby
 */
public interface InventorySnapshotService {

    /**
     * 获得库存快照分页列表
     * 
     * 说明：
     * - 查询库存快照记录
     * - 包含关联的仓库名、商品名
     * - 计算可用数量
     * - 按快照日期倒序排列
     * 
     * @param pageReqVO 分页查询条件
     * @return 分页列表（每项包含关联字段）
     */
    PageResult<InventorySnapshotRespVO> getInventorySnapshotPage(InventorySnapshotPageReqVO pageReqVO);

    /**
     * 获得库存趋势数据（不分页）
     * 
     * 说明：
     * - 用于生成库存趋势图表
     * - 按快照日期升序排列
     * - 返回指定时间范围内的所有快照
     * 
     * @param reqVO 查询条件（必须包含日期范围）
     * @return 快照数据列表
     */
    List<InventorySnapshotRespVO> getInventoryTrendList(InventorySnapshotPageReqVO reqVO);

}

