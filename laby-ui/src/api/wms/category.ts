/**
 * 商品分类 API
 * 
 * @author laby
 * @description 提供商品分类的增删改查功能，支持树形结构
 */
import request from '@/config/axios'

/**
 * 商品分类 VO
 * 用于商品分类的数据传输
 */
export interface GoodsCategoryVO {
  id?: number // 分类ID（编辑时必传）
  categoryCode: string // 分类编码，唯一标识，如：CAT-001
  categoryName: string // 分类名称，如：电子产品
  parentId?: number // 父分类ID，0表示顶级分类
  level?: number // 分类层级，1-一级分类，2-二级分类，3-三级分类
  sort?: number // 排序号，数字越小越靠前
  createTime?: Date // 创建时间
}

/**
 * 商品分类查询条件 VO
 * 用于分类列表的搜索和过滤
 */
export interface GoodsCategoryListReqVO {
  categoryCode?: string // 分类编码（模糊搜索）
  categoryName?: string // 分类名称（模糊搜索）
  status?: number // 状态：0-禁用，1-启用
  createTime?: Date[] // 创建时间范围
}

/**
 * 创建商品分类
 * 
 * @param data 分类信息
 * @returns 返回创建的分类ID
 */
export const createGoodsCategory = (data: GoodsCategoryVO) => {
  return request.post({ url: '/wms/goods-category/create', data })
}

/**
 * 更新商品分类
 * 
 * @param data 分类信息（必须包含id）
 * @returns 返回是否成功
 */
export const updateGoodsCategory = (data: GoodsCategoryVO) => {
  return request.put({ url: '/wms/goods-category/update', data })
}

/**
 * 删除商品分类
 * 
 * @param id 分类ID
 * @returns 返回是否成功
 */
export const deleteGoodsCategory = (id: number) => {
  return request.delete({ url: '/wms/goods-category/delete?id=' + id })
}

/**
 * 获取商品分类详情
 * 
 * @param id 分类ID
 * @returns 返回分类详细信息
 */
export const getGoodsCategory = (id: number) => {
  return request.get({ url: '/wms/goods-category/get?id=' + id })
}

/**
 * 获取商品分类列表（树形表格）
 * 不分页，返回所有符合条件的分类（扁平结构）
 * 
 * @param params 查询参数
 * @returns 返回分类列表（扁平结构，前端构建树）
 */
export const getGoodsCategoryList = (params?: GoodsCategoryListReqVO) => {
  return request.get({ url: '/wms/goods-category/list', params })
}

/**
 * 获取商品分类简单列表（用于下拉框）
 * 返回所有启用的分类，不分页
 * 
 * @returns 返回分类列表
 */
export const getGoodsCategorySimpleList = () => {
  return request.get({ url: '/wms/goods-category/simple-list' })
}

