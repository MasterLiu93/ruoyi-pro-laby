package com.laby.module.wms.enums;

import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 盘点范围类型枚举
 * 
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum StockTakingScopeTypeEnum implements ArrayValuable<Integer> {

    WAREHOUSE(1, "全仓"),
    AREA(2, "库区"),
    LOCATION(3, "库位"),
    GOODS(4, "商品");

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
        return new Integer[]{WAREHOUSE.getType(), AREA.getType(), LOCATION.getType(), GOODS.getType()};
    }
}

