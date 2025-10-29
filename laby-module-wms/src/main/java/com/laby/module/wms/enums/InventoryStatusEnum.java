package com.laby.module.wms.enums;

import cn.hutool.core.util.ArrayUtil;
import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 库存状态枚举
 * 
 * 状态说明：
 * - NORMAL(1)：正常库存，可正常使用
 * - FROZEN(2)：冻结库存，不可使用（如质量问题待处理）
 * - PENDING(3)：待检库存，新入库等待质检
 * - DAMAGED(4)：损坏库存，已损坏待报废处理
 * 
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum InventoryStatusEnum implements ArrayValuable<Integer> {

    /**
     * 正常
     * 说明：可正常销售和出库的库存
     */
    NORMAL(1, "正常"),
    
    /**
     * 冻结
     * 说明：被冻结不可使用的库存
     * 场景：质量问题、待处理纠纷等
     */
    FROZEN(2, "冻结"),
    
    /**
     * 待检
     * 说明：新入库等待质检的库存
     * 场景：新收货、退货入库等需要质检的场景
     */
    PENDING(3, "待检"),
    
    /**
     * 损坏
     * 说明：已损坏待报废处理的库存
     * 场景：破损、过期等不可用库存
     */
    DAMAGED(4, "损坏");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(InventoryStatusEnum::getStatus).toArray(Integer[]::new);

    /**
     * 状态值
     */
    private final Integer status;
    
    /**
     * 状态名称
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    /**
     * 根据状态值获取枚举
     * 
     * @param status 状态值
     * @return 枚举对象，找不到返回 null
     */
    public static InventoryStatusEnum valueOf(Integer status) {
        return ArrayUtil.firstMatch(o -> o.getStatus().equals(status), values());
    }

}

