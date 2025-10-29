/**
 * 库存流水 API
 * 
 * @author laby
 * @description 提供库存流水的查询功能，记录所有库存变动日志
 */
import request from '@/config/axios'

/**
 * 库存流水 VO
 */
export interface InventoryLogVO {
  id?: number
  warehouseId: number
  warehouseName?: string
  goodsId: number
  goodsName?: string
  skuCode?: string
  locationId?: number
  locationCode?: string
  batchNo?: string
  operationType: string
  quantityBefore?: number
  quantityChange: number
  quantityAfter?: number
  businessType?: string
  businessNo?: string
  operator?: string
  remark?: string
  createTime?: Date
}

/**
 * 库存流水分页查询 VO
 */
export interface InventoryLogPageReqVO extends PageParam {
  warehouseId?: number
  goodsId?: number
  locationId?: number
  batchNo?: string
  operationType?: string
  businessType?: string
  businessNo?: string
  createTime?: Date[]
}

/**
 * 获取库存流水分页列表
 * 
 * @param params 分页查询参数
 * @returns 返回分页数据
 */
export const getInventoryLogPage = (params: InventoryLogPageReqVO) => {
  return request.get({ url: '/wms/inventory-log/page', params })
}

