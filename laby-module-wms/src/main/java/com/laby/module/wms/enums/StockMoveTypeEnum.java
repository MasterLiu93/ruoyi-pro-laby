package com.laby.module.wms.enums;

import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 移库类型枚举
 * 
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum StockMoveTypeEnum implements ArrayValuable<Integer> {

    LOCATION(1, "库位调整"),
    AREA(2, "库区调整"),
    WAREHOUSE(3, "仓库调拨");

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
        return new Integer[]{LOCATION.getType(), AREA.getType(), WAREHOUSE.getType()};
    }
}

