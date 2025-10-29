package com.laby.module.wms.enums;

import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 盘点类型枚举
 * 
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum StockTakingTypeEnum implements ArrayValuable<Integer> {

    FULL(1, "全盘"),
    CYCLE(2, "循环盘"),
    SPOT(3, "抽盘"),
    DYNAMIC(4, "动态盘");

    /**
     * 类型
     */
    private final Integer type;
    
    /**
     * 类型名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return new Integer[]{FULL.getType(), CYCLE.getType(), SPOT.getType(), DYNAMIC.getType()};
    }
}

