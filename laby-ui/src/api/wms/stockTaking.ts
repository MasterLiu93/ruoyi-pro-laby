/**
 * 盘点单管理 API
 * 
 * @author laby
 * @description 提供盘点单的增删改查功能，包括提交盘点、复核、调整库存等操作
 */
import request from '@/config/axios'

/**
 * 盘点单 VO
 */
export interface StockTakingVO {
  id?: number
  takingNo?: string
  planId?: number
  planNo?: string
  warehouseId: number
  warehouseName?: string
  locationId?: number
  locationCode?: string
  goodsId: number
  skuCode: string
  goodsName: string
  batchNo?: string
  bookQuantity: number
  actualQuantity?: number
  diffQuantity?: number
  diffReason?: string
  operator?: string
  operateTime?: Date
  reviewer?: string
  reviewTime?: Date
  status?: number // 1-待盘点,2-已盘点,3-已复核,4-已调整
  remark?: string
  createTime?: Date
}

/**
 * 盘点单分页查询 VO
 */
export interface StockTakingPageReqVO extends PageParam {
  takingNo?: string
  planId?: number
  planNo?: string
  warehouseId?: number
  locationId?: number
  goodsName?: string
  skuCode?: string
  batchNo?: string
  status?: number
  createTime?: Date[]
}

// 创建盘点单
export const createStockTaking = (data: StockTakingVO) => {
  return request.post({ url: '/wms/stock-taking/create', data })
}

// 更新盘点单
export const updateStockTaking = (data: StockTakingVO) => {
  return request.put({ url: '/wms/stock-taking/update', data })
}

// 删除盘点单
export const deleteStockTaking = (id: number) => {
  return request.delete({ url: '/wms/stock-taking/delete?id=' + id })
}

// 获取盘点单详情
export const getStockTaking = (id: number) => {
  return request.get({ url: '/wms/stock-taking/get?id=' + id })
}

// 获取盘点单分页列表
export const getStockTakingPage = (params: StockTakingPageReqVO) => {
  return request.get({ url: '/wms/stock-taking/page', params })
}

// 提交盘点
export const submitStockTaking = (id: number, actualQuantity: number, diffReason?: string) => {
  return request.put({ 
    url: '/wms/stock-taking/submit', 
    params: { id, actualQuantity, diffReason }
  })
}

// 复核盘点
export const reviewStockTaking = (id: number) => {
  return request.put({ url: '/wms/stock-taking/review?id=' + id })
}

// 调整库存
export const adjustStockTaking = (id: number) => {
  return request.put({ url: '/wms/stock-taking/adjust?id=' + id })
}

