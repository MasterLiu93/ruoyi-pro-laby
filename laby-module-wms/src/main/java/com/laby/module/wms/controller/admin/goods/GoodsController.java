package com.laby.module.wms.controller.admin.goods;

import com.laby.framework.apilog.core.annotation.ApiAccessLog;
import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.goods.vo.goods.GoodsPageReqVO;
import com.laby.module.wms.controller.admin.goods.vo.goods.GoodsRespVO;
import com.laby.module.wms.controller.admin.goods.vo.goods.GoodsSaveReqVO;
import com.laby.module.wms.convert.goods.GoodsConvert;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.dal.dataobject.goods.GoodsCategoryDO;
import com.laby.module.wms.service.goods.GoodsService;
import com.laby.module.wms.service.goods.GoodsCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Map;
import static com.laby.framework.apilog.core.enums.OperateTypeEnum.*;
import static com.laby.framework.common.pojo.CommonResult.success;
import static com.laby.framework.common.util.collection.CollectionUtils.convertSet;

/**
 * 商品信息管理 Controller
 * 提供商品信息的增删改查功能
 * 
 * 功能说明：
 * - 商品是WMS系统的核心主数据，记录商品的详细信息
 * - 商品信息包括：SKU编码、名称、分类、品牌、型号、条形码等
 * - 支持计量单位、规格、重量、体积等物理属性
 * - 支持保质期、存储温度等存储要求
 * - 支持批次管理（食品、药品）和序列号管理（贵重物品）
 * - 支持安全库存和最大库存设置，用于库存告警
 * - 自动关联并显示分类名称
 * - 支持多租户隔离
 * 
 * @author laby
 */
@Tag(name = "管理后台 - 商品信息")
@RestController
@RequestMapping("/wms/goods")
@Validated
public class GoodsController {

    @Resource
    private GoodsService goodsService;
    
    @Resource
    private GoodsCategoryService goodsCategoryService;

    /**
     * 创建商品
     * 
     * 注意事项：
     * - 创建前会校验SKU编码是否重复
     * - 如果设置了分类，会校验分类是否存在
     * - 计量单位、状态为必填字段
     * - 启用批次管理或序列号管理后，入库时需要相应的批次号或序列号
     * 
     * @param createReqVO 商品创建信息，包括SKU编码、名称、分类、规格等
     * @return 创建成功的商品ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建商品")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('wms:goods:create')")
    public CommonResult<Long> createGoods(@Valid @RequestBody GoodsSaveReqVO createReqVO) {
        return success(goodsService.createGoods(createReqVO));
    }

    /**
     * 更新商品
     * 
     * 注意事项：
     * - 更新前会校验商品是否存在
     * - SKU编码不能与其他商品重复
     * - 如果商品已有库存，某些字段（如批次管理、序列号管理）可能受限
     * 
     * @param updateReqVO 商品更新信息，必须包含id
     * @return 是否更新成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新商品")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('wms:goods:update')")
    public CommonResult<Boolean> updateGoods(@Valid @RequestBody GoodsSaveReqVO updateReqVO) {
        goodsService.updateGoods(updateReqVO);
        return success(true);
    }

    /**
     * 删除商品
     * 
     * 注意事项：
     * - 删除前会校验商品是否有库存
     * - 有库存的商品不能删除
     * - 删除前会校验商品是否在入库单、出库单中
     * - 逻辑删除，不会真正删除数据
     * 
     * @param id 商品ID
     * @return 是否删除成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除商品")
    @Parameter(name = "id", description = "商品ID", required = true, example = "1")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('wms:goods:delete')")
    public CommonResult<Boolean> deleteGoods(@RequestParam("id") Long id) {
        goodsService.deleteGoods(id);
        return success(true);
    }

    /**
     * 获取商品详情
     * 
     * 数据关联：
     * - 自动查询并填充分类名称（categoryName）
     * 
     * @param id 商品ID
     * @return 商品详细信息（包含分类名称）
     */
    @GetMapping("/get")
    @Operation(summary = "获得商品详情")
    @Parameter(name = "id", description = "商品ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:goods:query')")
    public CommonResult<GoodsRespVO> getGoods(@RequestParam("id") Long id) {
        GoodsDO goods = goodsService.getGoods(id);
        GoodsRespVO respVO = GoodsConvert.INSTANCE.convert(goods);
        // 关联分类名称
        if (respVO != null && respVO.getCategoryId() != null) {
            GoodsCategoryDO category = goodsCategoryService.getGoodsCategory(respVO.getCategoryId());
            if (category != null) {
                respVO.setCategoryName(category.getCategoryName());
            }
        }
        return success(respVO);
    }

    /**
     * 获取商品分页列表
     * 
     * 支持的查询条件：
     * - SKU编码（模糊查询）
     * - 商品名称（模糊查询）
     * - 商品分类ID（精确查询）
     * - 品牌（模糊查询）
     * - 条形码（精确查询）
     * - 状态（精确查询：启用、禁用）
     * - 创建时间范围
     * 
     * 数据关联：
     * - 自动查询并填充分类名称（categoryName）
     * - 使用批量查询优化性能
     * 
     * @param pageReqVO 分页查询参数
     * @return 商品分页数据（包含分类名称）
     */
    @GetMapping("/page")
    @Operation(summary = "获得商品分页列表")
    @PreAuthorize("@ss.hasPermission('wms:goods:query')")
    public CommonResult<PageResult<GoodsRespVO>> getGoodsPage(@Valid GoodsPageReqVO pageReqVO) {
        PageResult<GoodsDO> pageResult = goodsService.getGoodsPage(pageReqVO);
        // 关联分类名称
        Map<Long, GoodsCategoryDO> categoryMap = goodsCategoryService.getGoodsCategorySimpleList().stream()
                .collect(java.util.stream.Collectors.toMap(GoodsCategoryDO::getId, v -> v, (v1, v2) -> v1));
        PageResult<GoodsRespVO> result = GoodsConvert.INSTANCE.convertPage(pageResult, categoryMap);
        return success(result);
    }

}

