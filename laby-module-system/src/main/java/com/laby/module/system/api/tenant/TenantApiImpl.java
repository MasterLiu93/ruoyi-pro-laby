package com.laby.module.system.api.tenant;

import com.laby.framework.common.biz.system.tenant.TenantCommonApi;
import com.laby.module.system.service.tenant.TenantService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 多租户的 API 实现类
 *
 * @author Laby
 */
@Service
public class TenantApiImpl implements TenantCommonApi {

    @Resource
    private TenantService tenantService;

    @Override
    public List<Long> getTenantIdList() {
        return tenantService.getTenantIdList();
    }

    @Override
    public void validateTenant(Long id) {
        tenantService.validTenant(id);
    }

}
