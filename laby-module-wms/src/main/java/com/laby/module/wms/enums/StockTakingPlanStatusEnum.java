package com.laby.module.wms.enums;

import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 盘点计划状态枚举
 * 
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum StockTakingPlanStatusEnum implements ArrayValuable<Integer> {

    PENDING_AUDIT(1, "待审核"),
    PENDING(2, "待执行"),
    PROCESSING(3, "执行中"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消");

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
        return new Integer[]{PENDING_AUDIT.getStatus(), PENDING.getStatus(), PROCESSING.getStatus(), 
                            COMPLETED.getStatus(), CANCELLED.getStatus()};
    }
}

