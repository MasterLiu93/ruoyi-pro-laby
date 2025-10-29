package com.laby.module.wms.service.goods;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.goods.vo.category.GoodsCategoryPageReqVO;
import com.laby.module.wms.controller.admin.goods.vo.category.GoodsCategorySaveReqVO;
import com.laby.module.wms.dal.dataobject.goods.GoodsCategoryDO;

import jakarta.validation.*;
import java.util.List;
import java.util.Map;

/**
 * 商品分类 Service 接口
 * 提供商品分类的业务逻辑处理
 * 
 * 功能说明：
 * - 商品分类用于对商品进行分类管理，支持树形结构
 * - 最多支持3级分类（一级分类→二级分类→三级分类）
 * - 通过 parentId 关联父分类，实现层级关系
 * - 分类编码必须唯一
 * - 分类的创建、更新、删除等基础操作
 * - 删除分类前需校验是否有子分类和商品
 * - 支持批量查询优化（getGoodsCategoryMap）
 * - 支持多租户隔离
 *
 * @author laby
 */
public interface GoodsCategoryService {

    /**
     * 创建商品分类
     * 
     * 业务规则：
     * - 分类编码必须唯一
     * - 如果设置了父分类，会校验父分类是否存在
     * - 层级（level）用于限制分类深度，最多3级
     * - 自动设置创建时间和创建人
     * 
     * @param createReqVO 分类创建信息，包括编码、名称、父分类ID、层级等
     * @return 创建成功的分类ID
     */
    Long createGoodsCategory(@Valid GoodsCategorySaveReqVO createReqVO);

    /**
     * 更新商品分类
     * 
     * 业务规则：
     * - 分类必须存在
     * - 分类编码必须唯一（排除自己）
     * - 不能将父分类设置为自己或自己的子分类（避免循环引用）
     * - 自动更新修改时间和修改人
     * 
     * @param updateReqVO 分类更新信息，必须包含id
     * @throws com.laby.framework.common.exception.ServiceException 如果分类不存在或编码重复
     */
    void updateGoodsCategory(@Valid GoodsCategorySaveReqVO updateReqVO);

    /**
     * 删除商品分类
     * 
     * 业务规则：
     * - 分类必须存在
     * - 分类下不能有子分类（需先删除子分类）
     * - 分类下不能有商品（需先删除或移动商品）
     * - 逻辑删除，不会真正删除数据
     * 
     * @param id 分类ID
     * @throws com.laby.framework.common.exception.ServiceException 如果分类不存在或有关联数据
     */
    void deleteGoodsCategory(Long id);

    /**
     * 获取商品分类详情
     * 
     * @param id 分类ID
     * @return 分类信息，如果不存在返回null
     */
    GoodsCategoryDO getGoodsCategory(Long id);

    /**
     * 获取商品分类列表（支持条件查询）
     * 
     * 说明：
     * - 不分页，返回所有符合条件的分类
     * - 返回扁平列表，前端构建树形结构
     * - 支持搜索条件过滤
     * 
     * 支持的查询条件：
     * - 分类编码（模糊查询）
     * - 分类名称（模糊查询）
     * - 状态（精确查询）
     * - 创建时间范围
     * 
     * @param listReqVO 查询条件
     * @return 分类完整列表（扁平结构）
     */
    List<GoodsCategoryDO> getGoodsCategoryList(GoodsCategoryPageReqVO listReqVO);

    /**
     * 获取商品分类简单列表（不分页，无条件）
     * 
     * 说明：
     * - 返回所有启用的分类
     * - 主要用于前端下拉框选择
     * - 前端可根据 parentId 和 level 构建树形结构
     * 
     * @return 分类完整列表
     */
    List<GoodsCategoryDO> getGoodsCategorySimpleList();

    /**
     * 批量获取商品分类Map
     * 
     * 说明：
     * - 根据分类ID列表批量查询分类信息
     * - 返回 Map<分类ID, 分类DO>
     * - 主要用于数据关联查询优化（如商品列表关联分类名称）
     * - 如果某个ID对应的分类不存在，Map中不会包含该键值对
     * 
     * @param ids 分类ID列表
     * @return 分类Map，Key为分类ID，Value为分类DO
     */
    Map<Long, GoodsCategoryDO> getGoodsCategoryMap(List<Long> ids);

}
