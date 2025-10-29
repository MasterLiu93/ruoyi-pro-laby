package com.laby.module.wms.dal.mysql.goods;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.goods.vo.category.GoodsCategoryPageReqVO;
import com.laby.module.wms.dal.dataobject.goods.GoodsCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品分类 Mapper
 * 数据访问层，使用 MyBatis Plus
 * 
 * 功能说明：
 * - 继承 BaseMapperX，提供基础的 CRUD 方法
 * - 使用 LambdaQueryWrapperX 构建动态查询条件
 * - 只返回 DO 对象，不返回 VO（关联查询在 Service 层处理）
 * - 支持树形结构查询，通过 parentId 关联父分类
 * - 支持查询所有分类列表（用于前端构建树形结构）
 * - 支持多租户自动隔离（由 TenantLineHandler 拦截器实现）
 *
 * @author laby
 */
@Mapper
public interface GoodsCategoryMapper extends BaseMapperX<GoodsCategoryDO> {

    /**
     * 查询商品分类列表（支持条件查询）
     *
     * 说明：
     * - 不分页，返回所有符合条件的分类
     * - 返回扁平列表，前端构建树形结构
     *
     * 支持的查询条件：
     * - 分类编码（模糊查询）
     * - 分类名称（模糊查询）
     * - 状态（精确查询）
     * - 创建时间范围（区间查询）
     *
     * 排序规则：
     * - 先按排序字段升序
     * - 再按 ID 升序
     *
     * @param reqVO 查询条件
     * @return 商品分类列表（扁平结构）
     */
    default List<GoodsCategoryDO> selectList(GoodsCategoryPageReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<GoodsCategoryDO>()
                .likeIfPresent(GoodsCategoryDO::getCategoryCode, reqVO.getCategoryCode())
                .likeIfPresent(GoodsCategoryDO::getCategoryName, reqVO.getCategoryName())
                .eqIfPresent(GoodsCategoryDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(GoodsCategoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(GoodsCategoryDO::getSort)
                .orderByAsc(GoodsCategoryDO::getId));
    }

    /**
     * 查询所有商品分类列表（不分页，无条件）
     *
     * 说明：
     * - 返回所有启用的分类
     * - 主要用于前端下拉框选择
     * - 前端可根据 parentId 和 level 构建树形结构
     *
     * 排序规则：
     * - 先按排序字段升序
     * - 再按 ID 升序
     *
     * @return 所有商品分类列表
     */
    default List<GoodsCategoryDO> selectList() {
        return selectList(new LambdaQueryWrapperX<GoodsCategoryDO>()
                .orderByAsc(GoodsCategoryDO::getSort)
                .orderByAsc(GoodsCategoryDO::getId));
    }

}
