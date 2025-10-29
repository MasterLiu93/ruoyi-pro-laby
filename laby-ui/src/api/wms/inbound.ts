/**
 * WMS入库管理 API
 * 
 * 功能说明：
 * 1. 入库单的CRUD操作
 * 2. 入库单的业务操作（审核、收货、完成、取消）
 * 3. 支持批量操作和状态流转
 * 
 * @author laby
 * @date 2025-10-28
 */

import request from '@/config/axios'

/**
 * 入库单VO
 */
export interface InboundVO {
  id: number // 入库单ID
  inboundNo: string // 入库单号
  inboundType: number // 入库类型：1-采购入库，2-退货入库，3-调拨入库，4-其他入库
  warehouseId: number // 仓库ID
  warehouseName?: string // 仓库名称（关联查询字段）
  supplierId?: number // 供应商ID
  supplierName?: string // 供应商名称
  status: number // 状态：1-待审核，2-已审核，3-收货中，4-已完成，5-已取消
  expectedArrivalTime?: Date // 预计到货时间
  actualArrivalTime?: Date // 实际到货时间
  totalQuantity: number // 总数量
  receivedQuantity: number // 已收货数量
  totalAmount?: number // 总金额
  auditBy?: number // 审核人ID
  auditByName?: string // 审核人名称
  auditTime?: Date // 审核时间
  completeBy?: number // 完成人ID
  completeByName?: string // 完成人名称
  completeTime?: Date // 完成时间
  remark?: string // 备注
  createTime?: Date // 创建时间
  items?: InboundItemVO[] // 入库单明细列表
}

/**
 * 入库单明细VO
 */
export interface InboundItemVO {
  id?: number // 明细ID
  inboundId?: number // 入库单ID
  goodsId: number // 商品ID
  goodsName?: string // 商品名称（关联查询字段）
  skuCode?: string // SKU编码（关联查询字段）
  goodsUnit?: number // 商品单位（关联查询字段）
  locationId?: number // 库位ID
  locationCode?: string // 库位编码（关联查询字段）
  batchNo?: string // 批次号
  serialNo?: string // 序列号
  productionDate?: Date // 生产日期
  expireDate?: Date // 过期日期
  planQuantity: number // 计划数量
  receivedQuantity: number // 实收数量
  qualifiedQuantity: number // 合格数量
  unqualifiedQuantity: number // 不合格数量
  price?: number // 单价
  amount?: number // 金额
  remark?: string // 备注
}

/**
 * 查询入库单分页列表
 * 
 * @param params 分页查询参数，包含入库单号、入库类型、仓库、状态等条件
 * @returns 返回分页数据
 */
export const getInboundPage = (params) => {
  return request.get({ url: '/wms/inbound/page', params })
}

/**
 * 查询入库单详情
 * 
 * @param id 入库单ID
 * @returns 返回入库单详情（包含明细列表）
 */
export const getInbound = (id: number) => {
  return request.get({ url: '/wms/inbound/get?id=' + id })
}

/**
 * 新增入库单
 * 
 * @param data 入库单信息，必须包含入库类型、仓库、明细列表
 * @returns 返回新创建的入库单ID
 */
export const createInbound = (data: InboundVO) => {
  return request.post({ url: '/wms/inbound/create', data })
}

/**
 * 修改入库单
 * 
 * 业务说明：仅"待审核"状态的入库单可以修改
 * 
 * @param data 入库单信息，必须包含ID
 * @returns 返回是否成功
 */
export const updateInbound = (data: InboundVO) => {
  return request.put({ url: '/wms/inbound/update', data })
}

/**
 * 删除入库单
 * 
 * 业务说明：仅"待审核"状态的入库单可以删除
 * 
 * @param id 入库单ID
 * @returns 返回是否成功
 */
export const deleteInbound = (id: number) => {
  return request.delete({ url: '/wms/inbound/delete?id=' + id })
}

/**
 * 审核入库单
 * 
 * 业务流程：
 * 1. 仅"待审核"状态可以审核
 * 2. 审核后状态变为"已审核"
 * 
 * @param id 入库单ID
 * @param auditBy 审核人ID
 * @param auditByName 审核人名称
 * @returns 返回是否成功
 */
export const auditInbound = (id: number, auditBy: number, auditByName: string) => {
  return request.post({
    url: '/wms/inbound/audit',
    params: { id, auditBy, auditByName }
  })
}

/**
 * 开始收货
 * 
 * 业务流程：
 * 1. 仅"已审核"状态可以开始收货
 * 2. 状态变为"收货中"
 * 
 * @param id 入库单ID
 * @returns 返回是否成功
 */
export const startReceiving = (id: number) => {
  return request.post({ url: '/wms/inbound/start-receiving', params: { id } })
}

/**
 * 完成收货（单个明细）
 * 
 * @param id 入库单ID
 * @param itemId 明细ID
 * @param receivedQuantity 实收数量
 * @param qualifiedQuantity 合格数量
 * @param unqualifiedQuantity 不合格数量
 * @returns 返回是否成功
 */
export const completeReceiving = (id: number, itemId: number, receivedQuantity: number, qualifiedQuantity: number, unqualifiedQuantity: number) => {
  return request.post({
    url: '/wms/inbound/complete-receiving',
    params: { id, itemId, receivedQuantity, qualifiedQuantity, unqualifiedQuantity }
  })
}

/**
 * 完成入库
 * 
 * 业务流程：
 * 1. 仅"收货中"状态可以完成入库
 * 2. 状态变为"已完成"
 * 3. 更新库存
 * 
 * @param id 入库单ID
 * @param completeBy 完成人ID
 * @param completeByName 完成人名称
 * @returns 返回是否成功
 */
export const completeInbound = (id: number, completeBy: number, completeByName: string) => {
  return request.post({
    url: '/wms/inbound/complete',
    params: { id, completeBy, completeByName }
  })
}

/**
 * 取消入库单
 * 
 * 业务说明："已完成"状态不能取消
 * 
 * @param id 入库单ID
 * @returns 返回是否成功
 */
export const cancelInbound = (id: number) => {
  return request.post({ url: '/wms/inbound/cancel', params: { id } })
}
