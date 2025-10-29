package com.laby.module.wms.service.inventory;

import com.laby.module.wms.controller.admin.inventory.vo.warning.InventoryWarningRespVO;

import java.util.List;

/**
 * 库存预警 Service 接口
 * 
 * 功能说明：
 * - 提供库存预警功能
 * - 检测低库存和即将过期商品
 * - 支持库存预警查询
 * 
 * 预警规则：
 * 1. 低库存预警：可用数量（quantity - lockQuantity）< 安全库存（safetyStock）
 * 2. 即将过期预警：商品有批次管理且距离过期日期 <= 7天
 * 
 * 业务场景：
 * 1. 库存预警查询
 * 2. 预警通知推送
 * 3. 库存补货建议
 * 4. 过期商品处理
 *
 * @author laby
 */
public interface InventoryWarningService {

    /**
     * 获取低库存预警列表
     * 
     * 说明：
     * - 查询可用数量低于安全库存的商品
     * - 按仓库维度统计
     * - 包含关联的仓库名、商品名
     * 
     * 预警条件：
     * - (quantity - lockQuantity) < safetyStock
     * - safetyStock > 0（已设置安全库存）
     * 
     * @return 低库存预警列表
     */
    List<InventoryWarningRespVO> getLowStockWarnings();

    /**
     * 获取即将过期预警列表
     * 
     * 说明：
     * - 查询距离过期日期 <= 7天的批次商品
     * - 按仓库和批次维度统计
     * - 包含关联的仓库名、商品名
     * 
     * 预警条件：
     * - expireDate IS NOT NULL
     * - DATEDIFF(expireDate, CURDATE()) <= 7
     * - DATEDIFF(expireDate, CURDATE()) >= 0（未过期）
     * 
     * @return 即将过期预警列表
     */
    List<InventoryWarningRespVO> getExpiringWarnings();

    /**
     * 获取所有预警列表（包含低库存和即将过期）
     * 
     * 说明：
     * - 合并低库存预警和即将过期预警
     * - 去除重复数据
     * - 按预警类型和商品分组
     * 
     * @return 所有预警列表
     */
    List<InventoryWarningRespVO> getAllWarnings();

}

