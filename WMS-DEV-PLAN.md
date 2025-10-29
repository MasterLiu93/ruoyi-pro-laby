# WMS 仓库管理系统开发计划

## 📋 项目概述

**项目名称**: WMS 仓库管理系统  
**基于框架**: laby-boot (yudao-boot-mini)  
**开发模块**: laby-module-wms  
**开发周期**: 分阶段实现  
**技术栈**: Spring Boot 3.x + MyBatis-Plus + Redis + Vue 3

---

## ✅ 架构确认

### 当前架构状态
- ✅ 基于 laby-boot (RuoYi-Pro-Mini) 框架
- ✅ Admin 功能已实现（用户、角色、权限、部门、字典等）
- ✅ 通用工具已实现（Redis、MyBatis、Excel、Security、Tenant等）
- ✅ 参考模块：laby-module-system

### 开发规范（严格遵循 system 模块）
1. **包结构规范**
```
laby-module-wms/
├── controller/admin/       # 管理后台控制器
│   ├── warehouse/         # 仓库管理
│   ├── goods/            # 商品管理
│   ├── inventory/        # 库存管理
│   ├── inbound/          # 入库管理
│   ├── outbound/         # 出库管理
│   ├── picking/          # 拣货管理
│   ├── stocktaking/      # 盘点管理
│   └── vo/               # VO对象
├── service/               # 服务层
├── dal/                   # 数据访问层
│   ├── dataobject/       # DO对象
│   └── mysql/            # Mapper接口
├── convert/              # 转换器
├── enums/                # 枚举
└── framework/            # 框架配置
```

2. **代码规范**
- Controller: 使用 `@Tag`、`@Operation`、`@PreAuthorize`、返回 `CommonResult`
- Service: 接口 + 实现类，使用 `@Resource` 注入
- DO: 继承 `TenantBaseDO`，使用 `@TableName`、`@TableId`
- Mapper: 继承 `BaseMapperX`，使用 `LambdaQueryWrapperX`
- VO: 分为 `SaveReqVO`、`RespVO`、`ListReqVO`、`PageReqVO` 等
- 使用 `@Schema` 描述，`@Valid` 校验

---

## 🎯 开发阶段规划

### 第一阶段：基础数据模块（核心优先）
**目标**: 完成基础数据管理，为后续业务打基础  
**预估时间**: 3天

#### 1.1 仓库管理模块
- [x] 创建 wms 模块结构
- [ ] 仓库表 (wms_warehouse)
- [ ] 库区表 (wms_warehouse_area)
- [ ] 库位表 (wms_warehouse_location)
- [ ] CRUD接口实现
- [ ] 前端页面开发

**核心功能**:
- 仓库信息维护（增删改查）
- 库区划分管理
- 库位管理（支持批量生成）
- 库位状态管理（空闲、占用、锁定、禁用）

#### 1.2 商品管理模块
- [ ] 商品分类表 (wms_goods_category)
- [ ] 商品信息表 (wms_goods)
- [ ] CRUD接口实现
- [ ] 商品导入导出
- [ ] 前端页面开发

**核心功能**:
- 商品档案管理
- 商品分类管理（树形结构）
- SKU编码规则
- 批次管理配置
- 商品Excel导入

#### 1.3 往来单位管理
- [ ] 供应商表 (wms_supplier)
- [ ] 客户表 (wms_customer)
- [ ] 承运商表 (wms_carrier)
- [ ] CRUD接口实现
- [ ] 前端页面开发

---

### 第二阶段：库存核心模块（重点难点）
**目标**: 实现库存管理核心逻辑  
**预估时间**: 4天

#### 2.1 库存管理
- [ ] 库存表 (wms_inventory)
- [ ] 库存流水表 (wms_inventory_log)
- [ ] 库存快照表 (wms_inventory_snapshot)
- [ ] 库存查询接口
- [ ] 库存锁定/解锁接口
- [ ] 库存扣减接口
- [ ] 库存预警功能
- [ ] 前端库存查询页面

**核心功能**:
- 实时库存查询（总库存、锁定数量、可用库存）
- 库存锁定机制（分布式锁 + 数据库行锁）
- 库存流水记录
- 库存预警（安全库存、库龄预警）
- 库存对账功能

**技术要点**:
- Redis 分布式锁防并发
- 数据库行锁 (FOR UPDATE)
- 乐观锁版本号
- 库存流水异步记录（MQ）

#### 2.2 库存调整
- [ ] 移库单表 (wms_stock_move)
- [ ] 移库接口实现
- [ ] 移库审核流程
- [ ] 前端移库页面

---

### 第三阶段：入库管理模块
**目标**: 完成入库业务闭环  
**预估时间**: 3天

#### 3.1 入库单据管理
- [ ] 入库单主表 (wms_inbound_order)
- [ ] 入库单明细表 (wms_inbound_detail)
- [ ] 创建入库单接口
- [ ] 入库单审核接口
- [ ] 入库单查询接口
- [ ] 前端入库管理页面

**核心功能**:
- 入库单创建（采购入库、退货入库、调拨入库等）
- 入库单审核流程
- 入库单状态流转
- 收货上架功能

#### 3.2 入库执行
- [ ] 收货接口
- [ ] 上架接口
- [ ] 库位分配算法
- [ ] 入库完成接口
- [ ] 更新库存

**业务流程**:
1. 创建入库单 → 审核 → 收货 → 质检 → 上架 → 完成

---

### 第四阶段：出库管理模块
**目标**: 实现出库业务流程  
**预估时间**: 3天

#### 4.1 出库单据管理
- [ ] 出库单主表 (wms_outbound_order)
- [ ] 出库单明细表 (wms_outbound_detail)
- [ ] 创建出库单接口
- [ ] 出库单审核接口
- [ ] 出库单查询接口
- [ ] 前端出库管理页面

**核心功能**:
- 出库单创建（销售出库、调拨出库、报损出库等）
- 出库单审核流程
- 库存锁定
- 出库单状态流转

#### 4.2 出库执行
- [ ] 库存锁定接口
- [ ] 出库完成接口
- [ ] 库存扣减
- [ ] 物流对接

**业务流程**:
1. 创建出库单 → 审核 → 锁定库存 → 拣货 → 复核 → 打包 → 发货 → 扣减库存

---

### 第五阶段：拣货管理模块（智能化）
**目标**: 实现智能拣货调度  
**预估时间**: 4天

#### 5.1 拣货波次管理
- [ ] 拣货波次表 (wms_picking_wave)
- [ ] 波次订单关联表 (wms_picking_wave_order)
- [ ] 拣货任务表 (wms_picking_task)
- [ ] 波次生成算法
- [ ] 前端波次管理页面

**核心功能**:
- 智能波次生成（订单聚合）
- 拣货路径优化（贪心算法）
- 拣货任务分配
- 拣货进度监控

#### 5.2 拣货路径优化
- [ ] 路径优化算法实现
- [ ] 库位距离计算
- [ ] 最短路径规划
- [ ] 路径可视化

**算法实现**:
- 贪心算法（最近邻居）
- 曼哈顿距离计算
- 2-opt 优化

---

### 第六阶段：盘点管理模块
**目标**: 完成库存盘点功能  
**预估时间**: 3天

#### 6.1 盘点计划管理
- [ ] 盘点计划表 (wms_stock_taking_plan)
- [ ] 盘点单表 (wms_stock_taking)
- [ ] 盘点计划创建
- [ ] 盘点任务生成
- [ ] 前端盘点管理页面

**核心功能**:
- 盘点计划创建（全盘、循环盘、抽盘、动态盘）
- 盘点范围配置
- 盘点任务分配
- 差异处理

#### 6.2 盘点执行
- [ ] 盘点记录接口
- [ ] 盘点差异分析
- [ ] 库存调整接口
- [ ] 盘点报告生成

---

### 第七阶段：报表统计模块
**目标**: 数据分析和决策支持  
**预估时间**: 2天

#### 7.1 基础报表
- [ ] 库存汇总报表
- [ ] 出入库统计报表
- [ ] 库存周转率分析
- [ ] 库龄分析报表
- [ ] ABC分类分析

#### 7.2 数据可视化
- [ ] 库存趋势图表
- [ ] 出入库统计图表
- [ ] 库位利用率图表
- [ ] 实时数据大屏

---

### 第八阶段：系统优化与完善
**目标**: 性能优化和功能完善  
**预估时间**: 3天

#### 8.1 性能优化
- [ ] Redis 缓存优化
- [ ] 数据库索引优化
- [ ] SQL 慢查询优化
- [ ] 分页查询优化

#### 8.2 功能完善
- [ ] 操作日志记录
- [ ] 数据权限控制
- [ ] Excel 导入导出
- [ ] 打印功能
- [ ] 移动端 PDA 接口

---

## 📦 数据库表清单（共18张核心表）

### 基础数据表（6张）
1. `wms_warehouse` - 仓库表
2. `wms_warehouse_area` - 库区表
3. `wms_warehouse_location` - 库位表
4. `wms_goods_category` - 商品分类表
5. `wms_goods` - 商品信息表
6. `wms_supplier` - 供应商表
7. `wms_customer` - 客户表
8. `wms_carrier` - 承运商表

### 库存管理表（3张）
9. `wms_inventory` - 库存表
10. `wms_inventory_log` - 库存流水表
11. `wms_inventory_snapshot` - 库存快照表

### 业务单据表（6张）
12. `wms_inbound_order` - 入库单表
13. `wms_inbound_detail` - 入库单明细表
14. `wms_outbound_order` - 出库单表
15. `wms_outbound_detail` - 出库单明细表
16. `wms_stock_move` - 移库单表
17. `wms_stock_taking_plan` - 盘点计划表
18. `wms_stock_taking` - 盘点单表

### 拣货管理表（3张）
19. `wms_picking_wave` - 拣货波次表
20. `wms_picking_wave_order` - 波次订单关联表
21. `wms_picking_task` - 拣货任务表

---

## 🛠️ 技术实现要点

### 1. 库存并发控制
```java
// 分布式锁 + 数据库行锁 + 乐观锁 三重保障
String lockKey = "inventory:lock:" + goodsId;
RLock lock = redissonClient.getLock(lockKey);
try {
    lock.lock(10, TimeUnit.SECONDS);
    // 行锁查询
    Inventory inventory = inventoryMapper.selectForUpdate(goodsId);
    // 乐观锁更新
    inventoryMapper.updateWithVersion(inventory);
} finally {
    lock.unlock();
}
```

### 2. 库存预警机制
```java
@Scheduled(cron = "0 0 * * * ?") // 每小时执行
public void checkInventoryAlert() {
    // 1. 安全库存预警
    List<Inventory> lowStock = inventoryMapper.selectBelowSafetyStock();
    // 2. 库龄预警
    // 3. 临期商品预警
    // 发送预警通知
}
```

### 3. 拣货路径优化
```java
// 贪心算法：最近邻居法
public List<Location> optimizePath(List<Location> locations) {
    List<Location> path = new ArrayList<>();
    Location current = entrance;
    while (!locations.isEmpty()) {
        Location nearest = findNearest(current, locations);
        path.add(nearest);
        locations.remove(nearest);
        current = nearest;
    }
    return path;
}
```

---

## 📝 开发注意事项

### 1. 表命名规范
- WMS业务表统一前缀: `wms_`
- 系统基础表: `system_` (已存在，无需创建)
- 关联字段命名: `xxx_id`、`xxx_name`

### 2. 权限标识规范
```java
@PreAuthorize("@ss.hasPermission('wms:warehouse:create')")
@PreAuthorize("@ss.hasPermission('wms:inventory:query')")
@PreAuthorize("@ss.hasPermission('wms:inbound:audit')")
```

### 3. 接口路径规范
```java
@RequestMapping("/wms/warehouse")      // 仓库管理
@RequestMapping("/wms/inventory")      // 库存管理
@RequestMapping("/wms/inbound")        // 入库管理
@RequestMapping("/wms/outbound")       // 出库管理
@RequestMapping("/wms/picking")        // 拣货管理
```

### 4. 状态枚举规范
```java
public enum InboundStatusEnum {
    PENDING(1, "待审核"),
    APPROVED(2, "待入库"),
    IN_PROGRESS(3, "入库中"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消");
}
```

---

## 🚀 快速开始

### Step 1: 创建模块
```bash
# 在 pom.xml 中添加 wms 模块
<module>laby-module-wms</module>
```

### Step 2: 创建数据库
```sql
-- 执行 SQL 脚本
source sql/mysql/wms_init.sql;
```

### Step 3: 启动开发
```bash
# 启动后端服务
mvn clean install
java -jar laby-server/target/laby-server.jar

# 启动前端
cd laby-ui
npm install
npm run dev
```

---

## 📊 开发进度跟踪

| 阶段 | 模块 | 状态 | 完成度 | 预计完成时间 |
|-----|------|------|-------|------------|
| 阶段一 | 仓库管理 | 🟡 进行中 | 0% | Day 1-3 |
| 阶段一 | 商品管理 | ⚪ 未开始 | 0% | Day 1-3 |
| 阶段二 | 库存管理 | ⚪ 未开始 | 0% | Day 4-7 |
| 阶段三 | 入库管理 | ⚪ 未开始 | 0% | Day 8-10 |
| 阶段四 | 出库管理 | ⚪ 未开始 | 0% | Day 11-13 |
| 阶段五 | 拣货管理 | ⚪ 未开始 | 0% | Day 14-17 |
| 阶段六 | 盘点管理 | ⚪ 未开始 | 0% | Day 18-20 |
| 阶段七 | 报表统计 | ⚪ 未开始 | 0% | Day 21-22 |
| 阶段八 | 系统优化 | ⚪ 未开始 | 0% | Day 23-25 |

---

## 📖 参考资源

- [RuoYi-Pro-Mini 官方文档](https://doc.iocoder.cn/)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [Spring Boot 3.x 官方文档](https://spring.io/projects/spring-boot)
- [Vue 3 官方文档](https://cn.vuejs.org/)
- [Element Plus 官方文档](https://element-plus.org/)

---

**开发原则**：
1. ✅ 严格遵循 laby-boot 开发规范
2. ✅ 参考 system 模块代码风格
3. ✅ 保持代码简洁、注释清晰
4. ✅ 单元测试覆盖核心业务
5. ✅ 性能优先、安全第一

**开始时间**: 2025-10-28  
**项目状态**: 🟢 开发中

