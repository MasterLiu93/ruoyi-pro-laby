/**
 * 商品信息 API
 * 
 * @author laby
 * @description 提供商品信息的增删改查功能，支持批次管理、序列号管理等
 */
import request from '@/config/axios'

/**
 * 商品信息 VO
 * 用于商品信息的数据传输
 */
export interface GoodsVO {
  id?: number // 商品ID（编辑时必传）
  skuCode: string // SKU编码，唯一标识，如：SKU-001
  goodsName: string // 商品名称，如：iPhone 15 Pro Max
  categoryId?: number // 商品分类ID
  categoryName?: string // 商品分类名称（关联查询字段，不需要提交）
  brand?: string // 品牌，如：Apple
  model?: string // 型号，如：A2849
  barcode?: string // 条形码，如：6901234567890
  unit: number // 计量单位：1-个，2-箱，3-千克，4-吨等
  spec?: string // 规格描述，如：512GB 深空黑色
  weight?: number // 重量(KG)
  volume?: number // 体积(立方米)
  shelfLife?: number // 保质期(天)，0表示无保质期
  storageTempMin?: number // 最低存储温度(℃)，可为负数
  storageTempMax?: number // 最高存储温度(℃)
  needBatch?: boolean // 是否启用批次管理（食品、药品等）
  needSerial?: boolean // 是否启用序列号管理（手机、电脑等贵重物品）
  safetyStock?: number // 安全库存，低于此值告警
  maxStock?: number // 最大库存，高于此值告警
  status: number // 状态：0-禁用，1-启用
  remark?: string // 备注说明
  createTime?: Date // 创建时间
}

/**
 * 商品分页查询 VO
 * 用于商品列表的搜索和分页
 */
export interface GoodsPageReqVO extends PageParam {
  skuCode?: string // SKU编码（模糊搜索）
  goodsName?: string // 商品名称（模糊搜索）
  categoryId?: number // 商品分类ID（精确搜索）
  brand?: string // 品牌（模糊搜索）
  barcode?: string // 条形码（精确搜索）
  status?: number // 状态：0-禁用，1-启用
  createTime?: Date[] // 创建时间范围
}

/**
 * 创建商品
 * 
 * @param data 商品信息
 * @returns 返回创建的商品ID
 */
export const createGoods = (data: GoodsVO) => {
  return request.post({ url: '/wms/goods/create', data })
}

/**
 * 更新商品
 * 
 * @param data 商品信息（必须包含id）
 * @returns 返回是否成功
 */
export const updateGoods = (data: GoodsVO) => {
  return request.put({ url: '/wms/goods/update', data })
}

/**
 * 删除商品
 * 
 * @param id 商品ID
 * @returns 返回是否成功
 */
export const deleteGoods = (id: number) => {
  return request.delete({ url: '/wms/goods/delete?id=' + id })
}

/**
 * 获取商品详情
 * 后端会自动关联分类名称
 * 
 * @param id 商品ID
 * @returns 返回商品详细信息（包含分类名称）
 */
export const getGoods = (id: number) => {
  return request.get({ url: '/wms/goods/get?id=' + id })
}

/**
 * 获取商品分页列表
 * 后端会自动关联分类名称
 * 
 * @param params 分页查询参数
 * @returns 返回分页数据（每项包含分类名称）
 */
export const getGoodsPage = (params: GoodsPageReqVO) => {
  return request.get({ url: '/wms/goods/page', params })
}

/**
 * 获取商品简单列表（用于下拉框）
 * 
 * @returns 返回商品列表（最多100条）
 */
export const getGoodsSimpleList = async () => {
  const data = await getGoodsPage({ 
    pageNo: 1, 
    pageSize: 100,
    status: 1 // 只获取启用的商品
  })
  return data.list || []
}

