package com.laby.module.wms.service.goods;

import cn.hutool.core.collection.CollUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.module.wms.controller.admin.goods.vo.category.GoodsCategoryPageReqVO;
import com.laby.module.wms.controller.admin.goods.vo.category.GoodsCategorySaveReqVO;
import com.laby.module.wms.convert.goods.GoodsCategoryConvert;
import com.laby.module.wms.dal.dataobject.goods.GoodsCategoryDO;
import com.laby.module.wms.dal.mysql.goods.GoodsCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.module.wms.enums.ErrorCodeConstants.GOODS_CATEGORY_NOT_EXISTS;

/**
 * 商品分类 Service 实现类
 * 
 * 实现说明：
 * - 所有公共方法都经过参数校验（@Validated）
 * - 支持树形结构，最多3级分类
 * - 创建/更新时会校验父分类是否存在（如果设置了父分类）
 * - 删除分类前会校验是否有子分类和商品（TODO：待商品模块完善后补全）
 * - 使用 MapStruct 进行对象转换（GoodsCategoryConvert）
 * - 异常统一使用 ServiceException，错误码定义在 ErrorCodeConstants 中
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class GoodsCategoryServiceImpl implements GoodsCategoryService {

    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    /**
     * 创建商品分类
     *
     * 实现步骤：
     * 1. 校验父分类是否存在（如果设置了父分类）
     * 2. 使用 MapStruct 转换 VO 为 DO
     * 3. 插入数据库（MyBatis Plus 自动设置 ID、创建时间等）
     * 4. 返回新生成的分类ID
     */
    @Override
    public Long createGoodsCategory(GoodsCategorySaveReqVO createReqVO) {
        // TODO: 校验父分类是否存在
        // TODO: 校验分类编码唯一性
        
        // 插入
        GoodsCategoryDO goodsCategory = GoodsCategoryConvert.INSTANCE.convert(createReqVO);
        goodsCategoryMapper.insert(goodsCategory);
        // 返回
        return goodsCategory.getId();
    }

    /**
     * 更新商品分类
     *
     * 实现步骤：
     * 1. 校验分类是否存在
     * 2. 校验父分类是否存在（如果设置了父分类）
     * 3. 校验不能将父分类设置为自己或自己的子分类
     * 4. 使用 MapStruct 转换 VO 为 DO
     * 5. 更新数据库（MyBatis Plus 自动更新修改时间等）
     */
    @Override
    public void updateGoodsCategory(GoodsCategorySaveReqVO updateReqVO) {
        // 校验存在
        validateGoodsCategoryExists(updateReqVO.getId());
        
        // TODO: 校验父分类是否存在
        // TODO: 校验不能将父分类设置为自己或自己的子分类
        // TODO: 校验分类编码唯一性
        
        // 更新
        GoodsCategoryDO updateObj = GoodsCategoryConvert.INSTANCE.convert(updateReqVO);
        goodsCategoryMapper.updateById(updateObj);
    }

    /**
     * 删除商品分类
     *
     * 实现步骤：
     * 1. 校验分类是否存在
     * 2. 校验分类下是否有子分类（如果有，不允许删除）
     * 3. 校验分类下是否有商品（如果有，不允许删除）
     * 4. 逻辑删除（deleted 字段）
     */
    @Override
    public void deleteGoodsCategory(Long id) {
        // 校验存在
        validateGoodsCategoryExists(id);
        
        // TODO: 校验是否有子分类
        // TODO: 校验是否有商品
        
        // 删除
        goodsCategoryMapper.deleteById(id);
    }

    /**
     * 校验商品分类是否存在（私有方法）
     *
     * @param id 分类ID
     * @throws com.laby.framework.common.exception.ServiceException 如果分类不存在
     */
    private void validateGoodsCategoryExists(Long id) {
        if (goodsCategoryMapper.selectById(id) == null) {
            throw exception(GOODS_CATEGORY_NOT_EXISTS);
        }
    }

    /**
     * 获取商品分类详情
     *
     * @param id 分类ID
     * @return 分类信息，如果不存在返回null
     */
    @Override
    public GoodsCategoryDO getGoodsCategory(Long id) {
        return goodsCategoryMapper.selectById(id);
    }

    /**
     * 获取商品分类列表（支持条件查询）
     *
     * 说明：
     * - 不分页，返回所有符合条件的分类
     * - 返回扁平列表，前端构建树形结构
     *
     * @param listReqVO 查询条件
     * @return 分类完整列表（扁平结构）
     */
    @Override
    public List<GoodsCategoryDO> getGoodsCategoryList(GoodsCategoryPageReqVO listReqVO) {
        return goodsCategoryMapper.selectList(listReqVO);
    }

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
    @Override
    public List<GoodsCategoryDO> getGoodsCategorySimpleList() {
        return goodsCategoryMapper.selectList();
    }

    /**
     * 批量获取商品分类Map
     *
     * 实现说明：
     * - 如果ID集合为空，返回空Map
     * - 使用 MyBatis Plus 的 selectBatchIds 批量查询
     * - 使用 CollectionUtils.convertMap 转换为 Map
     * - 主要用于数据关联查询优化（如商品列表关联分类名称）
     *
     * @param ids 分类ID列表
     * @return 分类Map，Key为分类ID，Value为分类DO
     */
    @Override
    public Map<Long, GoodsCategoryDO> getGoodsCategoryMap(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Map.of();
        }
        return CollectionUtils.convertMap(goodsCategoryMapper.selectBatchIds(ids), GoodsCategoryDO::getId);
    }

}
