package com.laby.module.wms.service.inbound;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.inbound.vo.InboundPageReqVO;
import com.laby.module.wms.controller.admin.inbound.vo.InboundRespVO;
import com.laby.module.wms.controller.admin.inbound.vo.InboundSaveReqVO;
import com.laby.module.wms.dal.dataobject.inbound.InboundDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 入库单 Service 接口
 *
 * 业务功能：
 * 1. 入库单的CRUD操作
 * 2. 入库单状态流转（审核、收货、完成、取消）
 * 3. 入库单明细管理
 * 4. 库存更新（收货时）
 *
 * @author laby
 */
public interface InboundService {

    /**
     * 创建入库单
     *
     * 业务规则：
     * - 自动生成入库单号
     * - 初始状态为"待审核"
     * - 必须包含至少一条明细
     * - 采购入库必须填写供应商
     *
     * @param createReqVO 创建信息
     * @return 入库单ID
     */
    Long createInbound(@Valid InboundSaveReqVO createReqVO);

    /**
     * 更新入库单
     *
     * 业务规则：
     * - 只有"待审核"状态的入库单可以修改
     * - 修改时会删除旧明细并新增新明细
     *
     * @param updateReqVO 更新信息
     */
    void updateInbound(@Valid InboundSaveReqVO updateReqVO);

    /**
     * 删除入库单
     *
     * 业务规则：
     * - 只有"待审核"状态的入库单可以删除
     * - 删除入库单时会同时删除明细
     *
     * @param id 入库单ID
     */
    void deleteInbound(Long id);

    /**
     * 获得入库单详情
     *
     * @param id 入库单ID
     * @return 入库单详情（包含明细和关联字段）
     */
    InboundRespVO getInbound(Long id);

    /**
     * 获得入库单分页列表
     *
     * @param pageReqVO 分页查询条件
     * @return 分页列表（包含关联字段）
     */
    PageResult<InboundRespVO> getInboundPage(InboundPageReqVO pageReqVO);

    /**
     * 审核入库单
     *
     * 业务规则：
     * - 只有"待审核"状态可以审核
     * - 审核通过后状态变更为"已审核"
     * - 记录审核人和审核时间
     *
     * @param id 入库单ID
     */
    void auditInbound(Long id);

    /**
     * 开始收货
     *
     * 业务规则：
     * - 只有"已审核"状态可以开始收货
     * - 状态变更为"收货中"
     * - 记录实际到货时间
     *
     * @param id 入库单ID
     */
    void startReceiving(Long id);

    /**
     * 完成收货
     *
     * 业务规则：
     * - 只有"收货中"状态可以完成
     * - 状态变更为"已完成"
     * - 更新库存
     * - 记录库存流水
     * - 记录完成人和完成时间
     *
     * @param id 入库单ID
     */
    void completeInbound(Long id);

    /**
     * 取消入库单
     *
     * 业务规则：
     * - "已完成"状态不能取消
     * - 状态变更为"已取消"
     *
     * @param id 入库单ID
     */
    void cancelInbound(Long id);

    /**
     * 批量获取入库单Map
     *
     * @param ids 入库单ID列表
     * @return 入库单Map，Key为入库单ID，Value为入库单DO
     */
    Map<Long, InboundDO> getInboundMap(Collection<Long> ids);

}

