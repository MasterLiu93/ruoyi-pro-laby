package com.laby.module.wms.service.stocktaking;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPlanPageReqVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPlanRespVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPlanSaveReqVO;
import jakarta.validation.Valid;

/**
 * 盘点计划 Service 接口
 *
 * @author laby
 */
public interface StockTakingPlanService {

    /**
     * 创建盘点计划
     *
     * @param createReqVO 创建信息
     * @return 盘点计划ID
     */
    Long createStockTakingPlan(@Valid StockTakingPlanSaveReqVO createReqVO);

    /**
     * 更新盘点计划
     *
     * @param updateReqVO 更新信息
     */
    void updateStockTakingPlan(@Valid StockTakingPlanSaveReqVO updateReqVO);

    /**
     * 删除盘点计划
     *
     * @param id 盘点计划ID
     */
    void deleteStockTakingPlan(Long id);

    /**
     * 获得盘点计划
     *
     * @param id 盘点计划ID
     * @return 盘点计划
     */
    StockTakingPlanRespVO getStockTakingPlan(Long id);

    /**
     * 获得盘点计划分页
     *
     * @param pageReqVO 分页查询
     * @return 盘点计划分页
     */
    PageResult<StockTakingPlanRespVO> getStockTakingPlanPage(StockTakingPlanPageReqVO pageReqVO);

    /**
     * 审核盘点计划
     *
     * @param id 盘点计划ID
     */
    void auditStockTakingPlan(Long id);

    /**
     * 取消盘点计划
     *
     * @param id 盘点计划ID
     */
    void cancelStockTakingPlan(Long id);

}

