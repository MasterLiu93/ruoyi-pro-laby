package com.laby.module.wms.controller.admin.customer;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.customer.vo.CustomerPageReqVO;
import com.laby.module.wms.controller.admin.customer.vo.CustomerRespVO;
import com.laby.module.wms.controller.admin.customer.vo.CustomerSaveReqVO;
import com.laby.module.wms.service.customer.CustomerService;
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
 * 客户信息 Controller
 * 管理端客户信息的 CRUD API
 *
 * 功能说明：
 * 1. 客户的创建、修改、删除、查询
 * 2. 客户分页列表查询
 * 3. 客户简化列表查询（用于下拉框）
 * 4. 所有接口都添加了权限控制
 *
 * @author laby
 */
@Tag(name = "管理后台 - 客户信息")
@RestController
@RequestMapping("/wms/customer")
@Validated
public class CustomerController {

    @Resource
    private CustomerService customerService;

    /**
     * 创建客户
     *
     * @param createReqVO 客户信息
     * @return 客户ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建客户")
    @PreAuthorize("@ss.hasPermission('wms:customer:create')")
    public CommonResult<Long> createCustomer(@Valid @RequestBody CustomerSaveReqVO createReqVO) {
        return success(customerService.createCustomer(createReqVO));
    }

    /**
     * 更新客户
     *
     * @param updateReqVO 客户信息（必须包含id）
     * @return 是否成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新客户")
    @PreAuthorize("@ss.hasPermission('wms:customer:update')")
    public CommonResult<Boolean> updateCustomer(@Valid @RequestBody CustomerSaveReqVO updateReqVO) {
        customerService.updateCustomer(updateReqVO);
        return success(true);
    }

    /**
     * 删除客户
     *
     * @param id 客户ID
     * @return 是否成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除客户")
    @Parameter(name = "id", description = "客户ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:customer:delete')")
    public CommonResult<Boolean> deleteCustomer(@RequestParam("id") Long id) {
        customerService.deleteCustomer(id);
        return success(true);
    }

    /**
     * 获得客户详情
     *
     * @param id 客户ID
     * @return 客户详情
     */
    @GetMapping("/get")
    @Operation(summary = "获得客户详情")
    @Parameter(name = "id", description = "客户ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('wms:customer:query')")
    public CommonResult<CustomerRespVO> getCustomer(@RequestParam("id") Long id) {
        return success(customerService.getCustomer(id));
    }

    /**
     * 获得客户分页列表
     *
     * @param pageReqVO 分页查询参数
     * @return 分页列表
     */
    @GetMapping("/page")
    @Operation(summary = "获得客户分页列表")
    @PreAuthorize("@ss.hasPermission('wms:customer:query')")
    public CommonResult<PageResult<CustomerRespVO>> getCustomerPage(@Valid CustomerPageReqVO pageReqVO) {
        return success(customerService.getCustomerPage(pageReqVO));
    }

    /**
     * 获得客户简化列表（用于下拉框）
     *
     * @return 客户简化列表
     */
    @GetMapping("/simple-list")
    @Operation(summary = "获得客户简化列表")
    @PreAuthorize("@ss.hasPermission('wms:customer:query')")
    public CommonResult<List<CustomerRespVO>> getCustomerSimpleList() {
        return success(customerService.getCustomerSimpleList());
    }
}

