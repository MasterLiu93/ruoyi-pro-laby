package com.laby.module.wms.enums;

import cn.hutool.core.util.ArrayUtil;
import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 入库状态枚举
 *
 * 状态流转：
 * 1. 待审核（PENDING） → 已审核（APPROVED）
 * 2. 已审核（APPROVED） → 收货中（RECEIVING）
 * 3. 收货中（RECEIVING） → 已完成（COMPLETED）
 * 4. 任意状态 → 已取消（CANCELLED）
 *
 * 业务说明：
 * - PENDING：入库单已创建，等待审核
 * - APPROVED：审核通过，可以开始收货
 * - RECEIVING：正在收货上架
 * - COMPLETED：所有商品已收货完成
 * - CANCELLED：入库单已取消（可能因为订单取消、货物问题等）
 *
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum InboundStatusEnum implements ArrayValuable<Integer> {

    /**
     * 待审核
     * 说明：入库单已创建，等待仓库主管审核
     * 可执行操作：审核、编辑、取消
     */
    PENDING(1, "待审核"),

    /**
     * 已审核
     * 说明：审核通过，等待收货
     * 可执行操作：开始收货、取消
     */
    APPROVED(2, "已审核"),

    /**
     * 收货中
     * 说明：正在进行收货上架操作
     * 可执行操作：继续收货、完成收货
     */
    RECEIVING(3, "收货中"),

    /**
     * 已完成
     * 说明：所有商品已收货完成，库存已更新
     * 可执行操作：查看详情
     */
    COMPLETED(4, "已完成"),

    /**
     * 已取消
     * 说明：入库单已取消
     * 可执行操作：查看详情
     */
    CANCELLED(5, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(InboundStatusEnum::getStatus).toArray(Integer[]::new);

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
    public static InboundStatusEnum getByStatus(Integer status) {
        return ArrayUtil.firstMatch(o -> o.getStatus().equals(status), values());
    }
}

