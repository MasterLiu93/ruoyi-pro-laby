package com.laby.module.wms.framework.web.config;

import com.laby.framework.swagger.config.LabySwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WMS 模块的 web 组件的 Configuration
 * 
 * 功能说明：
 * 1. 配置 Swagger/Knife4j 的 API 分组
 * 2. WMS 模块包含仓库管理、商品管理、库存管理、出入库管理等功能
 * 3. 所有 WMS 相关的接口都会在 Swagger UI 中的 "wms" 分组中展示
 *
 * @author laby
 */
@Configuration(proxyBeanMethods = false)
public class WmsWebConfiguration {

    /**
     * WMS 模块的 API 分组
     * 
     * 配置说明：
     * - 分组名称：wms
     * - 扫描路径：/admin-api/wms/** 和 /app-api/wms/**
     * - 自动添加租户ID和认证Token请求头参数
     * 
     * @return WMS 模块的 API 分组配置
     */
    @Bean
    public GroupedOpenApi wmsGroupedOpenApi() {
        return LabySwaggerAutoConfiguration.buildGroupedOpenApi("wms");
    }

}
