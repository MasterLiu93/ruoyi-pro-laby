package com.laby.module.wms.controller.admin.goods;

import com.laby.framework.apilog.core.annotation.ApiAccessLog;
import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageParam;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.goods.vo.category.GoodsCategoryPageReqVO;
import com.laby.module.wms.controller.admin.goods.vo.category.GoodsCategoryRespVO;
import com.laby.module.wms.controller.admin.goods.vo.category.GoodsCategorySaveReqVO;
import com.laby.module.wms.convert.goods.GoodsCategoryConvert;
import com.laby.module.wms.dal.dataobject.goods.GoodsCategoryDO;
import com.laby.module.wms.service.goods.GoodsCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static com.laby.framework.apilog.core.enums.OperateTypeEnum.*;
import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * 商品分类管理 Controller
 * 提供商品分类的增删改查功能
 * 
 * 功能说明：
 * - 商品分类用于对商品进行分类管理，支持树形结构
 * - 最多支持3级分类（一级分类→二级分类→三级分类）
 * - 通过 parentId 关联父分类，实现层级关系
 * - 分类信息包括编码、名称、父分类ID、层级、排序等
 * - 支持多租户隔离
 * 
 * @author laby
 */
@Tag(name = "管理后台 - 商品分类")
@RestController
@RequestMapping("/wms/goods-category")
@Validated
public class GoodsCategoryController {

    @Resource
    private GoodsCategoryService goodsCategoryService;

    /**
     * 创建商品分类
     * 
     * 注意事项：
     * - 创建前会校验分类编码是否重复
     * - 如果设置了父分类，会校验父分类是否存在
     * - 层级（level）用于限制分类深度，最多3级
     * 
     * @param createReqVO 分类创建信息，包括编码、名称、父分类ID、层级等
     * @return 创建成功的分类ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建商品分类")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('wms:category:create')")
    public CommonResult<Long> createGoodsCategory(@Valid @RequestBody GoodsCategorySaveReqVO createReqVO) {
        return success(goodsCategoryService.createGoodsCategory(createReqVO));
    }

    /**
     * 更新商品分类
     * 
     * 注意事项：
     * - 更新前会校验分类是否存在
     * - 分类编码不能与其他分类重复
     * - 不能将父分类设置为自己或自己的子分类（避免循环引用）
     * 
     * @param updateReqVO 分类更新信息，必须包含id
     * @return 是否更新成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新商品分类")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('wms:category:update')")
    public CommonResult<Boolean> updateGoodsCategory(@Valid @RequestBody GoodsCategorySaveReqVO updateReqVO) {
        goodsCategoryService.updateGoodsCategory(updateReqVO);
        return success(true);
    }

    /**
     * 删除商品分类
     * 
     * 注意事项：
     * - 删除前会校验分类是否存在子分类
     * - 删除前会校验分类下是否有商品
     * - 有子分类或商品的分类不能删除
     * - 逻辑删除，不会真正删除数据
     * 
     * @param id 分类ID
     * @return 是否删除成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除商品分类")
    @Parameter(name = "id", description = "分类ID", required = true, example = "1")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('wms:category:delete')")
    public CommonResult<Boolean> deleteGoodsCategory(@RequestParam("id") Long id) {
        goodsCategoryService.deleteGoodsCategory(id);
        return success(true);
    }

    /**
     * 获取商品分类详情
     * 
     * @param id 分类ID
     * @return 分类详细信息
     */
    @GetMapping("/get")
    @Operation(summary = "获得商品分类详情")
    @Parameter(name = "id", description = "分类ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:category:query')")
    public CommonResult<GoodsCategoryRespVO> getGoodsCategory(@RequestParam("id") Long id) {
        GoodsCategoryDO goodsCategory = goodsCategoryService.getGoodsCategory(id);
        return success(GoodsCategoryConvert.INSTANCE.convert(goodsCategory));
    }

    /**
     * 获取商品分类列表（树形表格）
     * 
     * 说明：
     * - 不分页，返回所有分类的扁平列表
     * - 前端根据 parentId 和 level 构建树形结构
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
    @GetMapping("/list")
    @Operation(summary = "获得商品分类列表", description = "返回扁平列表，前端构建树形结构")
    @PreAuthorize("@ss.hasPermission('wms:category:query')")
    public CommonResult<List<GoodsCategoryRespVO>> getGoodsCategoryList(@Valid GoodsCategoryPageReqVO listReqVO) {
        List<GoodsCategoryDO> list = goodsCategoryService.getGoodsCategoryList(listReqVO);
        return success(GoodsCategoryConvert.INSTANCE.convertList(list));
    }

    /**
     * 获取商品分类简单列表（用于下拉框）
     * 
     * 说明：
     * - 返回所有启用的分类
     * - 不分页，返回完整列表
     * - 主要用于前端下拉框选择
     * - 前端可根据 parentId 和 level 构建树形结构
     * 
     * @return 分类完整列表
     */
    @GetMapping("/simple-list")
    @Operation(summary = "获得商品分类简单列表", description = "返回所有分类，主要用于前端下拉选项")
    @PreAuthorize("@ss.hasPermission('wms:category:query')")
    public CommonResult<List<GoodsCategoryRespVO>> getGoodsCategorySimpleList() {
        List<GoodsCategoryDO> list = goodsCategoryService.getGoodsCategorySimpleList();
        return success(GoodsCategoryConvert.INSTANCE.convertList(list));
    }

}

