package com.laby.module.wms.enums;

import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 拣货任务状态枚举
 * 
 * 对应字典类型：wms_picking_task_status
 * 
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum PickingTaskStatusEnum implements ArrayValuable<Integer> {

    /**
     * 1-待拣货
     * 任务已创建，等待拣货员执行
     */
    PENDING(1, "待拣货"),

    /**
     * 2-拣货中
     * 拣货员正在执行拣货操作
     */
    PICKING(2, "拣货中"),

    /**
     * 3-已完成
     * 拣货任务已完成，实际数量已记录
     */
    COMPLETED(3, "已完成"),

    /**
     * 4-异常
     * 拣货过程中发生异常（库位空、库存不足、商品损坏等）
     */
    EXCEPTION(4, "异常");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(PickingTaskStatusEnum::getStatus).toArray(Integer[]::new);

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
     * @return 枚举对象
     */
    public static PickingTaskStatusEnum fromStatus(Integer status) {
        return Arrays.stream(values())
                .filter(e -> e.getStatus().equals(status))
                .findFirst()
                .orElse(null);
    }
}

