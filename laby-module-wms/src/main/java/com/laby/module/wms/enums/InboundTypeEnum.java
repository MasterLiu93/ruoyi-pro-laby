package com.laby.module.wms.enums;

import cn.hutool.core.util.ArrayUtil;
import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 入库类型枚举
 *
 * 业务说明：
 * - PURCHASE：采购入库，从供应商采购的商品入库
 * - RETURN：退货入库，客户退货的商品入库
 * - TRANSFER：调拨入库，从其他仓库调拨过来的商品
 * - OTHER：其他入库，如盘盈、生产入库等
 *
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum InboundTypeEnum implements ArrayValuable<Integer> {

    /**
     * 采购入库
     * 说明：从供应商采购的商品入库
     * 场景：正常采购流程、补货等
     */
    PURCHASE(1, "采购入库"),

    /**
     * 退货入库
     * 说明：客户退货的商品入库
     * 场景：客户退换货、质量问题退货等
     */
    RETURN(2, "退货入库"),

    /**
     * 调拨入库
     * 说明：从其他仓库调拨过来的商品
     * 场景：仓库间调拨、库存调配等
     */
    TRANSFER(3, "调拨入库"),

    /**
     * 其他入库
     * 说明：其他类型的入库
     * 场景：盘盈、生产入库、借入等
     */
    OTHER(4, "其他入库");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(InboundTypeEnum::getType).toArray(Integer[]::new);

    /**
     * 类型值
     */
    private final Integer type;

    /**
     * 类型名称
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    /**
     * 根据类型值获取枚举
     *
     * @param type 类型值
     * @return 对应的枚举，如果不存在则返回null
     */
    public static InboundTypeEnum getByType(Integer type) {
        return ArrayUtil.firstMatch(o -> o.getType().equals(type), values());
    }
}

