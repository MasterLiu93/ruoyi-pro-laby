package com.laby.module.wms.controller.admin.supplier;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.supplier.vo.SupplierPageReqVO;
import com.laby.module.wms.controller.admin.supplier.vo.SupplierRespVO;
import com.laby.module.wms.controller.admin.supplier.vo.SupplierSaveReqVO;
import com.laby.module.wms.service.supplier.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * 供应商信息 Controller
 * 管理端供应商信息的 CRUD API
 *
 * 功能说明：
 * 1. 供应商的创建、修改、删除、查询
 * 2. 供应商分页列表查询
 * 3. 供应商简化列表查询（用于下拉框）
 * 4. 所有接口都添加了权限控制
 *
 * @author laby
 */
@Tag(name = "管理后台 - 供应商信息")
@RestController
@RequestMapping("/wms/supplier")
@Validated
public class SupplierController {

    @Resource
    private SupplierService supplierService;

    /**
     * 创建供应商
     *
     * @param createReqVO 供应商信息
     * @return 供应商ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建供应商")
    @PreAuthorize("@ss.hasPermission('wms:supplier:create')")
    public CommonResult<Long> createSupplier(@Valid @RequestBody SupplierSaveReqVO createReqVO) {
        return success(supplierService.createSupplier(createReqVO));
    }

    /**
     * 更新供应商
     *
     * @param updateReqVO 供应商信息（必须包含id）
     * @return 是否成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新供应商")
    @PreAuthorize("@ss.hasPermission('wms:supplier:update')")
    public CommonResult<Boolean> updateSupplier(@Valid @RequestBody SupplierSaveReqVO updateReqVO) {
        supplierService.updateSupplier(updateReqVO);
        return success(true);
    }

    /**
     * 删除供应商
     *
     * @param id 供应商ID
     * @return 是否成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除供应商")
    @Parameter(name = "id", description = "供应商ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:supplier:delete')")
    public CommonResult<Boolean> deleteSupplier(@RequestParam("id") Long id) {
        supplierService.deleteSupplier(id);
        return success(true);
    }

    /**
     * 获得供应商详情
     *
     * @param id 供应商ID
     * @return 供应商详情
     */
    @GetMapping("/get")
    @Operation(summary = "获得供应商详情")
    @Parameter(name = "id", description = "供应商ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:supplier:query')")
    public CommonResult<SupplierRespVO> getSupplier(@RequestParam("id") Long id) {
        return success(supplierService.getSupplier(id));
    }

    /**
     * 获得供应商分页列表
     *
     * @param pageReqVO 分页查询参数
     * @return 分页列表
     */
    @GetMapping("/page")
    @Operation(summary = "获得供应商分页列表")
    @PreAuthorize("@ss.hasPermission('wms:supplier:query')")
    public CommonResult<PageResult<SupplierRespVO>> getSupplierPage(@Valid SupplierPageReqVO pageReqVO) {
        return success(supplierService.getSupplierPage(pageReqVO));
    }

    /**
     * 获得供应商简化列表（用于下拉框）
     *
     * @return 供应商简化列表
     */
    @GetMapping("/simple-list")
    @Operation(summary = "获得供应商简化列表")
    @PreAuthorize("@ss.hasPermission('wms:supplier:query')")
    public CommonResult<List<SupplierRespVO>> getSupplierSimpleList() {
        return success(supplierService.getSupplierSimpleList());
    }
}

