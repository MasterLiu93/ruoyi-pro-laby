import request from '@/config/axios'

/**
 * 库存信息 VO
 * 用于新增和修改库存
 */
export interface InventoryVO {
  id?: number // 库存ID（编辑时必传）
  warehouseId: number // 仓库ID（必填）
  locationId?: number // 库位ID（可选）
  goodsId: number // 商品ID（必填）
  batchNo?: string // 批次号（启用批次管理时必填）
  serialNo?: string // 序列号（启用序列号管理时必填）
  quantity: number // 库存数量（必填）
  lockQuantity?: number // 锁定数量（可选，默认0）
  productionDate?: string // 生产日期（可选）
  expireDate?: string // 过期日期（可选）
  inboundDate?: string // 入库日期（可选）
  supplierId?: number // 供应商ID（可选）
  supplierName?: string // 供应商名称（可选）
  status: number // 库存状态（必填）1-正常，2-冻结，3-待检，4-损坏
}

/**
 * 库存信息查询条件 VO
 * 用于库存列表的搜索和分页
 */
export interface InventoryPageReqVO extends PageParam {
  warehouseId?: number // 仓库ID（精确查询）
  locationId?: number // 库位ID（精确查询）
  goodsId?: number // 商品ID（精确查询）
  goodsName?: string // 商品名称（模糊查询）
  skuCode?: string // SKU编码（模糊查询）
  batchNo?: string // 批次号（精确查询）
  serialNo?: string // 序列号（精确查询）
  status?: number // 库存状态（精确查询）
  lowStock?: boolean // 是否低于安全库存
  createTime?: Date[] // 创建时间范围
}

/**
 * 库存信息返回 VO
 * 包含关联查询字段
 */
export interface InventoryRespVO {
  id: number // 库存ID
  warehouseId: number // 仓库ID
  warehouseName?: string // 仓库名称（关联查询）
  locationId?: number // 库位ID
  locationCode?: string // 库位编码（关联查询）
  goodsId: number // 商品ID
  goodsName?: string // 商品名称（关联查询）
  skuCode?: string // SKU编码（关联查询）
  goodsUnit?: number // 商品单位（关联查询）
  batchNo?: string // 批次号
  serialNo?: string // 序列号
  quantity: number // 库存数量
  lockQuantity: number // 锁定数量
  availableQuantity?: number // 可用数量（虚拟列）
  productionDate?: string // 生产日期
  expireDate?: string // 过期日期
  inboundDate?: string // 入库日期
  supplierId?: number // 供应商ID
  supplierName?: string // 供应商名称
  version: number // 乐观锁版本号
  status: number // 库存状态
  createTime: Date // 创建时间
  updateTime: Date // 更新时间
}

/**
 * 创建库存
 * 
 * @param data 库存信息
 * @returns 返回库存ID
 */
export const createInventory = (data: InventoryVO) => {
  return request.post({ url: '/wms/inventory/create', data })
}

/**
 * 更新库存
 * 
 * @param data 库存信息
 * @returns 返回成功标识
 */
export const updateInventory = (data: InventoryVO) => {
  return request.put({ url: '/wms/inventory/update', data })
}

/**
 * 删除库存
 * 只有库存数量=0且锁定数量=0时才允许删除
 * 
 * @param id 库存ID
 * @returns 返回成功标识
 */
export const deleteInventory = (id: number) => {
  return request.delete({ url: '/wms/inventory/delete?id=' + id })
}

/**
 * 获得库存详情
 * 
 * @param id 库存ID
 * @returns 返回库存详情
 */
export const getInventory = (id: number) => {
  return request.get({ url: '/wms/inventory/get?id=' + id })
}

/**
 * 获得库存分页列表
 * 
 * @param params 查询参数
 * @returns 返回分页结果（包含关联字段）
 */
export const getInventoryPage = (params: InventoryPageReqVO) => {
  return request.get({ url: '/wms/inventory/page', params })
}

