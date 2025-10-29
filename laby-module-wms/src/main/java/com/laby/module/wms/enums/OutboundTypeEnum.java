package com.laby.module.wms.enums;

import cn.hutool.core.util.ArrayUtil;
import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 出库类型枚举
 *
 * 业务说明：
 * - SALE：销售出库，发货给客户的商品
 * - TRANSFER：调拨出库，调拨到其他仓库的商品
 * - RETURN：退货出库，退货给供应商的商品
 * - OTHER：其他出库，如盘亏、借出等
 *
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum OutboundTypeEnum implements ArrayValuable<Integer> {

    /**
     * 销售出库
     * 说明：发货给客户的商品
     * 场景：正常销售、线上订单发货等
     */
    SALE(1, "销售出库"),

    /**
     * 调拨出库
     * 说明：调拨到其他仓库的商品
     * 场景：仓库间调拨、库存调配等
     */
    TRANSFER(2, "调拨出库"),

    /**
     * 退货出库
     * 说明：退货给供应商的商品
     * 场景：质量问题退货、过期商品退货等
     */
    RETURN(3, "退货出库"),

    /**
     * 其他出库
     * 说明：其他类型的出库
     * 场景：盘亏、借出、报废等
     */
    OTHER(4, "其他出库");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(OutboundTypeEnum::getType).toArray(Integer[]::new);

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
    public static OutboundTypeEnum getByType(Integer type) {
        return ArrayUtil.firstMatch(o -> o.getType().equals(type), values());
    }
}

