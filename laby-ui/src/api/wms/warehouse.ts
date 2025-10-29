import request from '@/config/axios'

export interface WarehouseVO {
  id?: number
  warehouseCode: string
  warehouseName: string
  warehouseType: string
  address?: string
  contactPerson?: string
  contactPhone?: string
  status: number
  remark?: string
  createTime?: Date
}

export interface WarehousePageReqVO extends PageParam {
  warehouseCode?: string
  warehouseName?: string
  warehouseType?: string
  status?: number
  createTime?: Date[]
}

// 创建仓库
export const createWarehouse = (data: WarehouseVO) => {
  return request.post({ url: '/wms/warehouse/create', data })
}

// 更新仓库
export const updateWarehouse = (data: WarehouseVO) => {
  return request.put({ url: '/wms/warehouse/update', data })
}

// 删除仓库
export const deleteWarehouse = (id: number) => {
  return request.delete({ url: '/wms/warehouse/delete?id=' + id })
}

// 批量删除仓库
export const deleteWarehouseList = (ids: number[]) => {
  return request.delete({ url: '/wms/warehouse/delete-list?ids=' + ids.join(',') })
}

// 获得仓库
export const getWarehouse = (id: number) => {
  return request.get({ url: '/wms/warehouse/get?id=' + id })
}

// 获得仓库分页
export const getWarehousePage = (params: WarehousePageReqVO) => {
  return request.get({ url: '/wms/warehouse/page', params })
}

// 获得仓库精简列表
export const getWarehouseSimpleList = () => {
  return request.get({ url: '/wms/warehouse/list-all-simple' })
}

// 导出仓库 Excel
export const exportWarehouse = (params: WarehousePageReqVO) => {
  return request.download({ url: '/wms/warehouse/export-excel', params })
}

