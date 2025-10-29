/**
 * 移库管理 API
 * 
 * @author laby
 * @description 提供移库单的增删改查功能，包括库位调整、库区调整、仓库调拨等
 */
import request from '@/config/axios'

/**
 * 移库单 VO
 * 用于移库单的数据传输
 */
export interface StockMoveVO {
  id?: number // 主键ID（编辑时必传）
  moveNo?: string // 移库单号（系统生成）
  warehouseId: number // 仓库ID
  warehouseName?: string // 仓库名称（关联查询字段，不需要提交）
  moveType: number // 移库类型：1-库位调整，2-库区调整，3-仓库调拨
  goodsId: number // 商品ID
  skuCode: string // SKU编码
  goodsName: string // 商品名称
  batchNo?: string // 批次号
  fromLocationId: number // 源库位ID
  fromLocationCode: string // 源库位编码
  toLocationId: number // 目标库位ID
  toLocationCode: string // 目标库位编码
  quantity: number // 移库数量
  moveReason?: string // 移库原因
  operator?: string // 操作人
  operateTime?: Date // 操作时间
  status?: number // 状态：1-待执行，2-执行中，3-已完成，4-已取消
  remark?: string // 备注
  createTime?: Date // 创建时间
}

/**
 * 移库单分页查询 VO
 */
export interface StockMovePageReqVO extends PageParam {
  moveNo?: string // 移库单号（模糊搜索）
  warehouseId?: number // 仓库ID（精确搜索）
  moveType?: number // 移库类型：1-库位调整，2-库区调整，3-仓库调拨
  goodsName?: string // 商品名称（模糊搜索）
  skuCode?: string // SKU编码（模糊搜索）
  batchNo?: string // 批次号（精确搜索）
  fromLocationId?: number // 源库位ID（精确搜索）
  toLocationId?: number // 目标库位ID（精确搜索）
  status?: number // 状态：1-待执行，2-执行中，3-已完成，4-已取消
  createTime?: Date[] // 创建时间范围
}

/**
 * 创建移库单
 * 
 * @param data 移库单信息
 * @returns 返回创建的移库单ID
 */
export const createStockMove = (data: StockMoveVO) => {
  return request.post({ url: '/wms/stock-move/create', data })
}

/**
 * 更新移库单
 * 
 * @param data 移库单信息（必须包含id）
 * @returns 返回是否成功
 */
export const updateStockMove = (data: StockMoveVO) => {
  return request.put({ url: '/wms/stock-move/update', data })
}

/**
 * 删除移库单
 * 
 * @param id 移库单ID
 * @returns 返回是否成功
 */
export const deleteStockMove = (id: number) => {
  return request.delete({ url: '/wms/stock-move/delete?id=' + id })
}

/**
 * 获取移库单详情
 * 
 * @param id 移库单ID
 * @returns 返回移库单详细信息
 */
export const getStockMove = (id: number) => {
  return request.get({ url: '/wms/stock-move/get?id=' + id })
}

/**
 * 获取移库单分页列表
 * 
 * @param params 分页查询参数
 * @returns 返回分页数据
 */
export const getStockMovePage = (params: StockMovePageReqVO) => {
  return request.get({ url: '/wms/stock-move/page', params })
}

/**
 * 执行移库
 * 
 * @param id 移库单ID
 * @returns 返回是否成功
 */
export const executeStockMove = (id: number) => {
  return request.put({ url: '/wms/stock-move/execute?id=' + id })
}

/**
 * 完成移库
 * 
 * @param id 移库单ID
 * @returns 返回是否成功
 */
export const completeStockMove = (id: number) => {
  return request.put({ url: '/wms/stock-move/complete?id=' + id })
}

/**
 * 取消移库
 * 
 * @param id 移库单ID
 * @returns 返回是否成功
 */
export const cancelStockMove = (id: number) => {
  return request.put({ url: '/wms/stock-move/cancel?id=' + id })
}

