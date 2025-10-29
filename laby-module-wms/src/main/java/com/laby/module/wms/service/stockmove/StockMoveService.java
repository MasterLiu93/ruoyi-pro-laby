package com.laby.module.wms.service.stockmove;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.stockmove.vo.StockMovePageReqVO;
import com.laby.module.wms.controller.admin.stockmove.vo.StockMoveRespVO;
import com.laby.module.wms.controller.admin.stockmove.vo.StockMoveSaveReqVO;
import jakarta.validation.Valid;

/**
 * 移库管理 Service 接口
 *
 * @author laby
 */
public interface StockMoveService {

    /**
     * 创建移库单
     *
     * @param createReqVO 创建信息
     * @return 移库单ID
     */
    Long createStockMove(@Valid StockMoveSaveReqVO createReqVO);

    /**
     * 更新移库单
     *
     * @param updateReqVO 更新信息
     */
    void updateStockMove(@Valid StockMoveSaveReqVO updateReqVO);

    /**
     * 删除移库单
     *
     * @param id 移库单ID
     */
    void deleteStockMove(Long id);

    /**
     * 获得移库单
     *
     * @param id 移库单ID
     * @return 移库单
     */
    StockMoveRespVO getStockMove(Long id);

    /**
     * 获得移库单分页
     *
     * @param pageReqVO 分页查询
     * @return 移库单分页
     */
    PageResult<StockMoveRespVO> getStockMovePage(StockMovePageReqVO pageReqVO);

    /**
     * 执行移库
     *
     * @param id 移库单ID
     */
    void executeStockMove(Long id);

    /**
     * 完成移库
     *
     * @param id 移库单ID
     */
    void completeStockMove(Long id);

    /**
     * 取消移库
     *
     * @param id 移库单ID
     */
    void cancelStockMove(Long id);

}

