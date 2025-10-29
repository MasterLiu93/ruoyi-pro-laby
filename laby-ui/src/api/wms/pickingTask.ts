/**
 * WMS 拣货任务 API
 * 
 * @author laby
 * @description 提供拣货任务的查询、分配、执行等功能
 */
import request from '@/config/axios'

/**
 * 拣货任务 VO
 */
export interface PickingTaskVO {
  id?: number // 主键ID
  taskNo?: string // 任务编号
  waveId?: number // 波次ID
  waveNo?: string // 波次号
  outboundId: number // 出库单ID
  outboundNo?: string // 出库单号
  warehouseId: number // 仓库ID
  warehouseName?: string // 仓库名称
  goodsId: number // 商品ID
  skuCode?: string // SKU编码
  goodsName?: string // 商品名称
  batchNo?: string // 批次号
  locationId: number // 库位ID
  locationCode?: string // 库位编码
  planQuantity: number // 计划拣货数量
  actualQuantity?: number // 实际拣货数量
  sortOrder?: number // 拣货顺序
  pickerId?: number // 拣货员ID
  pickerName?: string // 拣货员姓名
  pickingTime?: Date // 拣货时间
  status?: number // 状态：1-待拣货，2-拣货中，3-已完成，4-异常
  exceptionType?: number // 异常类型（字典：wms_picking_exception_type，1-库位空, 2-库存不足, 3-商品损坏, 4-商品过期, 5-拣错商品）
  exceptionRemark?: string // 异常说明
  remark?: string // 备注
  createTime?: Date // 创建时间
}

/**
 * 拣货任务分页查询 VO
 */
export interface PickingTaskPageReqVO extends PageParam {
  taskNo?: string // 任务编号（模糊搜索）
  waveId?: number // 波次ID
  waveNo?: string // 波次号（模糊搜索）
  outboundId?: number // 出库单ID
  outboundNo?: string // 出库单号（模糊搜索）
  warehouseId?: number // 仓库ID
  goodsId?: number // 商品ID
  locationId?: number // 库位ID
  pickerId?: number // 拣货员ID
  status?: number // 拣货任务状态
  createTime?: Date[] // 创建时间范围
}

/**
 * 拣货操作 VO
 */
export interface PickingTaskPickReqVO {
  id: number // 拣货任务ID
  actualQuantity: number // 实际拣货数量
  exceptionType?: number // 异常类型（字典：wms_picking_exception_type，1-库位空, 2-库存不足, 3-商品损坏, 4-商品过期, 5-拣错商品）
  exceptionRemark?: string // 异常说明
  remark?: string // 备注
}

/**
 * 获取拣货任务详情
 * 
 * @param id 拣货任务ID
 * @returns 返回拣货任务详细信息
 */
export const getPickingTask = (id: number) => {
  return request.get({ url: '/wms/picking-task/get?id=' + id })
}

/**
 * 获取拣货任务分页列表
 * 
 * @param params 分页查询参数
 * @returns 返回分页数据
 */
export const getPickingTaskPage = (params: PickingTaskPageReqVO) => {
  return request.get({ url: '/wms/picking-task/page', params })
}

/**
 * 根据出库单ID查询拣货任务列表
 * 
 * @param outboundId 出库单ID
 * @returns 返回拣货任务列表
 */
export const getPickingTaskListByOutbound = (outboundId: number) => {
  return request.get({ url: '/wms/picking-task/list-by-outbound?outboundId=' + outboundId })
}

/**
 * 分配拣货任务
 * 
 * @param ids 拣货任务ID列表
 * @param pickerId 拣货员ID
 * @param pickerName 拣货员姓名
 * @returns 返回是否成功
 */
export const assignPickingTasks = (ids: number[], pickerId: number, pickerName: string) => {
  return request.put({
    url: '/wms/picking-task/assign',
    params: { ids, pickerId, pickerName }
  })
}

/**
 * 执行拣货操作
 * 
 * @param data 拣货操作信息
 * @returns 返回是否成功
 */
export const executePicking = (data: PickingTaskPickReqVO) => {
  return request.put({ url: '/wms/picking-task/pick', data })
}

/**
 * 完成拣货任务
 * 
 * @param id 拣货任务ID
 * @returns 返回是否成功
 */
export const completePickingTask = (id: number) => {
  return request.put({ url: '/wms/picking-task/complete?id=' + id })
}

/**
 * 取消拣货任务
 * 
 * @param id 拣货任务ID
 * @returns 返回是否成功
 */
export const cancelPickingTask = (id: number) => {
  return request.delete({ url: '/wms/picking-task/cancel?id=' + id })
}

/**
 * 标记拣货异常
 * 
 * @param id 拣货任务ID
 * @param exceptionType 异常类型
 * @param exceptionRemark 异常说明
 * @returns 返回是否成功
 */
export const markPickingException = (id: number, exceptionType: number, exceptionRemark?: string) => {
  return request.put({
    url: '/wms/picking-task/mark-exception',
    params: { id, exceptionType, exceptionRemark }
  })
}

