/**
 * 出入库统计 API
 * 
 * @author laby
 * @description 提供出入库统计查询和汇总功能
 */
import request from '@/config/axios'

/**
 * 出入库统计查询 VO
 */
export interface InOutReportReqVO {
  statisticType?: number // 1-按日，2-按周，3-按月
  startTime?: Date | string
  endTime?: Date | string
  warehouseId?: number
  goodsId?: number
  supplierId?: number
  customerId?: number
}

/**
 * 出入库统计 VO
 */
export interface InOutReportVO {
  statisticDate: Date | string
  inboundQuantity: number
  inboundOrderCount: number
  outboundQuantity: number
  outboundOrderCount: number
  netChange: number
}

/**
 * 出入库统计汇总 VO
 */
export interface InOutReportSummaryVO {
  totalInboundQuantity: number
  totalInboundOrderCount: number
  totalOutboundQuantity: number
  totalOutboundOrderCount: number
  netChange: number
  turnoverRate: number
}

// 获取出入库统计列表
export const getInOutReportList = (params: InOutReportReqVO) => {
  return request.get({ url: '/wms/inout-report/list', params })
}

// 获取出入库统计汇总
export const getInOutReportSummary = (params: InOutReportReqVO) => {
  return request.get({ url: '/wms/inout-report/summary', params })
}

