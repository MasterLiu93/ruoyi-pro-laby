package com.laby.module.wms.enums;

import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 盘点状态枚举
 * 
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum StockTakingStatusEnum implements ArrayValuable<Integer> {

    PENDING(1, "待盘点"),
    COUNTED(2, "已盘点"),
    REVIEWED(3, "已复核"),
    ADJUSTED(4, "已调整");

    /**
     * 状态
     */
    private final Integer status;
    
    /**
     * 状态名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return new Integer[]{PENDING.getStatus(), COUNTED.getStatus(), REVIEWED.getStatus(), ADJUSTED.getStatus()};
    }
}

