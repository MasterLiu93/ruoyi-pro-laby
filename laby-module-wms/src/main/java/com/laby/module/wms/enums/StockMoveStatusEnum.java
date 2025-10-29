package com.laby.module.wms.enums;

import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 移库状态枚举
 * 
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum StockMoveStatusEnum implements ArrayValuable<Integer> {

    PENDING(1, "待执行"),
    PROCESSING(2, "执行中"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

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
        return new Integer[]{PENDING.getStatus(), PROCESSING.getStatus(), COMPLETED.getStatus(), CANCELLED.getStatus()};
    }
}

