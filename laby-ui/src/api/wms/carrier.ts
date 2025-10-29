/**
 * 承运商信息 API
 * 
 * @author laby
 * @description 提供承运商信息的增删改查功能
 */
import request from '@/config/axios'

/**
 * 承运商信息 VO
 */
export interface CarrierVO {
  id?: number // 承运商ID（编辑时必传）
  carrierCode: string // 承运商编码，唯一标识，如：CARRIER-001
  carrierName: string // 承运商名称，如：顺丰速运
  carrierType: number // 承运商类型：1-快递，2-物流，3-专线
  contactPerson?: string // 联系人，如：张三
  contactPhone: string // 联系电话，如：13800138000
  contactEmail?: string // 邮箱，如：contact@sf-express.com
  serviceArea?: string // 服务区域，如：全国范围
  priceStandard?: string // 收费标准，如：首重1kg/15元，续重1kg/5元
  timeLimit?: string // 时效要求，如：同城24小时，跨省48小时
  rating?: number // 服务评分（1-5分）
  cooperationStartDate?: string // 合作开始日期
  status: number // 状态：1-启用，0-禁用
  remark?: string // 备注
  createTime?: Date // 创建时间
}

/**
 * 承运商分页查询 VO
 */
export interface CarrierPageReqVO extends PageParam {
  carrierCode?: string // 承运商编码（模糊搜索）
  carrierName?: string // 承运商名称（模糊搜索）
  carrierType?: number // 承运商类型（精确搜索）
  status?: number // 状态：1-启用，0-禁用
  createTime?: Date[] // 创建时间范围
}

/**
 * 创建承运商
 * 
 * @param data 承运商信息
 * @returns 返回创建的承运商ID
 */
export const createCarrier = (data: CarrierVO) => {
  return request.post({ url: '/wms/carrier/create', data })
}

/**
 * 更新承运商
 * 
 * @param data 承运商信息（必须包含id）
 * @returns 返回是否成功
 */
export const updateCarrier = (data: CarrierVO) => {
  return request.put({ url: '/wms/carrier/update', data })
}

/**
 * 删除承运商
 * 
 * @param id 承运商ID
 * @returns 返回是否成功
 */
export const deleteCarrier = (id: number) => {
  return request.delete({ url: '/wms/carrier/delete?id=' + id })
}

/**
 * 获取承运商详情
 * 
 * @param id 承运商ID
 * @returns 返回承运商详细信息
 */
export const getCarrier = (id: number) => {
  return request.get({ url: '/wms/carrier/get?id=' + id })
}

/**
 * 获取承运商分页列表
 * 
 * @param params 分页查询参数
 * @returns 返回分页数据
 */
export const getCarrierPage = (params: CarrierPageReqVO) => {
  return request.get({ url: '/wms/carrier/page', params })
}

/**
 * 获取承运商简单列表（用于下拉选择）
 * 
 * @returns 返回简单列表
 */
export const getCarrierSimpleList = () => {
  return request.get({ url: '/wms/carrier/simple-list' })
}

