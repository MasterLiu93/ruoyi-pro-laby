package com.laby.module.wms.service.stocktaking;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPageReqVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingRespVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingSaveReqVO;
import jakarta.validation.Valid;

import java.math.BigDecimal;

/**
 * 盘点单 Service 接口
 *
 * @author laby
 */
public interface StockTakingService {

    /**
     * 创建盘点单
     *
     * @param createReqVO 创建信息
     * @return 盘点单ID
     */
    Long createStockTaking(@Valid StockTakingSaveReqVO createReqVO);

    /**
     * 更新盘点单
     *
     * @param updateReqVO 更新信息
     */
    void updateStockTaking(@Valid StockTakingSaveReqVO updateReqVO);

    /**
     * 删除盘点单
     *
     * @param id 盘点单ID
     */
    void deleteStockTaking(Long id);

    /**
     * 获得盘点单
     *
     * @param id 盘点单ID
     * @return 盘点单
     */
    StockTakingRespVO getStockTaking(Long id);

    /**
     * 获得盘点单分页
     *
     * @param pageReqVO 分页查询
     * @return 盘点单分页
     */
    PageResult<StockTakingRespVO> getStockTakingPage(StockTakingPageReqVO pageReqVO);

    /**
     * 提交盘点（填写实盘数量）
     *
     * @param id 盘点单ID
     * @param actualQuantity 实盘数量
     * @param diffReason 差异原因
     */
    void submitStockTaking(Long id, BigDecimal actualQuantity, String diffReason);

    /**
     * 复核盘点
     *
     * @param id 盘点单ID
     */
    void reviewStockTaking(Long id);

    /**
     * 调整库存（根据盘点结果调整库存）
     *
     * @param id 盘点单ID
     */
    void adjustStockTaking(Long id);

}

