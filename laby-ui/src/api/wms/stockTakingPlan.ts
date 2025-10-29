/**
 * 盘点计划管理 API
 * 
 * @author laby
 * @description 提供盘点计划的增删改查功能，包括审核、取消等操作
 */
import request from '@/config/axios'

/**
 * 盘点计划 VO
 */
export interface StockTakingPlanVO {
  id?: number
  planNo?: string
  planName: string
  warehouseId: number
  warehouseName?: string
  takingType: number // 1-全盘,2-循环盘,3-抽盘,4-动态盘
  scopeType: number // 1-全仓,2-库区,3-库位,4-商品
  scopeValue?: string
  planStartTime: Date | string
  planEndTime: Date | string
  actualStartTime?: Date
  actualEndTime?: Date
  totalCount?: number
  completedCount?: number
  diffCount?: number
  status?: number // 1-待审核,2-待执行,3-执行中,4-已完成,5-已取消
  auditUser?: string
  auditTime?: Date
  remark?: string
  createTime?: Date
}

/**
 * 盘点计划分页查询 VO
 */
export interface StockTakingPlanPageReqVO extends PageParam {
  planNo?: string
  planName?: string
  warehouseId?: number
  takingType?: number
  scopeType?: number
  status?: number
  createTime?: Date[]
}

// 创建盘点计划
export const createStockTakingPlan = (data: StockTakingPlanVO) => {
  return request.post({ url: '/wms/stock-taking-plan/create', data })
}

// 更新盘点计划
export const updateStockTakingPlan = (data: StockTakingPlanVO) => {
  return request.put({ url: '/wms/stock-taking-plan/update', data })
}

// 删除盘点计划
export const deleteStockTakingPlan = (id: number) => {
  return request.delete({ url: '/wms/stock-taking-plan/delete?id=' + id })
}

// 获取盘点计划详情
export const getStockTakingPlan = (id: number) => {
  return request.get({ url: '/wms/stock-taking-plan/get?id=' + id })
}

// 获取盘点计划分页列表
export const getStockTakingPlanPage = (params: StockTakingPlanPageReqVO) => {
  return request.get({ url: '/wms/stock-taking-plan/page', params })
}

// 审核盘点计划
export const auditStockTakingPlan = (id: number) => {
  return request.put({ url: '/wms/stock-taking-plan/audit?id=' + id })
}

// 取消盘点计划
export const cancelStockTakingPlan = (id: number) => {
  return request.put({ url: '/wms/stock-taking-plan/cancel?id=' + id })
}

