import request from '@/config/axios'

export interface WarehouseAreaVO {
  id?: number
  warehouseId: number
  areaCode: string
  areaName: string
  areaType: number
  floor?: number
  areaSize?: number
  status: number
  remark?: string
  createTime?: Date
}

export interface WarehouseAreaPageReqVO extends PageParam {
  warehouseId?: number
  areaCode?: string
  areaName?: string
  areaType?: number
  status?: number
  createTime?: Date[]
}

// 创建库区
export const createWarehouseArea = (data: WarehouseAreaVO) => {
  return request.post({ url: '/wms/warehouse-area/create', data })
}

// 更新库区
export const updateWarehouseArea = (data: WarehouseAreaVO) => {
  return request.put({ url: '/wms/warehouse-area/update', data })
}

// 删除库区
export const deleteWarehouseArea = (id: number) => {
  return request.delete({ url: '/wms/warehouse-area/delete?id=' + id })
}

// 批量删除库区
export const deleteWarehouseAreaList = (ids: number[]) => {
  return request.delete({ url: '/wms/warehouse-area/delete-list?ids=' + ids.join(',') })
}

// 获得库区
export const getWarehouseArea = (id: number) => {
  return request.get({ url: '/wms/warehouse-area/get?id=' + id })
}

// 获得库区分页
export const getWarehouseAreaPage = (params: WarehouseAreaPageReqVO) => {
  return request.get({ url: '/wms/warehouse-area/page', params })
}

// 根据仓库ID获得库区简单列表（用于下拉框）
export const getWarehouseAreaListByWarehouseId = (warehouseId: number) => {
  return request.get({ url: '/wms/warehouse-area/list-by-warehouse-id?warehouseId=' + warehouseId })
}

// 导出库区 Excel
export const exportWarehouseArea = (params: WarehouseAreaPageReqVO) => {
  return request.download({ url: '/wms/warehouse-area/export-excel', params })
}

