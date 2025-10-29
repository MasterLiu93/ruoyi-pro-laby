/**
 * 供应商信息 API
 * 
 * @author laby
 * @description 提供供应商信息的增删改查功能，支持供应商类型、信用等级管理
 */
import request from '@/config/axios'

/**
 * 供应商信息 VO
 * 用于供应商信息的数据传输
 */
export interface SupplierVO {
  id?: number // 供应商ID（编辑时必传）
  supplierCode: string // 供应商编码，唯一标识，如：SUP-001
  supplierName: string // 供应商名称，如：深圳市华强电子有限公司
  supplierType: number // 供应商类型（字典：wms_supplier_type）：1-普通，2-VIP，3-战略
  contactPerson?: string // 联系人，如：张三
  contactPhone?: string // 联系电话，如：13800138000
  contactEmail?: string // 邮箱，如：zhangsan@example.com
  province?: string // 省份，如：广东省
  city?: string // 城市，如：深圳市
  district?: string // 区县，如：南山区
  address?: string // 详细地址，如：科技园南区深南大道9988号
  creditLevel?: number // 信用等级（字典：wms_supplier_credit_level）：1-优秀，2-良好，3-一般，4-较差
  cooperationStartDate?: string // 合作开始日期，如：2023-01-01
  status?: number // 状态：0-禁用，1-启用
  remark?: string // 备注，如：长期合作伙伴，质量稳定
  createTime?: Date // 创建时间
  updateTime?: Date // 更新时间
}

/**
 * 供应商分页查询 VO
 */
export interface SupplierPageReqVO extends PageParam {
  supplierCode?: string // 供应商编码（模糊搜索）
  supplierName?: string // 供应商名称（模糊搜索）
  supplierType?: number // 供应商类型
  creditLevel?: number // 信用等级
  contactPerson?: string // 联系人（模糊搜索）
  contactPhone?: string // 联系电话（模糊搜索）
  status?: number // 状态
  createTime?: Date[] // 创建时间范围
}

/**
 * 创建供应商
 * 
 * @param data 供应商信息
 * @returns 返回创建的供应商ID
 */
export const createSupplier = (data: SupplierVO) => {
  return request.post({ url: '/wms/supplier/create', data })
}

/**
 * 更新供应商
 * 
 * @param data 供应商信息（必须包含id）
 * @returns 返回是否成功
 */
export const updateSupplier = (data: SupplierVO) => {
  return request.put({ url: '/wms/supplier/update', data })
}

/**
 * 删除供应商
 * 
 * @param id 供应商ID
 * @returns 返回是否成功
 */
export const deleteSupplier = (id: number) => {
  return request.delete({ url: '/wms/supplier/delete?id=' + id })
}

/**
 * 获取供应商详情
 * 
 * @param id 供应商ID
 * @returns 返回供应商详细信息
 */
export const getSupplier = (id: number) => {
  return request.get({ url: '/wms/supplier/get?id=' + id })
}

/**
 * 获取供应商分页列表
 * 
 * @param params 分页查询参数
 * @returns 返回分页数据
 */
export const getSupplierPage = (params: SupplierPageReqVO) => {
  return request.get({ url: '/wms/supplier/page', params })
}

/**
 * 获取供应商简化列表（用于下拉框）
 * 
 * @returns 返回供应商简化列表
 */
export const getSupplierSimpleList = () => {
  return request.get({ url: '/wms/supplier/simple-list' })
}

