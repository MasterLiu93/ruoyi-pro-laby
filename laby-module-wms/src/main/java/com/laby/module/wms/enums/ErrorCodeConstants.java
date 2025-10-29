package com.laby.module.wms.enums;

import com.laby.framework.common.exception.ErrorCode;

/**
 * WMS 错误码枚举
 * WMS 错误码区间: [1-020-000-000 ~ 1-020-999-999]
 *
 * @author Laby
 */
public interface ErrorCodeConstants {

    // ========== 仓库管理 1-020-001-000 ==========
    ErrorCode WAREHOUSE_NOT_EXISTS = new ErrorCode(1_020_001_000, "仓库不存在");
    ErrorCode WAREHOUSE_CODE_DUPLICATE = new ErrorCode(1_020_001_001, "仓库编码已存在");
    ErrorCode WAREHOUSE_HAS_AREA = new ErrorCode(1_020_001_002, "仓库下存在库区，无法删除");

    // ========== 库区管理 1-020-002-000 ==========
    ErrorCode WAREHOUSE_AREA_NOT_EXISTS = new ErrorCode(1_020_002_000, "库区不存在");
    ErrorCode WAREHOUSE_AREA_CODE_DUPLICATE = new ErrorCode(1_020_002_001, "库区编码已存在");
    ErrorCode WAREHOUSE_AREA_HAS_LOCATION = new ErrorCode(1_020_002_002, "库区下存在库位，无法删除");

    // ========== 库位管理 1-020-003-000 ==========
    ErrorCode WAREHOUSE_LOCATION_NOT_EXISTS = new ErrorCode(1_020_003_000, "库位不存在");
    ErrorCode WAREHOUSE_LOCATION_CODE_DUPLICATE = new ErrorCode(1_020_003_001, "库位编码已存在");
    ErrorCode WAREHOUSE_LOCATION_HAS_INVENTORY = new ErrorCode(1_020_003_002, "库位下存在库存，无法删除");

    // ========== 商品分类 1-020-004-000 ==========
    ErrorCode GOODS_CATEGORY_NOT_EXISTS = new ErrorCode(1_020_004_000, "商品分类不存在");
    ErrorCode GOODS_CATEGORY_CODE_DUPLICATE = new ErrorCode(1_020_004_001, "分类编码已存在");
    
    // ========== 商品管理 1-020-004-100 ==========
    ErrorCode GOODS_NOT_EXISTS = new ErrorCode(1_020_004_100, "商品不存在");
    ErrorCode GOODS_SKU_CODE_DUPLICATE = new ErrorCode(1_020_004_101, "SKU编码已存在");

    // ========== 库存管理 1-020-005-000 ==========
    ErrorCode INVENTORY_NOT_EXISTS = new ErrorCode(1_020_005_000, "库存不存在");
    ErrorCode INVENTORY_NOT_ENOUGH = new ErrorCode(1_020_005_001, "库存不足");
    ErrorCode INVENTORY_LOCK_FAILED = new ErrorCode(1_020_005_002, "库存锁定失败");
    ErrorCode INVENTORY_HAS_QUANTITY = new ErrorCode(1_020_005_003, "库存数量不为0，无法删除");
    ErrorCode INVENTORY_QUANTITY_INVALID = new ErrorCode(1_020_005_004, "库存数量不能为负数");
    ErrorCode INVENTORY_LOCK_QUANTITY_INVALID = new ErrorCode(1_020_005_005, "锁定数量不能为负数");
    ErrorCode INVENTORY_LOCK_QUANTITY_EXCEED = new ErrorCode(1_020_005_006, "锁定数量不能大于库存数量");

    // ========== 入库管理 1-020-006-000 ==========
    ErrorCode INBOUND_NOT_EXISTS = new ErrorCode(1_020_006_000, "入库单不存在");
    ErrorCode INBOUND_STATUS_ERROR = new ErrorCode(1_020_006_001, "入库单状态不正确，无法执行该操作");
    ErrorCode INBOUND_NO_DUPLICATE = new ErrorCode(1_020_006_002, "入库单号已存在");
    ErrorCode INBOUND_HAS_ITEMS = new ErrorCode(1_020_006_003, "入库单包含明细，无法删除");
    ErrorCode INBOUND_ITEM_NOT_EXISTS = new ErrorCode(1_020_006_004, "入库明细不存在");
    ErrorCode INBOUND_QUANTITY_INVALID = new ErrorCode(1_020_006_005, "入库数量不能为负数或零");

    // ========== 出库管理 1-020-007-000 ==========
    ErrorCode OUTBOUND_NOT_EXISTS = new ErrorCode(1_020_007_000, "出库单不存在");
    ErrorCode OUTBOUND_STATUS_ERROR = new ErrorCode(1_020_007_001, "出库单状态不正确，无法执行该操作");
    ErrorCode OUTBOUND_NO_DUPLICATE = new ErrorCode(1_020_007_002, "出库单号已存在");
    ErrorCode OUTBOUND_HAS_ITEMS = new ErrorCode(1_020_007_003, "出库单包含明细，无法删除");
    ErrorCode OUTBOUND_ITEM_NOT_EXISTS = new ErrorCode(1_020_007_004, "出库明细不存在");
    ErrorCode OUTBOUND_QUANTITY_EXCEED = new ErrorCode(1_020_007_005, "出库数量超过可用库存");
    ErrorCode OUTBOUND_NOT_ALLOW_UPDATE = new ErrorCode(1_020_007_006, "出库单状态不允许修改");
    ErrorCode OUTBOUND_NOT_ALLOW_DELETE = new ErrorCode(1_020_007_007, "出库单状态不允许删除");
    ErrorCode OUTBOUND_NOT_ALLOW_AUDIT = new ErrorCode(1_020_007_008, "出库单状态不允许审核");
    ErrorCode OUTBOUND_NOT_ALLOW_PICK = new ErrorCode(1_020_007_009, "出库单状态不允许拣货");
    ErrorCode OUTBOUND_NOT_ALLOW_SHIP = new ErrorCode(1_020_007_010, "出库单状态不允许发货");
    ErrorCode OUTBOUND_NOT_ALLOW_CANCEL = new ErrorCode(1_020_007_011, "出库单状态不允许取消");
    ErrorCode OUTBOUND_ITEM_EMPTY = new ErrorCode(1_020_007_012, "出库单明细不能为空");

    // ========== 拣货管理 1-020-008-000 ==========
    ErrorCode PICKING_TASK_NOT_EXISTS = new ErrorCode(1_020_008_000, "拣货任务不存在");
    ErrorCode PICKING_TASK_STATUS_ERROR = new ErrorCode(1_020_008_001, "拣货任务状态不正确，无法执行该操作");
    ErrorCode PICKING_TASK_ALREADY_COMPLETED = new ErrorCode(1_020_008_002, "拣货任务已完成");
    ErrorCode PICKING_QUANTITY_EXCEED = new ErrorCode(1_020_008_003, "实际拣货数量超过计划数量");
    ErrorCode PICKING_TASK_EXCEPTION_EXISTS = new ErrorCode(1_020_008_004, "拣货任务已标记为异常");

    // ========== 拣货波次管理 1-020-009-000 ==========
    ErrorCode PICKING_WAVE_NOT_EXISTS = new ErrorCode(1_020_009_000, "拣货波次不存在");
    ErrorCode PICKING_WAVE_STATUS_ERROR = new ErrorCode(1_020_009_001, "拣货波次状态不正确，无法执行该操作");
    ErrorCode PICKING_WAVE_NOT_ALLOW_UPDATE = new ErrorCode(1_020_009_002, "拣货波次状态不允许修改");
    ErrorCode PICKING_WAVE_NOT_ALLOW_DELETE = new ErrorCode(1_020_009_003, "拣货波次状态不允许删除");
    ErrorCode PICKING_WAVE_NOT_ALLOW_ASSIGN = new ErrorCode(1_020_009_004, "拣货波次状态不允许分配");
    ErrorCode PICKING_WAVE_NOT_ALLOW_CANCEL = new ErrorCode(1_020_009_005, "拣货波次状态不允许取消");
    ErrorCode PICKING_WAVE_OUTBOUND_EMPTY = new ErrorCode(1_020_009_006, "拣货波次出库单不能为空");
    ErrorCode PICKING_WAVE_OUTBOUND_DUPLICATE = new ErrorCode(1_020_009_007, "出库单已在其他波次中");
    ErrorCode PICKING_WAVE_WAREHOUSE_MISMATCH = new ErrorCode(1_020_009_008, "出库单必须属于同一仓库");

    // ========== 供应商管理 1-020-010-000 ==========
    ErrorCode SUPPLIER_NOT_EXISTS = new ErrorCode(1_020_010_000, "供应商不存在");
    ErrorCode SUPPLIER_CODE_DUPLICATE = new ErrorCode(1_020_010_001, "供应商编码已存在");
    ErrorCode SUPPLIER_HAS_INBOUND = new ErrorCode(1_020_010_002, "供应商存在关联的入库单，无法删除");

    // ========== 客户管理 1-020-011-000 ==========
    ErrorCode CUSTOMER_NOT_EXISTS = new ErrorCode(1_020_011_000, "客户不存在");
    ErrorCode CUSTOMER_CODE_DUPLICATE = new ErrorCode(1_020_011_001, "客户编码已存在");
    ErrorCode CUSTOMER_HAS_OUTBOUND = new ErrorCode(1_020_011_002, "客户存在关联的出库单，无法删除");

    // ========== 承运商模块 1_020_012_xxx ==========
    ErrorCode CARRIER_NOT_EXISTS = new ErrorCode(1_020_012_000, "承运商不存在");
    ErrorCode CARRIER_CODE_DUPLICATE = new ErrorCode(1_020_012_001, "承运商编码已存在");
    ErrorCode CARRIER_HAS_OUTBOUND = new ErrorCode(1_020_012_002, "承运商存在关联的出库单，无法删除");

    // ========== 移库管理 1_020_013_xxx ==========
    ErrorCode STOCK_MOVE_NOT_EXISTS = new ErrorCode(1_020_013_000, "移库单不存在");
    ErrorCode STOCK_MOVE_NO_DUPLICATE = new ErrorCode(1_020_013_001, "移库单号已存在");
    ErrorCode STOCK_MOVE_NOT_ALLOW_UPDATE = new ErrorCode(1_020_013_002, "移库单状态不允许修改");
    ErrorCode STOCK_MOVE_NOT_ALLOW_DELETE = new ErrorCode(1_020_013_003, "移库单状态不允许删除");
    ErrorCode STOCK_MOVE_NOT_ALLOW_EXECUTE = new ErrorCode(1_020_013_004, "移库单状态不允许执行");
    ErrorCode STOCK_MOVE_NOT_ALLOW_COMPLETE = new ErrorCode(1_020_013_005, "移库单状态不允许完成");
    ErrorCode STOCK_MOVE_NOT_ALLOW_CANCEL = new ErrorCode(1_020_013_006, "移库单状态不允许取消");
    ErrorCode STOCK_MOVE_SAME_LOCATION = new ErrorCode(1_020_013_007, "源库位和目标库位不能相同");
    ErrorCode STOCK_MOVE_INSUFFICIENT_STOCK = new ErrorCode(1_020_013_008, "源库位库存不足");

    // ========== 盘点管理 1_020_014_xxx ==========
    ErrorCode STOCK_TAKING_PLAN_NOT_EXISTS = new ErrorCode(1_020_014_000, "盘点计划不存在");
    ErrorCode STOCK_TAKING_PLAN_NOT_ALLOW_UPDATE = new ErrorCode(1_020_014_001, "盘点计划状态不允许修改");
    ErrorCode STOCK_TAKING_PLAN_NOT_ALLOW_DELETE = new ErrorCode(1_020_014_002, "盘点计划状态不允许删除");
    ErrorCode STOCK_TAKING_PLAN_NOT_ALLOW_AUDIT = new ErrorCode(1_020_014_003, "盘点计划状态不允许审核");
    ErrorCode STOCK_TAKING_PLAN_NOT_ALLOW_CANCEL = new ErrorCode(1_020_014_004, "盘点计划状态不允许取消");
    ErrorCode STOCK_TAKING_NOT_EXISTS = new ErrorCode(1_020_014_100, "盘点单不存在");
    ErrorCode STOCK_TAKING_NOT_ALLOW_UPDATE = new ErrorCode(1_020_014_101, "盘点单状态不允许修改");

}


