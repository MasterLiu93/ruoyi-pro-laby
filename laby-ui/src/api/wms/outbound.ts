/**
 * WMS出库管理 API
 * 
 * 功能说明：
 * 1. 出库单的CRUD操作
 * 2. 出库单的业务操作（审核、拣货、发货、完成、取消）
 * 3. 支持批量操作和状态流转
 * 4. 出库会扣减库存并记录流水
 * 
 * @author laby
 * @date 2025-10-28
 */

import request from '@/config/axios'

/**
 * 出库单VO
 */
export interface OutboundVO {
  id: number // 出库单ID
  outboundNo: string // 出库单号
  outboundType: number // 出库类型：1-销售出库，2-调拨出库，3-退货出库，4-其他出库
  warehouseId: number // 仓库ID
  warehouseName?: string // 仓库名称（关联查询字段）
  customerId?: number // 客户ID
  customerName?: string // 客户名称
  status: number // 状态：1-待审核，2-已审核，3-拣货中，4-待发货，5-已发货，6-已取消
  expectedShipmentTime?: Date // 预计发货时间
  actualShipmentTime?: Date // 实际发货时间
  totalQuantity: number // 总数量
  pickedQuantity: number // 已拣货数量
  totalAmount?: number // 总金额
  auditBy?: number // 审核人ID
  auditByName?: string // 审核人名称
  auditTime?: Date // 审核时间
  completeBy?: number // 完成人ID
  completeByName?: string // 完成人名称
  completeTime?: Date // 完成时间
  remark?: string // 备注
  createTime?: Date // 创建时间
  items?: OutboundItemVO[] // 出库单明细列表
}

/**
 * 出库单明细VO
 */
export interface OutboundItemVO {
  id?: number // 明细ID
  outboundId?: number // 出库单ID
  goodsId: number // 商品ID
  goodsName?: string // 商品名称（关联查询字段）
  skuCode?: string // SKU编码（关联查询字段）
  goodsUnit?: number // 商品单位（关联查询字段）
  locationId?: number // 库位ID
  locationCode?: string // 库位编码（关联查询字段）
  batchNo?: string // 批次号
  serialNo?: string // 序列号
  planQuantity: number // 计划数量
  pickedQuantity: number // 已拣货数量
  shippedQuantity: number // 已发货数量
  price?: number // 单价
  amount?: number // 金额
  remark?: string // 备注
}

/**
 * 查询出库单分页列表
 * 
 * @param params 分页查询参数，包含出库单号、出库类型、仓库、状态等条件
 * @returns 返回分页数据
 */
export const getOutboundPage = (params) => {
  return request.get({ url: '/wms/outbound/page', params })
}

/**
 * 查询出库单详情
 * 
 * @param id 出库单ID
 * @returns 返回出库单详情（包含明细列表）
 */
export const getOutbound = (id: number) => {
  return request.get({ url: '/wms/outbound/get?id=' + id })
}

/**
 * 新增出库单
 * 
 * @param data 出库单信息，必须包含出库类型、仓库、明细列表
 * @returns 返回新创建的出库单ID
 */
export const createOutbound = (data: OutboundVO) => {
  return request.post({ url: '/wms/outbound/create', data })
}

/**
 * 修改出库单
 * 
 * 业务说明：仅"待审核"状态的出库单可以修改
 * 
 * @param data 出库单信息，必须包含ID
 * @returns 返回是否成功
 */
export const updateOutbound = (data: OutboundVO) => {
  return request.put({ url: '/wms/outbound/update', data })
}

/**
 * 删除出库单
 * 
 * 业务说明：仅"待审核"状态的出库单可以删除
 * 
 * @param id 出库单ID
 * @returns 返回是否成功
 */
export const deleteOutbound = (id: number) => {
  return request.delete({ url: '/wms/outbound/delete?id=' + id })
}

/**
 * 审核出库单
 * 
 * 业务流程：
 * 1. 仅"待审核"状态可以审核
 * 2. 审核后状态变为"已审核"
 * 
 * @param id 出库单ID
 * @param auditBy 审核人ID
 * @param auditByName 审核人名称
 * @returns 返回是否成功
 */
export const auditOutbound = (id: number, auditBy: number, auditByName: string) => {
  return request.post({
    url: '/wms/outbound/audit',
    params: { id, auditBy, auditByName }
  })
}

/**
 * 开始拣货
 * 
 * 业务流程：
 * 1. 仅"已审核"状态可以开始拣货
 * 2. 状态变为"拣货中"
 * 
 * @param id 出库单ID
 * @returns 返回是否成功
 */
export const startPicking = (id: number) => {
  return request.post({ url: '/wms/outbound/start-picking', params: { id } })
}

/**
 * 完成拣货（单个明细）
 * 
 * @param id 出库单ID
 * @param itemId 明细ID
 * @param pickedQuantity 已拣货数量
 * @returns 返回是否成功
 */
export const completePicking = (id: number, itemId: number, pickedQuantity: number) => {
  return request.post({
    url: '/wms/outbound/complete-picking',
    params: { id, itemId, pickedQuantity }
  })
}

/**
 * 发货（完成出库）
 * 
 * 业务流程：
 * 1. 仅"待发货"状态可以发货
 * 2. 状态变为"已发货"
 * 3. 扣减库存
 * 
 * @param id 出库单ID
 * @param completeBy 完成人ID
 * @param completeByName 完成人名称
 * @returns 返回是否成功
 */
export const shipOutbound = (id: number, completeBy: number, completeByName: string) => {
  return request.post({
    url: '/wms/outbound/ship',
    params: { id, completeBy, completeByName }
  })
}

/**
 * 取消出库单
 * 
 * 业务说明："已发货"状态不能取消
 * 
 * @param id 出库单ID
 * @returns 返回是否成功
 */
export const cancelOutbound = (id: number) => {
  return request.post({ url: '/wms/outbound/cancel', params: { id } })
}
