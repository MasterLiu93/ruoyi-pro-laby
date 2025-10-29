package com.laby.framework.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.ClassUtils;

import java.util.concurrent.TimeUnit;

/**
 * 项目启动成功后，提供文档相关的地址
 *
 * @author Laby
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        ThreadUtil.execute(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS); // 延迟 1 秒，保证输出到结尾
            
            System.out.println("\n");
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║                                                               ║");
            System.out.println("║                   🚀 Laby WMS 启动成功                        ║");
            System.out.println("║                                                               ║");
            System.out.println("╠═══════════════════════════════════════════════════════════════╣");
            System.out.println("║                                                               ║");
            System.out.println("║  📚 接口文档:  http://localhost:8080/doc.html                ║");
            System.out.println("║  🌐 前端地址:  http://localhost:80                            ║");
            System.out.println("║  ⚙️  后端地址:  http://localhost:8080                          ║");
            System.out.println("║  💻 监控中心:  http://localhost:8080/druid                    ║");
            System.out.println("║                                                               ║");
            System.out.println("╠═══════════════════════════════════════════════════════════════╣");
            System.out.println("║                           模块状态                            ║");
            System.out.println("╠═══════════════════════════════════════════════════════════════╣");
            
            // WMS 核心模块
            if (isNotPresent("com.laby.module.wms.framework.web.config.WmsWebConfiguration")) {
                System.out.println("║  ⚠️  WMS 仓储模块 (laby-module-wms)          [已禁用]        ║");
            } else {
                System.out.println("║  ✅  WMS 仓储模块 (laby-module-wms)          [已启用]        ║");
            }
            
            // 系统基础模块
            if (isNotPresent("com.laby.module.system.framework.web.config.SystemWebConfiguration")) {
                System.out.println("║  ⚠️  系统管理模块 (laby-module-system)       [已禁用]        ║");
            } else {
                System.out.println("║  ✅  系统管理模块 (laby-module-system)       [已启用]        ║");
            }
            
            // 基础设施模块
            if (isNotPresent("com.laby.module.infra.framework.web.config.InfraWebConfiguration")) {
                System.out.println("║  ⚠️  基础设施模块 (laby-module-infra)        [已禁用]        ║");
            } else {
                System.out.println("║  ✅  基础设施模块 (laby-module-infra)        [已启用]        ║");
            }
            
            System.out.println("╠═══════════════════════════════════════════════════════════════╣");
            System.out.println("║                                                               ║");
            System.out.println("║        🐮 牛马护体 · 永不宕机 · 永无BUG 🚀                   ║");
            System.out.println("║                                                               ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");
        });
    }

    private static boolean isNotPresent(String className) {
        return !ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
    }

}
