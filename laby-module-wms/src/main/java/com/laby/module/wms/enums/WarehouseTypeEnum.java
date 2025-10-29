package com.laby.module.wms.enums;

import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 仓库类型枚举
 *
 * @author Laby
 */
@Getter
@AllArgsConstructor
public enum WarehouseTypeEnum implements ArrayValuable<Integer> {

    FINISHED_GOODS(1, "成品仓"),
    RAW_MATERIALS(2, "原料仓"),
    SEMI_FINISHED(3, "半成品仓");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(WarehouseTypeEnum::getType).toArray(Integer[]::new);

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 名称
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

