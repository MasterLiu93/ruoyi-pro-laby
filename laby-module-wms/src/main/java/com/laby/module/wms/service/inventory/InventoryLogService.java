package com.laby.module.wms.service.inventory;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.inventory.vo.log.InventoryLogPageReqVO;
import com.laby.module.wms.controller.admin.inventory.vo.log.InventoryLogRespVO;

/**
 * 库存流水 Service 接口
 * 
 * 功能说明：
 * - 提供库存流水的查询功能
 * - 记录库存变动日志
 * - 支持业务追溯和审计
 * 
 * 业务场景：
 * 1. 查询库存变动历史
 * 2. 追溯业务单据
 * 3. 库存审计报表
 * 4. 异常库存分析
 *
 * @author laby
 */
public interface InventoryLogService {

    /**
     * 获得库存流水分页列表
     * 
     * 说明：
     * - 查询库存流水记录
     * - 包含关联的仓库名、商品名、库位编码
     * - 支持多条件过滤
     * - 按创建时间倒序排列
     * 
     * @param pageReqVO 分页查询条件
     * @return 分页列表（每项包含关联字段）
     */
    PageResult<InventoryLogRespVO> getInventoryLogPage(InventoryLogPageReqVO pageReqVO);

}

