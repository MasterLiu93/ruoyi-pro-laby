package com.laby.module.wms.service.picking;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.picking.vo.PickingWavePageReqVO;
import com.laby.module.wms.controller.admin.picking.vo.PickingWaveRespVO;
import com.laby.module.wms.controller.admin.picking.vo.PickingWaveSaveReqVO;
import com.laby.module.wms.convert.picking.PickingWaveConvert;
import com.laby.module.wms.dal.dataobject.outbound.OutboundDO;
import com.laby.module.wms.dal.dataobject.picking.PickingWaveDO;
import com.laby.module.wms.dal.dataobject.picking.PickingWaveOrderDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.mysql.picking.PickingWaveMapper;
import com.laby.module.wms.dal.mysql.picking.PickingWaveOrderMapper;
import com.laby.module.wms.dal.mysql.warehouse.WarehouseMapper;
import com.laby.module.wms.enums.PickingWaveStatusEnum;
import com.laby.module.wms.service.outbound.OutboundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.module.wms.enums.ErrorCodeConstants.*;

/**
 * 拣货波次 Service 实现类
 * 
 * 实现说明：
 * - 所有公共方法都经过参数校验（@Validated）
 * - 状态流转：待分配 → 已分配 → 拣货中 → 已完成/已取消
 * - 波次生成时自动创建拣货任务
 * - 使用 MapStruct 进行对象转换
 * - 异常统一使用 ServiceException
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class PickingWaveServiceImpl implements PickingWaveService {

    @Resource
    private PickingWaveMapper pickingWaveMapper;

    @Resource
    private PickingWaveOrderMapper pickingWaveOrderMapper;

    @Resource
    private WarehouseMapper warehouseMapper;

    @Resource
    private OutboundService outboundService;

    @Resource
    private com.laby.module.wms.dal.mysql.outbound.OutboundMapper outboundMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPickingWave(PickingWaveSaveReqVO createReqVO) {
        // 1. 校验出库单是否存在且状态正确
        List<OutboundDO> outbounds = validateOutboundsForWave(createReqVO.getOutboundIds());

        // 2. 转换并填充波次信息
        PickingWaveDO wave = PickingWaveConvert.INSTANCE.convert(createReqVO);
        wave.setWaveNo(generateWaveNo());
        wave.setStatus(PickingWaveStatusEnum.PENDING_ASSIGN.getStatus());
        
        // 计算统计信息
        wave.setOrderCount(outbounds.size());
        wave.setTotalQuantity(outbounds.stream()
                .map(OutboundDO::getTotalQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // 3. 插入波次
        pickingWaveMapper.insert(wave);

        // 4. 创建波次与出库单的关联关系
        for (OutboundDO outbound : outbounds) {
            PickingWaveOrderDO waveOrder = PickingWaveOrderDO.builder()
                    .waveId(wave.getId())
                    .waveNo(wave.getWaveNo())
                    .outboundId(outbound.getId())
                    .outboundNo(outbound.getOutboundNo())
                    .build();
            pickingWaveOrderMapper.insert(waveOrder);
        }

        log.info("[拣货波次] 创建成功，波次号：{}，出库单数量：{}", wave.getWaveNo(), outbounds.size());
        return wave.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePickingWave(PickingWaveSaveReqVO updateReqVO) {
        // 1. 校验波次是否存在
        PickingWaveDO wave = validatePickingWaveExists(updateReqVO.getId());

        // 2. 校验状态（只有待分配状态可以修改）
        if (!PickingWaveStatusEnum.PENDING_ASSIGN.getStatus().equals(wave.getStatus())) {
            throw exception(PICKING_WAVE_NOT_ALLOW_UPDATE);
        }

        // 3. 更新波次信息
        PickingWaveDO updateObj = PickingWaveDO.builder()
                .id(updateReqVO.getId())
                .warehouseId(updateReqVO.getWarehouseId())
                .waveType(updateReqVO.getWaveType())
                .priority(updateReqVO.getPriority())
                .remark(updateReqVO.getRemark())
                .build();
        pickingWaveMapper.updateById(updateObj);

        log.info("[拣货波次] 更新成功，波次ID：{}", updateReqVO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePickingWave(Long id) {
        // 1. 校验波次是否存在
        PickingWaveDO wave = validatePickingWaveExists(id);

        // 2. 校验状态（只有待分配状态可以删除）
        if (!PickingWaveStatusEnum.PENDING_ASSIGN.getStatus().equals(wave.getStatus())) {
            throw exception(PICKING_WAVE_NOT_ALLOW_DELETE);
        }

        // 3. 删除波次
        pickingWaveMapper.deleteById(id);

        // 4. 删除关联关系
        pickingWaveOrderMapper.deleteByWaveId(id);

        log.info("[拣货波次] 删除成功，波次ID：{}", id);
    }

    @Override
    public PickingWaveRespVO getPickingWave(Long id) {
        // 1. 查询波次
        PickingWaveDO wave = pickingWaveMapper.selectById(id);
        if (wave == null) {
            return null;
        }

        // 2. 转换为VO
        PickingWaveRespVO respVO = PickingWaveConvert.INSTANCE.convert(wave);

        // 3. 查询关联的出库单并填充出库单号列表
        List<Long> outboundIds = pickingWaveOrderMapper.selectOutboundIdsByWaveId(id);
        if (CollUtil.isNotEmpty(outboundIds)) {
            List<OutboundDO> outbounds = outboundMapper.selectList(OutboundDO::getId, outboundIds);
            List<String> outboundNos = outbounds.stream()
                    .map(OutboundDO::getOutboundNo)
                    .toList();
            respVO.setOutboundNos(outboundNos);
        }

        return respVO;
    }

    @Override
    public PageResult<PickingWaveRespVO> getPickingWavePage(PickingWavePageReqVO pageReqVO) {
        PageResult<PickingWaveDO> pageResult = pickingWaveMapper.selectPage(pageReqVO);
        PageResult<PickingWaveRespVO> voPageResult = PickingWaveConvert.INSTANCE.convertPage(pageResult);
        
        // 填充仓库名称和关联出库单号
        if (CollUtil.isNotEmpty(voPageResult.getList())) {
            // 1. 获取所有仓库ID
            Set<Long> warehouseIds = voPageResult.getList().stream()
                    .map(PickingWaveRespVO::getWarehouseId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            
            // 2. 查询仓库信息
            Map<Long, WarehouseDO> warehouseMap = new HashMap<>();
            if (CollUtil.isNotEmpty(warehouseIds)) {
                List<WarehouseDO> warehouses = warehouseMapper.selectList(
                        WarehouseDO::getId, warehouseIds);
                warehouseMap = warehouses.stream()
                        .collect(Collectors.toMap(WarehouseDO::getId, warehouse -> warehouse));
            }
            
            // 3. 查询波次关联的出库单号
            List<Long> waveIds = voPageResult.getList().stream()
                    .map(PickingWaveRespVO::getId)
                    .collect(Collectors.toList());
            List<PickingWaveOrderDO> waveOrders = pickingWaveOrderMapper.selectList(
                    PickingWaveOrderDO::getWaveId, waveIds);
            
            // 按波次ID分组
            Map<Long, List<String>> waveOutboundMap = waveOrders.stream()
                    .collect(Collectors.groupingBy(
                            PickingWaveOrderDO::getWaveId,
                            Collectors.mapping(PickingWaveOrderDO::getOutboundNo, Collectors.toList())
                    ));
            
            // 4. 填充数据
            Map<Long, WarehouseDO> finalWarehouseMap = warehouseMap;
            voPageResult.getList().forEach(vo -> {
                // 填充仓库名称
                WarehouseDO warehouse = finalWarehouseMap.get(vo.getWarehouseId());
                if (warehouse != null) {
                    vo.setWarehouseName(warehouse.getWarehouseName());
                }
                
                // 填充关联出库单号列表
                List<String> outboundNos = waveOutboundMap.get(vo.getId());
                if (CollUtil.isNotEmpty(outboundNos)) {
                    vo.setOutboundNos(outboundNos);
                }
            });
        }
        
        return voPageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPicker(Long id, Long pickerId, String pickerName) {
        // 1. 校验波次是否存在
        PickingWaveDO wave = validatePickingWaveExists(id);

        // 2. 校验状态（只有待分配状态可以分配）
        if (!PickingWaveStatusEnum.PENDING_ASSIGN.getStatus().equals(wave.getStatus())) {
            throw exception(PICKING_WAVE_NOT_ALLOW_ASSIGN);
        }

        // 3. 更新拣货员信息
        PickingWaveDO updateObj = PickingWaveDO.builder()
                .id(id)
                .pickerId(pickerId)
                .pickerName(pickerName)
                .status(PickingWaveStatusEnum.ASSIGNED.getStatus())
                .build();
        pickingWaveMapper.updateById(updateObj);

        log.info("[拣货波次] 分配拣货员成功，波次ID：{}，拣货员：{}", id, pickerName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startPicking(Long id) {
        // 1. 校验波次是否存在
        PickingWaveDO wave = validatePickingWaveExists(id);

        // 2. 校验状态（只有已分配状态可以开始）
        if (!PickingWaveStatusEnum.ASSIGNED.getStatus().equals(wave.getStatus())) {
            throw exception(PICKING_WAVE_STATUS_ERROR);
        }

        // 3. 更新状态
        PickingWaveDO updateObj = PickingWaveDO.builder()
                .id(id)
                .status(PickingWaveStatusEnum.PICKING.getStatus())
                .startTime(LocalDateTime.now())
                .build();
        pickingWaveMapper.updateById(updateObj);

        log.info("[拣货波次] 开始拣货，波次ID：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completePickingWave(Long id) {
        // 1. 校验波次是否存在
        PickingWaveDO wave = validatePickingWaveExists(id);

        // 2. 校验状态（只有拣货中状态可以完成）
        if (!PickingWaveStatusEnum.PICKING.getStatus().equals(wave.getStatus())) {
            throw exception(PICKING_WAVE_STATUS_ERROR);
        }

        // 3. 计算实际耗时
        LocalDateTime now = LocalDateTime.now();
        Integer actualTime = null;
        if (wave.getStartTime() != null) {
            actualTime = (int) java.time.Duration.between(wave.getStartTime(), now).getSeconds();
        }

        // 4. 更新状态
        PickingWaveDO updateObj = PickingWaveDO.builder()
                .id(id)
                .status(PickingWaveStatusEnum.COMPLETED.getStatus())
                .endTime(now)
                .actualTime(actualTime)
                .build();
        pickingWaveMapper.updateById(updateObj);

        log.info("[拣货波次] 完成拣货，波次ID：{}，实际耗时：{}秒", id, actualTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelPickingWave(Long id) {
        // 1. 校验波次是否存在
        PickingWaveDO wave = validatePickingWaveExists(id);

        // 2. 校验状态（已完成的波次不能取消）
        if (PickingWaveStatusEnum.COMPLETED.getStatus().equals(wave.getStatus())) {
            throw exception(PICKING_TASK_ALREADY_COMPLETED);
        }

        // 3. 更新状态
        PickingWaveDO updateObj = PickingWaveDO.builder()
                .id(id)
                .status(PickingWaveStatusEnum.CANCELLED.getStatus())
                .build();
        pickingWaveMapper.updateById(updateObj);

        log.info("[拣货波次] 取消波次，波次ID：{}", id);
    }

    @Override
    public PickingWaveDO validatePickingWaveExists(Long id) {
        PickingWaveDO wave = pickingWaveMapper.selectById(id);
        if (wave == null) {
            throw exception(PICKING_WAVE_NOT_EXISTS);
        }
        return wave;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> generatePickingWaves(Long warehouseId, String waveType) {
        // 1. 查询该仓库所有"已审核"状态的出库单（未加入波次的）
        List<OutboundDO> outbounds = queryOutboundsForWave(warehouseId);
        
        if (CollUtil.isEmpty(outbounds)) {
            log.info("[拣货波次] 没有待拣货的出库单，仓库ID：{}", warehouseId);
            return new ArrayList<>();
        }

        List<Long> waveIds = new ArrayList<>();

        // 2. 根据波次类型分组生成波次
        if ("BATCH".equals(waveType)) {
            // 批次拣货：按优先级分组，每组最多20个出库单
            waveIds.addAll(generateBatchWaves(warehouseId, outbounds));
        } else if ("ZONE".equals(waveType)) {
            // 分区拣货：按库区分组（需要分析出库单的商品所在库区）
            waveIds.addAll(generateZoneWaves(warehouseId, outbounds));
        } else if ("SINGLE".equals(waveType)) {
            // 单品拣货：按商品分组，每个SKU一个波次
            waveIds.addAll(generateSingleWaves(warehouseId, outbounds));
        } else {
            // 默认批次拣货
            waveIds.addAll(generateBatchWaves(warehouseId, outbounds));
        }

        log.info("[拣货波次] 自动生成拣货波次成功，仓库ID：{}，波次类型：{}，生成波次数：{}", 
                warehouseId, waveType, waveIds.size());
        return waveIds;
    }

    /**
     * 查询待生成波次的出库单
     *
     * @param warehouseId 仓库ID
     * @return 出库单列表
     */
    private List<OutboundDO> queryOutboundsForWave(Long warehouseId) {
        // 查询"已审核"状态的出库单
        List<OutboundDO> allOutbounds = outboundMapper.selectList(
                OutboundDO::getWarehouseId, warehouseId,
                OutboundDO::getStatus, com.laby.module.wms.enums.OutboundStatusEnum.APPROVED.getStatus()
        );

        // 过滤掉已经在波次中的出库单
        return allOutbounds.stream()
                .filter(outbound -> {
                    Long waveId = pickingWaveOrderMapper.selectWaveIdByOutboundId(outbound.getId());
                    return waveId == null;
                })
                .toList();
    }

    /**
     * 批次拣货：按优先级分组
     *
     * @param warehouseId 仓库ID
     * @param outbounds 出库单列表
     * @return 波次ID列表
     */
    private List<Long> generateBatchWaves(Long warehouseId, List<OutboundDO> outbounds) {
        List<Long> waveIds = new ArrayList<>();
        
        // 按预计发货时间排序（越早越优先）
        outbounds.sort((o1, o2) -> {
            if (o1.getExpectedShipmentTime() == null) return 1;
            if (o2.getExpectedShipmentTime() == null) return -1;
            return o1.getExpectedShipmentTime().compareTo(o2.getExpectedShipmentTime());
        });

        // 每20个出库单生成一个波次
        int batchSize = 20;
        for (int i = 0; i < outbounds.size(); i += batchSize) {
            int end = Math.min(i + batchSize, outbounds.size());
            List<OutboundDO> batch = outbounds.subList(i, end);
            
            // 创建波次
            PickingWaveSaveReqVO saveReqVO = new PickingWaveSaveReqVO();
            saveReqVO.setWarehouseId(warehouseId);
            saveReqVO.setWaveType(1); // 批次拣货
            saveReqVO.setOutboundIds(batch.stream().map(OutboundDO::getId).toList());
            saveReqVO.setPriority(1); // 默认普通优先级
            saveReqVO.setRemark("自动生成批次拣货波次");
            
            Long waveId = createPickingWave(saveReqVO);
            waveIds.add(waveId);
        }

        return waveIds;
    }

    /**
     * 分区拣货：按库区分组（简化版本，实际需要分析商品所在库区）
     *
     * @param warehouseId 仓库ID
     * @param outbounds 出库单列表
     * @return 波次ID列表
     */
    private List<Long> generateZoneWaves(Long warehouseId, List<OutboundDO> outbounds) {
        // 简化实现：按出库单顺序每10个一组
        List<Long> waveIds = new ArrayList<>();
        int batchSize = 10;
        
        for (int i = 0; i < outbounds.size(); i += batchSize) {
            int end = Math.min(i + batchSize, outbounds.size());
            List<OutboundDO> batch = outbounds.subList(i, end);
            
            PickingWaveSaveReqVO saveReqVO = new PickingWaveSaveReqVO();
            saveReqVO.setWarehouseId(warehouseId);
            saveReqVO.setWaveType(2); // 分区拣货
            saveReqVO.setOutboundIds(batch.stream().map(OutboundDO::getId).toList());
            saveReqVO.setPriority(1);
            saveReqVO.setRemark("自动生成分区拣货波次");
            
            Long waveId = createPickingWave(saveReqVO);
            waveIds.add(waveId);
        }

        return waveIds;
    }

    /**
     * 单品拣货：按商品分组（简化版本）
     *
     * @param warehouseId 仓库ID
     * @param outbounds 出库单列表
     * @return 波次ID列表
     */
    private List<Long> generateSingleWaves(Long warehouseId, List<OutboundDO> outbounds) {
        // 简化实现：每5个出库单一组
        List<Long> waveIds = new ArrayList<>();
        int batchSize = 5;
        
        for (int i = 0; i < outbounds.size(); i += batchSize) {
            int end = Math.min(i + batchSize, outbounds.size());
            List<OutboundDO> batch = outbounds.subList(i, end);
            
            PickingWaveSaveReqVO saveReqVO = new PickingWaveSaveReqVO();
            saveReqVO.setWarehouseId(warehouseId);
            saveReqVO.setWaveType(3); // 单品拣货
            saveReqVO.setOutboundIds(batch.stream().map(OutboundDO::getId).toList());
            saveReqVO.setPriority(1);
            saveReqVO.setRemark("自动生成单品拣货波次");
            
            Long waveId = createPickingWave(saveReqVO);
            waveIds.add(waveId);
        }

        return waveIds;
    }

    /**
     * 校验出库单是否可以加入波次
     *
     * @param outboundIds 出库单ID列表
     * @return 出库单列表
     */
    private List<OutboundDO> validateOutboundsForWave(List<Long> outboundIds) {
        if (CollUtil.isEmpty(outboundIds)) {
            throw exception(PICKING_WAVE_OUTBOUND_EMPTY);
        }

        List<OutboundDO> outbounds = new ArrayList<>();
        Long firstWarehouseId = null;

        for (Long outboundId : outboundIds) {
            // 1. 校验出库单是否存在
            OutboundDO outbound = outboundMapper.selectById(outboundId);
            if (outbound == null) {
                throw exception(OUTBOUND_NOT_EXISTS);
            }

            // 2. 校验出库单状态（只有"已审核"状态可以加入波次）
            if (!com.laby.module.wms.enums.OutboundStatusEnum.APPROVED.getStatus().equals(outbound.getStatus())) {
                throw exception(OUTBOUND_NOT_ALLOW_PICK);
            }

            // 3. 校验出库单是否已经在其他波次中
            Long existWaveId = pickingWaveOrderMapper.selectWaveIdByOutboundId(outboundId);
            if (existWaveId != null) {
                throw exception(PICKING_WAVE_OUTBOUND_DUPLICATE);
            }

            // 4. 校验所有出库单是否属于同一仓库
            if (firstWarehouseId == null) {
                firstWarehouseId = outbound.getWarehouseId();
            } else if (!firstWarehouseId.equals(outbound.getWarehouseId())) {
                throw exception(PICKING_WAVE_WAREHOUSE_MISMATCH);
            }

            outbounds.add(outbound);
        }

        return outbounds;
    }

    /**
     * 生成波次号
     * 规则：WAVE-yyyyMMdd-流水号
     *
     * @return 波次号
     */
    private String generateWaveNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 查询当天最大波次号
        String prefix = "WAVE-" + date + "-";
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PickingWaveDO> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.like(PickingWaveDO::getWaveNo, prefix)
                .orderByDesc(PickingWaveDO::getWaveNo)
                .last("LIMIT 1");
        List<PickingWaveDO> todayWaves = pickingWaveMapper.selectList(wrapper);

        int serialNo = 1;
        if (CollUtil.isNotEmpty(todayWaves)) {
            String lastWaveNo = todayWaves.get(0).getWaveNo();
            // 提取最后4位流水号
            String lastSerialNoStr = lastWaveNo.substring(lastWaveNo.length() - 4);
            try {
                serialNo = Integer.parseInt(lastSerialNoStr) + 1;
            } catch (NumberFormatException e) {
                log.warn("[拣货波次] 解析波次号流水号失败，使用默认值1，波次号：{}", lastWaveNo);
            }
        }

        return StrUtil.format("WAVE-{}-{}", date, String.format("%04d", serialNo));
    }
}

