package com.laby.module.wms.dal.mysql.inventory;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.inventory.vo.InventoryPageReqVO;
import com.laby.module.wms.controller.admin.report.vo.InventoryReportReqVO;
import com.laby.module.wms.dal.dataobject.inventory.InventoryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存信息 Mapper
 * 数据访问层，使用 MyBatis Plus
 * 
 * 功能说明：
 * - 继承 BaseMapperX，提供基础的 CRUD 方法
 * - 使用 LambdaQueryWrapperX 构建动态查询条件
 * - 只返回 DO 对象，不返回 VO（关联查询在 Service 层处理）
 * - 支持多租户自动隔离（由 TenantLineHandler 拦截器实现）
 * 
 * 查询场景：
 * - 按仓库/库位/商品查询库存
 * - 按批次号/序列号精确查询
 * - 按状态筛选库存
 * - 低库存预警查询
 * 
 * @author laby
 */
@Mapper
public interface InventoryMapper extends BaseMapperX<InventoryDO> {

    /**
     * 分页查询库存列表
     * 
     * 说明：
     * - 必须指定仓库ID
     * - 支持多条件动态查询
     * 
     * 支持的查询条件：
     * - 仓库ID（精确查询，必填）
     * - 库位ID（精确查询，可选）
     * - 商品ID（精确查询，可选）
     * - 批次号（精确查询，可选）
     * - 序列号（精确查询，可选）
     * - 状态（精确查询，可选）
     * - 创建时间范围（区间查询，可选）
     * 
     * 排序规则：
     * - 先按更新时间降序（最近更新的在前）
     * 
     * @param reqVO 分页查询条件
     * @return 库存分页结果
     */
    default PageResult<InventoryDO> selectPage(InventoryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<InventoryDO>()
                .eqIfPresent(InventoryDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(InventoryDO::getLocationId, reqVO.getLocationId())
                .eqIfPresent(InventoryDO::getGoodsId, reqVO.getGoodsId())
                .eqIfPresent(InventoryDO::getBatchNo, reqVO.getBatchNo())
                .eqIfPresent(InventoryDO::getSerialNo, reqVO.getSerialNo())
                .eqIfPresent(InventoryDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(InventoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(InventoryDO::getUpdateTime));
    }

    /**
     * 报表查询库存分页列表
     * 
     * @param reqVO 报表查询条件
     * @return 库存分页结果
     */
    default PageResult<InventoryDO> selectPage(InventoryReportReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<InventoryDO>()
                .eqIfPresent(InventoryDO::getWarehouseId, reqVO.getWarehouseId())
                .orderByDesc(InventoryDO::getUpdateTime));
    }

}

