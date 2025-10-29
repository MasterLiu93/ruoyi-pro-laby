package com.laby.module.wms.enums;

import cn.hutool.core.util.ArrayUtil;
import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 出库状态枚举
 *
 * 状态流转：
 * 1. 待审核（PENDING） → 已审核（APPROVED）
 * 2. 已审核（APPROVED） → 拣货中（PICKING）
 * 3. 拣货中（PICKING） → 待发货（TO_SHIP）
 * 4. 待发货（TO_SHIP） → 已发货（SHIPPED）
 * 5. 任意状态 → 已取消（CANCELLED）
 *
 * 业务说明：
 * - PENDING：出库单已创建，等待审核
 * - APPROVED：审核通过，可以开始拣货
 * - PICKING：正在进行拣货作业
 * - TO_SHIP：拣货完成，等待复核发货
 * - SHIPPED：已发货完成，库存已扣减
 * - CANCELLED：出库单已取消
 *
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum OutboundStatusEnum implements ArrayValuable<Integer> {

    /**
     * 待审核
     * 说明：出库单已创建，等待仓库主管审核
     * 可执行操作：审核、编辑、取消
     */
    PENDING(1, "待审核"),

    /**
     * 已审核
     * 说明：审核通过，等待拣货
     * 可执行操作：开始拣货、取消
     */
    APPROVED(2, "已审核"),

    /**
     * 拣货中
     * 说明：正在进行拣货作业
     * 可执行操作：继续拣货、完成拣货
     */
    PICKING(3, "拣货中"),

    /**
     * 待发货
     * 说明：拣货完成，等待复核发货
     * 可执行操作：复核发货、取消
     */
    TO_SHIP(4, "待发货"),

    /**
     * 已发货
     * 说明：已发货完成，库存已扣减
     * 可执行操作：查看详情
     */
    SHIPPED(5, "已发货"),

    /**
     * 已取消
     * 说明：出库单已取消
     * 可执行操作：查看详情
     */
    CANCELLED(6, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(OutboundStatusEnum::getStatus).toArray(Integer[]::new);

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 状态名称
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    /**
     * 根据状态值获取枚举
     *
     * @param status 状态值
     * @return 对应的枚举，如果不存在则返回null
     */
    public static OutboundStatusEnum getByStatus(Integer status) {
        return ArrayUtil.firstMatch(o -> o.getStatus().equals(status), values());
    }
}

