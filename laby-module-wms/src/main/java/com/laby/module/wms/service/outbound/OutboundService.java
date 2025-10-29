package com.laby.module.wms.service.outbound;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundPageReqVO;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundRespVO;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundSaveReqVO;
import jakarta.validation.Valid;

/**
 * WMS出库单 Service接口
 *
 * @author laby
 */
public interface OutboundService {

    /**
     * 创建出库单
     *
     * @param createReqVO 创建信息
     * @return 出库单ID
     */
    Long createOutbound(@Valid OutboundSaveReqVO createReqVO);

    /**
     * 更新出库单
     *
     * @param updateReqVO 更新信息
     */
    void updateOutbound(@Valid OutboundSaveReqVO updateReqVO);

    /**
     * 删除出库单
     *
     * @param id 出库单ID
     */
    void deleteOutbound(Long id);

    /**
     * 获取出库单详情
     *
     * @param id 出库单ID
     * @return 出库单详情
     */
    OutboundRespVO getOutbound(Long id);

    /**
     * 分页查询出库单列表
     *
     * @param pageReqVO 分页查询条件
     * @return 出库单分页结果
     */
    PageResult<OutboundRespVO> getOutboundPage(OutboundPageReqVO pageReqVO);

    /**
     * 审核出库单
     *
     * @param id 出库单ID
     * @param auditBy 审核人ID
     * @param auditByName 审核人名称
     */
    void auditOutbound(Long id, Long auditBy, String auditByName);

    /**
     * 开始拣货
     *
     * @param id 出库单ID
     */
    void startPicking(Long id);

    /**
     * 完成拣货
     *
     * @param id 出库单ID
     * @param itemId 明细ID
     * @param pickedQuantity 拣货数量
     */
    void completePicking(Long id, Long itemId, java.math.BigDecimal pickedQuantity);

    /**
     * 发货
     *
     * @param id 出库单ID
     * @param completeBy 操作人ID
     * @param completeByName 操作人名称
     */
    void shipOutbound(Long id, Long completeBy, String completeByName);

    /**
     * 取消出库单
     *
     * @param id 出库单ID
     */
    void cancelOutbound(Long id);

    /**
     * 更新出库单的拣货数量
     * 
     * 业务说明：
     * - 拣货任务完成后调用此方法更新出库单的已拣货数量
     * - 累加拣货数量
     * - 检查所有拣货任务是否完成，如果完成则更新状态为"待发货"
     *
     * @param outboundId 出库单ID
     * @param pickedQuantity 本次拣货数量
     */
    void updatePickedQuantity(Long outboundId, java.math.BigDecimal pickedQuantity);

    /**
     * 检查并更新出库单拣货状态
     * 
     * 业务说明：
     * - 检查该出库单的所有拣货任务是否全部完成
     * - 如果全部完成，更新出库单状态为"待发货"（状态4）
     * - 如果还有未完成的任务，更新状态为"拣货中"（状态3）
     *
     * @param outboundId 出库单ID
     */
    void checkAndUpdatePickingStatus(Long outboundId);

}

