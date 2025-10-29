/**
 * 库存预警 API
 * 
 * @author laby
 * @description 提供库存预警功能，包括低库存预警和即将过期预警
 */
import request from '@/config/axios'

/**
 * 库存预警 VO
 */
export interface InventoryWarningVO {
  warehouseId: number
  warehouseName?: string
  goodsId: number
  goodsName?: string
  skuCode?: string
  batchNo?: string
  warningType: string
  quantity: number
  lockQuantity?: number
  availableQuantity?: number
  safetyStock?: number
  expireDate?: string
  daysToExpire?: number
}

/**
 * 获取低库存预警列表
 * 
 * @returns 返回低库存预警列表
 */
export const getLowStockWarnings = () => {
  return request.get({ url: '/wms/inventory-warning/low-stock' })
}

/**
 * 获取即将过期预警列表
 * 
 * @returns 返回即将过期预警列表
 */
export const getExpiringWarnings = () => {
  return request.get({ url: '/wms/inventory-warning/expiring' })
}

/**
 * 获取所有预警列表
 * 
 * @returns 返回所有预警列表
 */
export const getAllWarnings = () => {
  return request.get({ url: '/wms/inventory-warning/all' })
}

