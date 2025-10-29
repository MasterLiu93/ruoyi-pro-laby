package com.laby.module.wms.service.goods;

import cn.hutool.core.collection.CollUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.module.wms.controller.admin.goods.vo.goods.GoodsPageReqVO;
import com.laby.module.wms.controller.admin.goods.vo.goods.GoodsSaveReqVO;
import com.laby.module.wms.convert.goods.GoodsConvert;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.dal.mysql.goods.GoodsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.module.wms.enums.ErrorCodeConstants.GOODS_NOT_EXISTS;

/**
 * 商品信息 Service 实现类
 * 
 * 实现说明：
 * - 所有公共方法都经过参数校验（@Validated）
 * - 创建商品前会校验SKU编码的唯一性（TODO：待完善）
 * - 创建/更新时会校验商品分类是否存在（如果设置了分类）（TODO：待完善）
 * - 删除商品前会校验是否有库存和订单（TODO：待库存和订单模块实现后补全）
 * - 使用 MapStruct 进行对象转换（GoodsConvert）
 * - 异常统一使用 ServiceException，错误码定义在 ErrorCodeConstants 中
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    /**
     * 创建商品
     *
     * 实现步骤：
     * 1. 校验SKU编码唯一性（TODO：待完善）
     * 2. 校验商品分类是否存在（TODO：待完善）
     * 3. 使用 MapStruct 转换 VO 为 DO
     * 4. 插入数据库（MyBatis Plus 自动设置 ID、创建时间等）
     * 5. 返回新生成的商品ID
     */
    @Override
    public Long createGoods(GoodsSaveReqVO createReqVO) {
        // TODO: 校验SKU编码唯一性
        // TODO: 校验商品分类是否存在
        
        GoodsDO goods = GoodsConvert.INSTANCE.convert(createReqVO);
        goodsMapper.insert(goods);
        return goods.getId();
    }

    /**
     * 更新商品
     *
     * 实现步骤：
     * 1. 校验商品是否存在
     * 2. 校验SKU编码唯一性（排除自己）（TODO：待完善）
     * 3. 校验商品分类是否存在（TODO：待完善）
     * 4. 使用 MapStruct 转换 VO 为 DO
     * 5. 更新数据库（MyBatis Plus 自动更新修改时间等）
     */
    @Override
    public void updateGoods(GoodsSaveReqVO updateReqVO) {
        validateGoodsExists(updateReqVO.getId());
        
        // TODO: 校验SKU编码唯一性
        // TODO: 校验商品分类是否存在
        
        GoodsDO updateObj = GoodsConvert.INSTANCE.convert(updateReqVO);
        goodsMapper.updateById(updateObj);
    }

    /**
     * 删除商品
     *
     * 实现步骤：
     * 1. 校验商品是否存在
     * 2. 校验商品下是否有库存（如果有，不允许删除）（TODO：待库存模块实现后补全）
     * 3. 校验商品是否在订单中（如果有，不允许删除）（TODO：待订单模块实现后补全）
     * 4. 逻辑删除（deleted 字段）
     */
    @Override
    public void deleteGoods(Long id) {
        validateGoodsExists(id);
        
        // TODO: 校验是否有库存
        // TODO: 校验是否在订单中
        
        goodsMapper.deleteById(id);
    }

    /**
     * 校验商品是否存在（私有方法）
     *
     * @param id 商品ID
     * @throws com.laby.framework.common.exception.ServiceException 如果商品不存在
     */
    private void validateGoodsExists(Long id) {
        if (goodsMapper.selectById(id) == null) {
            throw exception(GOODS_NOT_EXISTS);
        }
    }

    /**
     * 获取商品详情
     *
     * @param id 商品ID
     * @return 商品信息，如果不存在返回null
     */
    @Override
    public GoodsDO getGoods(Long id) {
        return goodsMapper.selectById(id);
    }

    /**
     * 获取商品分页列表
     *
     * @param pageReqVO 分页查询参数
     * @return 商品分页数据
     */
    @Override
    public PageResult<GoodsDO> getGoodsPage(GoodsPageReqVO pageReqVO) {
        return goodsMapper.selectPage(pageReqVO);
    }

    /**
     * 获取商品列表（不分页）
     *
     * 说明：
     * - 返回所有启用的商品
     * - 主要用于前端下拉框选择
     *
     * @return 商品完整列表
     */
    @Override
    public List<GoodsDO> getGoodsList() {
        return goodsMapper.selectList();
    }

    /**
     * 批量获取商品Map
     *
     * 实现说明：
     * - 如果ID集合为空，返回空Map
     * - 使用 MyBatis Plus 的 selectBatchIds 批量查询
     * - 使用 CollectionUtils.convertMap 转换为 Map
     * - 主要用于数据关联查询优化（如库存列表关联商品名称）
     *
     * @param ids 商品ID列表
     * @return 商品Map，Key为商品ID，Value为商品DO
     */
    @Override
    public Map<Long, GoodsDO> getGoodsMap(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Map.of();
        }
        return CollectionUtils.convertMap(goodsMapper.selectBatchIds(ids), GoodsDO::getId);
    }

}
