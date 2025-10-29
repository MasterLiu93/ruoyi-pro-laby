package com.laby.module.wms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 拣货异常类型枚举
 * 
 * 对应字典类型：wms_picking_exception_type
 * 
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum PickingExceptionTypeEnum {

    /**
     * EMPTY-库位空
     * 目标库位无库存
     */
    EMPTY("EMPTY", "库位空"),

    /**
     * SHORT-库存不足
     * 库位库存数量小于计划拣货数量
     */
    SHORT("SHORT", "库存不足"),

    /**
     * DAMAGED-商品损坏
     * 库位商品已损坏，无法拣货
     */
    DAMAGED("DAMAGED", "商品损坏"),

    /**
     * EXPIRED-商品过期
     * 库位商品已过期，无法拣货
     */
    EXPIRED("EXPIRED", "商品过期"),

    /**
     * WRONG-拣错商品
     * 拣货员拣选了错误的商品
     */
    WRONG("WRONG", "拣错商品");

    /**
     * 异常类型代码
     */
    private final String code;

    /**
     * 异常类型名称
     */
    private final String name;

    /**
     * 根据代码获取枚举
     *
     * @param code 异常类型代码
     * @return 枚举对象
     */
    public static PickingExceptionTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}

