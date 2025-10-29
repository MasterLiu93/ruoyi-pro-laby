/**
 * 库存报表 API
 * 
 * @author laby
 * @description 提供库存报表查询和汇总功能
 */
import request from '@/config/axios'

/**
 * 库存报表查询 VO
 */
export interface InventoryReportReqVO extends PageParam {
  warehouseId?: number
  categoryId?: number
  goodsName?: string
  skuCode?: string
  lowStock?: boolean
}

/**
 * 库存报表 VO
 */
export interface InventoryReportVO {
  warehouseId: number
  warehouseName: string
  locationId: number
  locationCode: string
  goodsId: number
  skuCode: string
  goodsName: string
  categoryId: number
  categoryName: string
  batchNo: string
  totalQuantity: number
  availableQuantity: number
  lockedQuantity: number
  safetyStock: number
  isLowStock: boolean
  stockStatus: string
}

/**
 * 库存报表汇总 VO
 */
export interface InventoryReportSummaryVO {
  totalGoodsCount: number
  totalQuantity: number
  availableQuantity: number
  lockedQuantity: number
  lowStockCount: number
  zeroStockCount: number
}

// 获取库存报表分页列表
export const getInventoryReportPage = (params: InventoryReportReqVO) => {
  return request.get({ url: '/wms/inventory-report/page', params })
}

// 获取库存报表汇总
export const getInventoryReportSummary = (params: InventoryReportReqVO) => {
  return request.get({ url: '/wms/inventory-report/summary', params })
}

