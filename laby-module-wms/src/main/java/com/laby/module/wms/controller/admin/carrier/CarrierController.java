package com.laby.module.wms.controller.admin.carrier;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.carrier.vo.CarrierPageReqVO;
import com.laby.module.wms.controller.admin.carrier.vo.CarrierRespVO;
import com.laby.module.wms.controller.admin.carrier.vo.CarrierSaveReqVO;
import com.laby.module.wms.service.carrier.CarrierService;
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
 * 承运商信息 Controller
 * 
 * 功能说明：
 * - 提供承运商的 CRUD REST API
 * - 支持分页查询、详情查询、简单列表查询
 * - 权限标识：wms:carrier:query、create、update、delete
 * 
 * @author laby
 */
@Tag(name = "管理后台 - 承运商信息")
@RestController
@RequestMapping("/wms/carrier")
@Validated
public class CarrierController {

    @Resource
    private CarrierService carrierService;

    /**
     * 创建承运商
     * 
     * @param createReqVO 创建信息
     * @return 承运商ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建承运商")
    @PreAuthorize("@ss.hasPermission('wms:carrier:create')")
    public CommonResult<Long> createCarrier(@Valid @RequestBody CarrierSaveReqVO createReqVO) {
        return success(carrierService.createCarrier(createReqVO));
    }

    /**
     * 更新承运商
     * 
     * @param updateReqVO 更新信息
     * @return 是否成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新承运商")
    @PreAuthorize("@ss.hasPermission('wms:carrier:update')")
    public CommonResult<Boolean> updateCarrier(@Valid @RequestBody CarrierSaveReqVO updateReqVO) {
        carrierService.updateCarrier(updateReqVO);
        return success(true);
    }

    /**
     * 删除承运商
     * 
     * @param id 承运商ID
     * @return 是否成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除承运商")
    @Parameter(name = "id", description = "承运商ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:carrier:delete')")
    public CommonResult<Boolean> deleteCarrier(@RequestParam("id") Long id) {
        carrierService.deleteCarrier(id);
        return success(true);
    }

    /**
     * 获得承运商详情
     * 
     * @param id 承运商ID
     * @return 承运商详情
     */
    @GetMapping("/get")
    @Operation(summary = "获得承运商详情")
    @Parameter(name = "id", description = "承运商ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:carrier:query')")
    public CommonResult<CarrierRespVO> getCarrier(@RequestParam("id") Long id) {
        return success(carrierService.getCarrier(id));
    }

    /**
     * 获得承运商分页列表
     * 
     * @param pageReqVO 分页查询条件
     * @return 分页列表
     */
    @GetMapping("/page")
    @Operation(summary = "获得承运商分页列表")
    @PreAuthorize("@ss.hasPermission('wms:carrier:query')")
    public CommonResult<PageResult<CarrierRespVO>> getCarrierPage(@Valid CarrierPageReqVO pageReqVO) {
        return success(carrierService.getCarrierPage(pageReqVO));
    }

    /**
     * 获得承运商简单列表（用于下拉选择）
     * 
     * @return 简单列表
     */
    @GetMapping("/simple-list")
    @Operation(summary = "获得承运商简单列表")
    @PreAuthorize("@ss.hasPermission('wms:carrier:query')")
    public CommonResult<List<CarrierRespVO>> getCarrierSimpleList() {
        return success(carrierService.getCarrierSimpleList());
    }
}

