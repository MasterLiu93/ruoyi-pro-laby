/**
 * 库存快照 API
 * 
 * @author laby
 * @description 提供库存快照的查询功能，用于库存趋势分析
 */
import request from '@/config/axios'

/**
 * 库存快照 VO
 */
export interface InventorySnapshotVO {
  id?: number
  snapshotDate: string
  warehouseId: number
  warehouseName?: string
  goodsId: number
  goodsName?: string
  skuCode?: string
  quantity: number
  lockQuantity?: number
  availableQuantity?: number
  createTime?: Date
}

/**
 * 库存快照分页查询 VO
 */
export interface InventorySnapshotPageReqVO extends PageParam {
  snapshotDateRange?: string[]
  warehouseId?: number
  goodsId?: number
}

/**
 * 获取库存快照分页列表
 * 
 * @param params 分页查询参数
 * @returns 返回分页数据
 */
export const getInventorySnapshotPage = (params: InventorySnapshotPageReqVO) => {
  return request.get({ url: '/wms/inventory-snapshot/page', params })
}

/**
 * 获取库存趋势数据
 * 
 * @param params 查询参数
 * @returns 返回趋势数据列表
 */
export const getInventoryTrendList = (params: InventorySnapshotPageReqVO) => {
  return request.get({ url: '/wms/inventory-snapshot/trend', params })
}

