package com.laby.module.wms.service.picking;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.picking.vo.PickingWavePageReqVO;
import com.laby.module.wms.controller.admin.picking.vo.PickingWaveRespVO;
import com.laby.module.wms.controller.admin.picking.vo.PickingWaveSaveReqVO;
import com.laby.module.wms.dal.dataobject.picking.PickingWaveDO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 拣货波次 Service 接口
 * 
 * 提供拣货波次的生成、分配、执行等功能
 *
 * @author laby
 */
public interface PickingWaveService {

    /**
     * 创建拣货波次
     * 
     * 业务说明：
     * - 根据选择的出库单生成拣货波次
     * - 自动生成波次号（WAVE-yyyyMMdd-流水号）
     * - 计算订单数量、商品种类、总数量
     * - 根据波次内的出库单生成拣货任务
     * - 初始状态为"待分配"
     *
     * @param createReqVO 创建信息
     * @return 波次ID
     */
    Long createPickingWave(@Valid PickingWaveSaveReqVO createReqVO);

    /**
     * 更新拣货波次
     * 
     * 业务说明：
     * - 只有"待分配"状态的波次才能修改
     * - 可以修改出库单列表、优先级、备注
     *
     * @param updateReqVO 更新信息
     */
    void updatePickingWave(@Valid PickingWaveSaveReqVO updateReqVO);

    /**
     * 删除拣货波次
     * 
     * 业务说明：
     * - 只有"待分配"状态的波次才能删除
     * - 删除波次时同时删除关联的拣货任务
     *
     * @param id 波次ID
     */
    void deletePickingWave(Long id);

    /**
     * 获取拣货波次详情
     *
     * @param id 波次ID
     * @return 波次详情（含关联的出库单列表）
     */
    PickingWaveRespVO getPickingWave(Long id);

    /**
     * 获取拣货波次分页列表
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    PageResult<PickingWaveRespVO> getPickingWavePage(PickingWavePageReqVO pageReqVO);

    /**
     * 分配拣货员
     * 
     * 业务说明：
     * - 将波次分配给指定拣货员
     * - 更新波次状态为"已分配"
     * - 同时分配波次内的所有拣货任务
     *
     * @param id 波次ID
     * @param pickerId 拣货员ID
     * @param pickerName 拣货员姓名
     */
    void assignPicker(Long id, Long pickerId, String pickerName);

    /**
     * 开始拣货
     * 
     * 业务说明：
     * - 更新波次状态为"拣货中"
     * - 记录开始时间
     *
     * @param id 波次ID
     */
    void startPicking(Long id);

    /**
     * 完成拣货
     * 
     * 业务说明：
     * - 检查波次内所有拣货任务是否完成
     * - 更新波次状态为"已完成"
     * - 记录结束时间和实际耗时
     *
     * @param id 波次ID
     */
    void completePickingWave(Long id);

    /**
     * 取消拣货波次
     * 
     * 业务说明：
     * - 取消波次，同时取消所有拣货任务
     * - 只有"待分配"、"已分配"、"拣货中"状态可以取消
     *
     * @param id 波次ID
     */
    void cancelPickingWave(Long id);

    /**
     * 校验拣货波次是否存在
     *
     * @param id 波次ID
     * @return 波次DO
     */
    PickingWaveDO validatePickingWaveExists(Long id);

    /**
     * 根据出库单ID列表自动生成拣货波次
     * 
     * 业务说明：
     * - 选择多个待拣货的出库单
     * - 自动按照仓库、优先级等维度分组
     * - 生成多个拣货波次
     *
     * @param warehouseId 仓库ID
     * @param waveType 波次类型
     * @return 生成的波次ID列表
     */
    List<Long> generatePickingWaves(Long warehouseId, String waveType);
}

