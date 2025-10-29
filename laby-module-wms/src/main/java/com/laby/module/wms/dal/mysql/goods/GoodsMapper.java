package com.laby.module.wms.dal.mysql.goods;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.goods.vo.goods.GoodsPageReqVO;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 商品信息 Mapper
 * 数据访问层，使用 MyBatis Plus
 * 
 * 功能说明：
 * - 继承 BaseMapperX，提供基础的 CRUD 方法
 * - 使用 LambdaQueryWrapperX 构建动态查询条件
 * - 只返回 DO 对象，不返回 VO（关联查询在 Service 层处理）
 * - 支持按分类ID查询商品列表
 * - 支持多条件组合查询（SKU编码、名称、分类、品牌、条形码、状态等）
 * - 支持多租户自动隔离（由 TenantLineHandler 拦截器实现）
 *
 * @author laby
 */
@Mapper
public interface GoodsMapper extends BaseMapperX<GoodsDO> {

    /**
     * 分页查询商品列表
     *
     * 支持的查询条件：
     * - SKU编码（模糊查询）
     * - 商品名称（模糊查询）
     * - 商品分类ID（精确查询）
     * - 品牌（模糊查询）
     * - 条形码（精确查询）
     * - 状态（精确查询：启用、禁用）
     * - 创建时间范围（区间查询）
     *
     * 排序规则：
     * - 按 ID 降序
     *
     * @param reqVO 分页查询参数
     * @return 商品分页数据
     */
    default PageResult<GoodsDO> selectPage(GoodsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<GoodsDO>()
                .likeIfPresent(GoodsDO::getSkuCode, reqVO.getSkuCode())
                .likeIfPresent(GoodsDO::getGoodsName, reqVO.getGoodsName())
                .eqIfPresent(GoodsDO::getCategoryId, reqVO.getCategoryId())
                .likeIfPresent(GoodsDO::getBrand, reqVO.getBrand())
                .eqIfPresent(GoodsDO::getBarcode, reqVO.getBarcode())
                .eqIfPresent(GoodsDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(GoodsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(GoodsDO::getId));
    }

    /**
     * 查询指定分类下的所有商品
     *
     * 说明：
     * - 主要用于删除分类前的关联数据校验
     * - 如果该分类下有商品，不允许删除分类
     *
     * @param categoryId 商品分类ID
     * @return 该分类下的所有商品列表
     */
    default List<GoodsDO> selectListByCategoryId(Long categoryId) {
        return selectList(new LambdaQueryWrapperX<GoodsDO>()
                .eq(GoodsDO::getCategoryId, categoryId));
    }

}
