/**
 * WMS拣货波次管理 API
 *
 * 功能说明：
 * - 提供拣货波次的增删改查接口
 * - 支持拣货波次的分配、取消操作
 * - 支持自动生成拣货波次
 */
import request from '@/config/axios'

/**
 * 拣货波次 VO 接口定义
 */
export interface PickingWaveVO {
  id?: number
  waveNo?: string
  warehouseId?: number
  waveType?: number
  orderCount?: number
  itemCount?: number
  totalQuantity?: number
  priority?: number
  pickerId?: number
  pickerName?: string
  estimatedTime?: Date
  actualTime?: Date
  startTime?: Date
  endTime?: Date
  status?: number
  remark?: string
  createTime?: Date
  outboundIds?: number[]
  outboundNos?: string[]
}

/**
 * 拣货波次分页查询参数
 */
export interface PickingWavePageReqVO extends PageParam {
  waveNo?: string
  warehouseId?: number
  waveType?: number
  status?: number
  pickerId?: number
  createTime?: Date[]
}

/**
 * 获取拣货波次分页列表
 *
 * @param params 分页查询参数
 * @returns 分页数据
 */
export const getPickingWavePage = (params: PickingWavePageReqVO) => {
  return request.get({ url: '/wms/picking-wave/page', params })
}

/**
 * 获取拣货波次详情
 *
 * @param id 拣货波次ID
 * @returns 拣货波次详情
 */
export const getPickingWave = (id: number) => {
  return request.get({ url: `/wms/picking-wave/get?id=${id}` })
}

/**
 * 创建拣货波次
 *
 * @param data 拣货波次信息
 * @returns 创建的拣货波次ID
 */
export const createPickingWave = (data: PickingWaveVO) => {
  return request.post({ url: '/wms/picking-wave/create', data })
}

/**
 * 修改拣货波次
 *
 * @param data 拣货波次信息
 */
export const updatePickingWave = (data: PickingWaveVO) => {
  return request.put({ url: '/wms/picking-wave/update', data })
}

/**
 * 删除拣货波次
 *
 * @param id 拣货波次ID
 */
export const deletePickingWave = (id: number) => {
  return request.delete({ url: `/wms/picking-wave/delete?id=${id}` })
}

/**
 * 分配拣货波次
 *
 * @param id 拣货波次ID
 * @param pickerId 拣货员ID
 */
export const assignPickingWave = (id: number, pickerId: number) => {
  return request.put({ url: '/wms/picking-wave/assign', data: { id, pickerId } })
}

/**
 * 取消拣货波次
 *
 * @param id 拣货波次ID
 */
export const cancelPickingWave = (id: number) => {
  return request.put({ url: `/wms/picking-wave/cancel?id=${id}` })
}

/**
 * 自动生成拣货波次
 *
 * @param warehouseId 仓库ID
 * @param waveType 波次类型（1-批次拣货, 2-分区拣货, 3-单品拣货）
 * @returns 生成的波次ID列表
 */
export const generatePickingWaves = (warehouseId: number, waveType: number) => {
  return request.post({
    url: '/wms/picking-wave/generate',
    data: { warehouseId, waveType }
  })
}

