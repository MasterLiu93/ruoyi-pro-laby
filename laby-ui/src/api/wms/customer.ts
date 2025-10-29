/**
 * 客户信息 API
 * 
 * @author laby
 * @description 提供客户信息的增删改查功能，支持客户类型、等级管理，记录信用额度和累计订单
 */
import request from '@/config/axios'

/**
 * 客户信息 VO
 * 用于客户信息的数据传输
 */
export interface CustomerVO {
  id?: number // 客户ID（编辑时必传）
  customerCode: string // 客户编码，唯一标识，如：CUST-001
  customerName: string // 客户名称，如：深圳市XX商贸有限公司
  customerType: number // 客户类型（字典：wms_customer_type）：1-零售，2-批发，3-企业
  customerLevel?: number // 客户等级（字典：wms_customer_level）：1-VIP，2-金牌，3-银牌，4-普通
  contactPerson?: string // 联系人，如：李四
  contactPhone?: string // 联系电话，如：13900139000
  contactEmail?: string // 邮箱，如：lisi@example.com
  deliveryProvince?: string // 收货省份，如：广东省
  deliveryCity?: string // 收货城市，如：深圳市
  deliveryDistrict?: string // 收货区县，如：福田区
  deliveryAddress?: string // 收货地址，如：华强北路1001号
  creditLimit?: number // 信用额度，如：100000.00
  totalOrders?: number // 累计订单数
  totalAmount?: number // 累计金额
  status?: number // 状态：0-禁用，1-启用
  remark?: string // 备注，如：优质客户，长期合作
  createTime?: Date // 创建时间
  updateTime?: Date // 更新时间
}

/**
 * 客户分页查询 VO
 */
export interface CustomerPageReqVO extends PageParam {
  customerCode?: string // 客户编码（模糊搜索）
  customerName?: string // 客户名称（模糊搜索）
  customerType?: number // 客户类型
  customerLevel?: number // 客户等级
  contactPerson?: string // 联系人（模糊搜索）
  contactPhone?: string // 联系电话（模糊搜索）
  status?: number // 状态
  createTime?: Date[] // 创建时间范围
}

/**
 * 创建客户
 * 
 * @param data 客户信息
 * @returns 返回创建的客户ID
 */
export const createCustomer = (data: CustomerVO) => {
  return request.post({ url: '/wms/customer/create', data })
}

/**
 * 更新客户
 * 
 * @param data 客户信息（必须包含id）
 * @returns 返回是否成功
 */
export const updateCustomer = (data: CustomerVO) => {
  return request.put({ url: '/wms/customer/update', data })
}

/**
 * 删除客户
 * 
 * @param id 客户ID
 * @returns 返回是否成功
 */
export const deleteCustomer = (id: number) => {
  return request.delete({ url: '/wms/customer/delete?id=' + id })
}

/**
 * 获取客户详情
 * 
 * @param id 客户ID
 * @returns 返回客户详细信息
 */
export const getCustomer = (id: number) => {
  return request.get({ url: '/wms/customer/get?id=' + id })
}

/**
 * 获取客户分页列表
 * 
 * @param params 分页查询参数
 * @returns 返回分页数据
 */
export const getCustomerPage = (params: CustomerPageReqVO) => {
  return request.get({ url: '/wms/customer/page', params })
}

/**
 * 获取客户简化列表（用于下拉框）
 * 
 * @returns 返回客户简化列表
 */
export const getCustomerSimpleList = () => {
  return request.get({ url: '/wms/customer/simple-list' })
}

