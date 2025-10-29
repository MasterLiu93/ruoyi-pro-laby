package com.laby.module.wms.enums;

import com.laby.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 拣货波次状态枚举
 * 
 * 对应字典类型：wms_wave_status
 * 
 * @author laby
 */
@Getter
@AllArgsConstructor
public enum PickingWaveStatusEnum implements ArrayValuable<Integer> {

    /**
     * 1-待分配
     * 波次已创建，等待分配拣货员
     */
    PENDING_ASSIGN(1, "待分配"),

    /**
     * 2-已分配
     * 已分配拣货员，等待开始拣货
     */
    ASSIGNED(2, "已分配"),

    /**
     * 3-拣货中
     * 拣货员正在执行拣货操作
     */
    PICKING(3, "拣货中"),

    /**
     * 4-已完成
     * 波次内所有拣货任务已完成
     */
    COMPLETED(4, "已完成"),

    /**
     * 5-已取消
     * 波次已取消
     */
    CANCELLED(5, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(PickingWaveStatusEnum::getStatus).toArray(Integer[]::new);

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
    public static PickingWaveStatusEnum fromStatus(Integer status) {
        return Arrays.stream(values())
                .filter(e -> e.getStatus().equals(status))
                .findFirst()
                .orElse(null);
    }
}

