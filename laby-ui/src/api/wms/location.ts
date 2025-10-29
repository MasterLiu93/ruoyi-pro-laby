import request from '@/config/axios'

export interface WarehouseLocationVO {
  id?: number
  warehouseId: number
  areaId: number
  locationCode: string
  locationType: number  // 1-普通, 2-临时, 3-残次品, 4-冷冻
  rowNo?: number       // 排号
  columnNo?: number    // 列号
  layerNo?: number     // 层号
  capacity?: number
  maxWeight?: number
  status: number
  remark?: string
  createTime?: Date
}

export interface WarehouseLocationPageReqVO extends PageParam {
  warehouseId?: number
  areaId?: number
  locationCode?: string
  locationType?: number
  status?: number
  createTime?: Date[]
}

// 创建库位
export const createWarehouseLocation = (data: WarehouseLocationVO) => {
  return request.post({ url: '/wms/warehouse-location/create', data })
}

// 更新库位
export const updateWarehouseLocation = (data: WarehouseLocationVO) => {
  return request.put({ url: '/wms/warehouse-location/update', data })
}

// 删除库位
export const deleteWarehouseLocation = (id: number) => {
  return request.delete({ url: '/wms/warehouse-location/delete?id=' + id })
}

// 批量删除库位
export const deleteWarehouseLocationList = (ids: number[]) => {
  return request.delete({ url: '/wms/warehouse-location/delete-list?ids=' + ids.join(',') })
}

// 获得库位
export const getWarehouseLocation = (id: number) => {
  return request.get({ url: '/wms/warehouse-location/get?id=' + id })
}

// 获得库位分页
export const getWarehouseLocationPage = (params: WarehouseLocationPageReqVO) => {
  return request.get({ url: '/wms/warehouse-location/page', params })
}

// 根据库区ID获得库位列表
export const getWarehouseLocationListByAreaId = (areaId: number) => {
  return request.get({ url: '/wms/warehouse-location/list-by-area-id?areaId=' + areaId })
}

// 导出库位 Excel
export const exportWarehouseLocation = (params: WarehouseLocationPageReqVO) => {
  return request.download({ url: '/wms/warehouse-location/export-excel', params })
}

// 获取库位简单列表（用于下拉框）
export const getWarehouseLocationSimpleList = async (params: number | { warehouseId: number }) => {
  // 兼容两种调用方式：直接传数字或传对象
  const warehouseId = typeof params === 'number' ? params : params.warehouseId
  const data = await getWarehouseLocationPage({ 
    warehouseId, 
    pageNo: 1, 
    pageSize: 100,
    status: 1 // 只获取启用的库位
  })
  return data.list || []
}

