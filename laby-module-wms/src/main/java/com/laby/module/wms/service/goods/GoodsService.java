package com.laby.module.wms.service.goods;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.goods.vo.goods.GoodsPageReqVO;
import com.laby.module.wms.controller.admin.goods.vo.goods.GoodsSaveReqVO;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import jakarta.validation.*;
import java.util.List;
import java.util.Map;

/**
 * 商品信息 Service 接口
 * 提供商品信息的业务逻辑处理
 * 
 * 功能说明：
 * - 商品是WMS系统的核心主数据，记录商品的详细信息
 * - SKU编码必须唯一
 * - 商品的创建、更新、删除等基础操作
 * - 支持计量单位、规格、重量、体积等物理属性
 * - 支持保质期、存储温度等存储要求
 * - 支持批次管理（食品、药品）和序列号管理（贵重物品）
 * - 支持安全库存和最大库存设置，用于库存告警
 * - 删除商品前需校验是否有库存
 * - 支持批量查询优化（getGoodsMap）
 * - 支持多租户隔离
 *
 * @author laby
 */
public interface GoodsService {

    /**
     * 创建商品
     * 
     * 业务规则：
     * - SKU编码必须唯一
     * - 如果设置了分类，会校验分类是否存在
     * - 计量单位、状态为必填字段
     * - 启用批次管理或序列号管理后，入库时需要相应的批次号或序列号
     * - 自动设置创建时间和创建人
     * 
     * @param createReqVO 商品创建信息，包括SKU编码、名称、分类、规格等
     * @return 创建成功的商品ID
     */
    Long createGoods(@Valid GoodsSaveReqVO createReqVO);

    /**
     * 更新商品
     * 
     * 业务规则：
     * - 商品必须存在
     * - SKU编码必须唯一（排除自己）
     * - 如果商品已有库存，某些字段（如批次管理、序列号管理）可能受限
     * - 自动更新修改时间和修改人
     * 
     * @param updateReqVO 商品更新信息，必须包含id
     * @throws com.laby.framework.common.exception.ServiceException 如果商品不存在或SKU编码重复
     */
    void updateGoods(@Valid GoodsSaveReqVO updateReqVO);

    /**
     * 删除商品
     * 
     * 业务规则：
     * - 商品必须存在
     * - 商品下不能有库存（需先清空库存）
     * - 商品不能在入库单、出库单中（需先处理订单）
     * - 逻辑删除，不会真正删除数据
     * 
     * @param id 商品ID
     * @throws com.laby.framework.common.exception.ServiceException 如果商品不存在或有关联数据
     */
    void deleteGoods(Long id);

    /**
     * 获取商品详情
     * 
     * @param id 商品ID
     * @return 商品信息，如果不存在返回null
     */
    GoodsDO getGoods(Long id);

    /**
     * 获取商品分页列表
     * 
     * 支持的查询条件：
     * - SKU编码（模糊查询）
     * - 商品名称（模糊查询）
     * - 商品分类ID（精确查询）
     * - 品牌（模糊查询）
     * - 条形码（精确查询）
     * - 状态（精确查询：启用、禁用）
     * - 创建时间范围
     * 
     * @param pageReqVO 分页查询参数
     * @return 商品分页数据
     */
    PageResult<GoodsDO> getGoodsPage(GoodsPageReqVO pageReqVO);

    /**
     * 获取商品列表（不分页）
     * 
     * 说明：
     * - 返回所有启用的商品
     * - 主要用于前端下拉框选择
     * 
     * @return 商品完整列表
     */
    List<GoodsDO> getGoodsList();

    /**
     * 批量获取商品Map
     * 
     * 说明：
     * - 根据商品ID列表批量查询商品信息
     * - 返回 Map<商品ID, 商品DO>
     * - 主要用于数据关联查询优化（如库存列表关联商品名称）
     * - 如果某个ID对应的商品不存在，Map中不会包含该键值对
     * 
     * @param ids 商品ID列表
     * @return 商品Map，Key为商品ID，Value为商品DO
     */
    Map<Long, GoodsDO> getGoodsMap(List<Long> ids);

}
