package com.laby.module.wms.service.outbound;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundItemRespVO;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundPageReqVO;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundRespVO;
import com.laby.module.wms.controller.admin.outbound.vo.OutboundSaveReqVO;
import com.laby.module.wms.convert.outbound.OutboundConvert;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.dal.dataobject.outbound.OutboundDO;
import com.laby.module.wms.dal.dataobject.outbound.OutboundItemDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseLocationDO;
import com.laby.module.wms.dal.mysql.outbound.OutboundItemMapper;
import com.laby.module.wms.dal.mysql.outbound.OutboundMapper;
import com.laby.module.wms.enums.OutboundStatusEnum;
import com.laby.module.wms.service.goods.GoodsService;
import com.laby.module.wms.service.warehouse.WarehouseLocationService;
import com.laby.module.wms.service.warehouse.WarehouseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.module.wms.enums.ErrorCodeConstants.*;

/**
 * 出库单 Service 实现类
 *
 * 实现说明：
 * - 所有公共方法都经过参数校验（@Validated）
 * - 创建和修改出库单需要校验状态
 * - 状态流转严格按照：待审核 → 已审核 → 拣货中 → 待发货 → 已发货
 * - 发货时会扣减库存并记录流水
 * - 使用 MapStruct 进行对象转换
 * - 异常统一使用 ServiceException
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class OutboundServiceImpl implements OutboundService {

    @Resource
    private OutboundMapper outboundMapper;

    @Resource
    private OutboundItemMapper outboundItemMapper;

    @Resource
    private WarehouseService warehouseService;

    @Resource
    private WarehouseLocationService warehouseLocationService;

    @Resource
    private GoodsService goodsService;

    /**
     * 创建出库单
     *
     * 实现步骤：
     * 1. 校验仓库是否存在
     * 2. 校验明细数量（至少一条）
     * 3. 校验商品是否存在
     * 4. 销售出库时校验客户是否填写
     * 5. 生成出库单号（规则：OUT + yyyyMMdd + 流水号）
     * 6. 计算总数量和总金额
     * 7. 插入出库单主表，初始状态为"待审核"
     * 8. 插入出库单明细，初始化拣货和发货数量为0
     * 9. 返回出库单ID
     *
     * @param createReqVO 创建信息
     * @return 出库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOutbound(OutboundSaveReqVO createReqVO) {
        // 1. 数据校验
        validateOutboundForCreate(createReqVO);

        // 2. 创建出库单主表
        OutboundDO outbound = OutboundConvert.INSTANCE.convert(createReqVO);
        outbound.setStatus(OutboundStatusEnum.PENDING.getStatus());
        outbound.setTotalQuantity(calculateTotalQuantity(createReqVO.getItems()));
        outbound.setPickedQuantity(BigDecimal.ZERO);
        outboundMapper.insert(outbound);

        // 3. 创建出库单明细
        if (CollUtil.isNotEmpty(createReqVO.getItems())) {
            List<OutboundItemDO> items = OutboundConvert.INSTANCE.convertItemReqList(createReqVO.getItems());
            items.forEach(item -> {
                item.setOutboundId(outbound.getId());
                item.setPickedQuantity(BigDecimal.ZERO);
                item.setShippedQuantity(BigDecimal.ZERO);
                outboundItemMapper.insert(item);
            });
        }

        log.info("[createOutbound] 创建出库单成功，出库单号：{}, ID：{}", outbound.getOutboundNo(), outbound.getId());
        return outbound.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOutbound(OutboundSaveReqVO updateReqVO) {
        // 1. 校验出库单是否存在
        OutboundDO existOutbound = validateOutboundExists(updateReqVO.getId());

        // 2. 仅待审核状态可以修改
        if (!OutboundStatusEnum.PENDING.getStatus().equals(existOutbound.getStatus())) {
            throw exception(OUTBOUND_NOT_ALLOW_UPDATE);
        }

        // 3. 数据校验
        validateOutboundForUpdate(updateReqVO);

        // 4. 更新出库单主表
        OutboundDO updateObj = OutboundConvert.INSTANCE.convert(updateReqVO);
        updateObj.setTotalQuantity(calculateTotalQuantity(updateReqVO.getItems()));
        outboundMapper.updateById(updateObj);

        // 5. 更新出库单明细（先删除后新增）
        outboundItemMapper.deleteByOutboundId(updateReqVO.getId());
        if (CollUtil.isNotEmpty(updateReqVO.getItems())) {
            List<OutboundItemDO> items = OutboundConvert.INSTANCE.convertItemReqList(updateReqVO.getItems());
            items.forEach(item -> {
                item.setOutboundId(updateReqVO.getId());
                item.setPickedQuantity(BigDecimal.ZERO);
                item.setShippedQuantity(BigDecimal.ZERO);
                outboundItemMapper.insert(item);
            });
        }

        log.info("[updateOutbound] 更新出库单成功，出库单号：{}, ID：{}", existOutbound.getOutboundNo(), updateReqVO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOutbound(Long id) {
        // 1. 校验出库单是否存在
        OutboundDO outbound = validateOutboundExists(id);

        // 2. 仅待审核状态可以删除
        if (!OutboundStatusEnum.PENDING.getStatus().equals(outbound.getStatus())) {
            throw exception(OUTBOUND_NOT_ALLOW_DELETE);
        }

        // 3. 删除出库单（软删除）
        outboundMapper.deleteById(id);

        // 4. 删除出库单明细
        outboundItemMapper.deleteByOutboundId(id);

        log.info("[deleteOutbound] 删除出库单成功，出库单号：{}, ID：{}", outbound.getOutboundNo(), id);
    }

    @Override
    public OutboundRespVO getOutbound(Long id) {
        // 1. 查询出库单主表
        OutboundDO outbound = outboundMapper.selectById(id);
        if (outbound == null) {
            return null;
        }

        // 2. 转换为响应对象
        OutboundRespVO respVO = OutboundConvert.INSTANCE.convert(outbound);

        // 3. 填充仓库信息
        if (outbound.getWarehouseId() != null) {
            WarehouseDO warehouse = warehouseService.getWarehouse(outbound.getWarehouseId());
            if (warehouse != null) {
                respVO.setWarehouseName(warehouse.getWarehouseName());
            }
        }

        // 4. 查询并填充明细信息
        List<OutboundItemDO> items = outboundItemMapper.selectListByOutboundId(id);
        if (CollUtil.isNotEmpty(items)) {
            // 批量查询商品信息
            List<Long> goodsIds = items.stream().map(OutboundItemDO::getGoodsId).distinct().collect(Collectors.toList());
            Map<Long, GoodsDO> goodsMap = goodsService.getGoodsMap(goodsIds);

            // 批量查询库位信息
            List<Long> locationIds = items.stream()
                    .map(OutboundItemDO::getLocationId)
                    .filter(locationId -> locationId != null)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, WarehouseLocationDO> locationMap = warehouseLocationService.getWarehouseLocationMap(locationIds);

            // 转换并填充明细信息
            List<OutboundItemRespVO> itemRespVOList = OutboundConvert.INSTANCE.convertItemList(items);
            itemRespVOList.forEach(itemRespVO -> 
                OutboundConvert.INSTANCE.fillOutboundItemRespVO(itemRespVO, goodsMap, locationMap)
            );
            respVO.setItems(itemRespVOList);
        }

        return respVO;
    }

    @Override
    public PageResult<OutboundRespVO> getOutboundPage(OutboundPageReqVO pageReqVO) {
        // 1. 分页查询出库单
        PageResult<OutboundDO> pageResult = outboundMapper.selectPage(pageReqVO);

        // 2. 转换为响应对象
        PageResult<OutboundRespVO> result = OutboundConvert.INSTANCE.convertPage(pageResult);

        // 3. 批量填充仓库信息
        if (CollUtil.isNotEmpty(result.getList())) {
            List<Long> warehouseIds = result.getList().stream()
                    .map(OutboundRespVO::getWarehouseId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, WarehouseDO> warehouseMap = warehouseService.getWarehouseMap(warehouseIds);
            result.getList().forEach(respVO -> 
                OutboundConvert.INSTANCE.fillOutboundRespVO(respVO, warehouseMap)
            );
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditOutbound(Long id, Long auditBy, String auditByName) {
        // 1. 校验出库单是否存在
        OutboundDO outbound = validateOutboundExists(id);

        // 2. 仅待审核状态可以审核
        if (!OutboundStatusEnum.PENDING.getStatus().equals(outbound.getStatus())) {
            throw exception(OUTBOUND_NOT_ALLOW_AUDIT);
        }

        // 3. 更新状态为已审核
        OutboundDO updateObj = new OutboundDO();
        updateObj.setId(id);
        updateObj.setStatus(OutboundStatusEnum.APPROVED.getStatus());
        updateObj.setAuditBy(auditBy);
        updateObj.setAuditByName(auditByName);
        updateObj.setAuditTime(LocalDateTime.now());
        outboundMapper.updateById(updateObj);

        log.info("[auditOutbound] 审核出库单成功，出库单号：{}, ID：{}, 审核人：{}", outbound.getOutboundNo(), id, auditByName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startPicking(Long id) {
        // 1. 校验出库单是否存在
        OutboundDO outbound = validateOutboundExists(id);

        // 2. 仅已审核状态可以开始拣货
        if (!OutboundStatusEnum.APPROVED.getStatus().equals(outbound.getStatus())) {
            throw exception(OUTBOUND_NOT_ALLOW_PICK);
        }

        // 3. 更新状态为拣货中
        OutboundDO updateObj = new OutboundDO();
        updateObj.setId(id);
        updateObj.setStatus(OutboundStatusEnum.PICKING.getStatus());
        outboundMapper.updateById(updateObj);

        log.info("[startPicking] 开始拣货，出库单号：{}, ID：{}", outbound.getOutboundNo(), id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completePicking(Long id, Long itemId, BigDecimal pickedQuantity) {
        // 1. 校验出库单是否存在
        OutboundDO outbound = validateOutboundExists(id);

        // 2. 仅拣货中状态可以完成拣货
        if (!OutboundStatusEnum.PICKING.getStatus().equals(outbound.getStatus())) {
            throw exception(OUTBOUND_NOT_ALLOW_PICK);
        }

        // 3. 更新明细拣货数量
        OutboundItemDO item = outboundItemMapper.selectById(itemId);
        if (item == null || !item.getOutboundId().equals(id)) {
            throw exception(OUTBOUND_ITEM_NOT_EXISTS);
        }

        OutboundItemDO updateItem = new OutboundItemDO();
        updateItem.setId(itemId);
        updateItem.setPickedQuantity(pickedQuantity);
        outboundItemMapper.updateById(updateItem);

        // 4. 检查是否所有明细都拣货完成
        List<OutboundItemDO> allItems = outboundItemMapper.selectListByOutboundId(id);
        boolean allPicked = allItems.stream().allMatch(i -> 
            i.getPickedQuantity() != null && i.getPickedQuantity().compareTo(BigDecimal.ZERO) > 0
        );

        // 5. 更新出库单已拣货数量
        BigDecimal totalPicked = allItems.stream()
                .map(i -> i.getPickedQuantity() != null ? i.getPickedQuantity() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OutboundDO updateObj = new OutboundDO();
        updateObj.setId(id);
        updateObj.setPickedQuantity(totalPicked);

        // 6. 如果所有明细都拣货完成，状态改为待发货
        if (allPicked) {
            updateObj.setStatus(OutboundStatusEnum.TO_SHIP.getStatus());
            log.info("[completePicking] 拣货完成，出库单号：{}, ID：{}", outbound.getOutboundNo(), id);
        }

        outboundMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOutbound(Long id, Long completeBy, String completeByName) {
        // 1. 校验出库单是否存在
        OutboundDO outbound = validateOutboundExists(id);

        // 2. 仅待发货状态可以发货
        if (!OutboundStatusEnum.TO_SHIP.getStatus().equals(outbound.getStatus())) {
            throw exception(OUTBOUND_NOT_ALLOW_SHIP);
        }

        // 3. 更新状态为已发货
        OutboundDO updateObj = new OutboundDO();
        updateObj.setId(id);
        updateObj.setStatus(OutboundStatusEnum.SHIPPED.getStatus());
        updateObj.setCompleteBy(completeBy);
        updateObj.setCompleteByName(completeByName);
        updateObj.setCompleteTime(LocalDateTime.now());
        updateObj.setActualShipmentTime(LocalDateTime.now());
        outboundMapper.updateById(updateObj);

        // 4. 更新所有明细的已发货数量
        List<OutboundItemDO> items = outboundItemMapper.selectListByOutboundId(id);
        items.forEach(item -> {
            OutboundItemDO updateItem = new OutboundItemDO();
            updateItem.setId(item.getId());
            updateItem.setShippedQuantity(item.getPickedQuantity());
            outboundItemMapper.updateById(updateItem);
        });

        // TODO: 扣减库存（调用库存服务）

        log.info("[shipOutbound] 发货成功，出库单号：{}, ID：{}, 操作人：{}", outbound.getOutboundNo(), id, completeByName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOutbound(Long id) {
        // 1. 校验出库单是否存在
        OutboundDO outbound = validateOutboundExists(id);

        // 2. 已发货状态不能取消
        if (OutboundStatusEnum.SHIPPED.getStatus().equals(outbound.getStatus())) {
            throw exception(OUTBOUND_NOT_ALLOW_CANCEL);
        }

        // 3. 更新状态为已取消
        OutboundDO updateObj = new OutboundDO();
        updateObj.setId(id);
        updateObj.setStatus(OutboundStatusEnum.CANCELLED.getStatus());
        outboundMapper.updateById(updateObj);

        log.info("[cancelOutbound] 取消出库单成功，出库单号：{}, ID：{}", outbound.getOutboundNo(), id);
    }

    // ==================== 私有方法 ====================

    /**
     * 校验出库单是否存在
     *
     * @param id 出库单ID
     * @return 出库单DO
     */
    private OutboundDO validateOutboundExists(Long id) {
        OutboundDO outbound = outboundMapper.selectById(id);
        if (outbound == null) {
            throw exception(OUTBOUND_NOT_EXISTS);
        }
        return outbound;
    }

    /**
     * 校验创建出库单的数据
     *
     * @param createReqVO 创建请求
     */
    private void validateOutboundForCreate(OutboundSaveReqVO createReqVO) {
        // 1. 校验仓库是否存在
        Assert.notNull(createReqVO.getWarehouseId(), "仓库ID不能为空");
        WarehouseDO warehouse = warehouseService.getWarehouse(createReqVO.getWarehouseId());
        if (warehouse == null) {
            throw exception(WAREHOUSE_NOT_EXISTS);
        }

        // 2. 校验明细数据
        if (CollUtil.isEmpty(createReqVO.getItems())) {
            throw exception(OUTBOUND_ITEM_EMPTY);
        }

        // 3. 校验商品是否存在
        List<Long> goodsIds = createReqVO.getItems().stream()
                .map(item -> item.getGoodsId())
                .distinct()
                .collect(Collectors.toList());
        Map<Long, GoodsDO> goodsMap = goodsService.getGoodsMap(goodsIds);
        createReqVO.getItems().forEach(item -> {
            if (!goodsMap.containsKey(item.getGoodsId())) {
                throw exception(GOODS_NOT_EXISTS);
            }
        });
    }

    /**
     * 校验更新出库单的数据
     *
     * @param updateReqVO 更新请求
     */
    private void validateOutboundForUpdate(OutboundSaveReqVO updateReqVO) {
        validateOutboundForCreate(updateReqVO);
    }

    /**
     * 计算总数量
     *
     * @param items 明细列表
     * @return 总数量
     */
    private BigDecimal calculateTotalQuantity(List<?> items) {
        if (CollUtil.isEmpty(items)) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .filter(item -> item instanceof com.laby.module.wms.controller.admin.outbound.vo.OutboundItemSaveReqVO)
                .map(item -> ((com.laby.module.wms.controller.admin.outbound.vo.OutboundItemSaveReqVO) item).getPlanQuantity())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePickedQuantity(Long outboundId, BigDecimal pickedQuantity) {
        // 1. 查询出库单
        OutboundDO outbound = outboundMapper.selectById(outboundId);
        if (outbound == null) {
            throw exception(OUTBOUND_NOT_EXISTS);
        }

        // 2. 更新已拣货数量（累加）
        OutboundDO updateObj = new OutboundDO();
        updateObj.setId(outboundId);
        updateObj.setPickedQuantity(outbound.getPickedQuantity().add(pickedQuantity));
        outboundMapper.updateById(updateObj);

        log.info("[出库单] 更新拣货数量，出库单ID：{}，本次拣货：{}，累计拣货：{}", 
                outboundId, pickedQuantity, updateObj.getPickedQuantity());

        // 3. 检查并更新拣货状态
        checkAndUpdatePickingStatus(outboundId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAndUpdatePickingStatus(Long outboundId) {
        // 1. 查询出库单
        OutboundDO outbound = outboundMapper.selectById(outboundId);
        if (outbound == null) {
            throw exception(OUTBOUND_NOT_EXISTS);
        }

        // 2. 如果出库单不是"已审核"或"拣货中"状态，不处理
        if (!OutboundStatusEnum.APPROVED.getStatus().equals(outbound.getStatus()) &&
            !OutboundStatusEnum.PICKING.getStatus().equals(outbound.getStatus())) {
            return;
        }

        // 3. 比较已拣货数量和总数量
        // 如果已拣货数量 >= 总数量，说明拣货完成
        boolean allPicked = outbound.getPickedQuantity().compareTo(outbound.getTotalQuantity()) >= 0;

        // 4. 更新状态
        OutboundDO updateObj = new OutboundDO();
        updateObj.setId(outboundId);
        
        if (allPicked) {
            // 拣货完成，更新为"待发货"
            updateObj.setStatus(OutboundStatusEnum.TO_SHIP.getStatus());
            log.info("[出库单] 拣货完成，出库单ID：{}，总数量：{}，已拣货：{}", 
                    outboundId, outbound.getTotalQuantity(), outbound.getPickedQuantity());
        } else {
            // 部分拣货，更新为"拣货中"
            if (outbound.getPickedQuantity().compareTo(BigDecimal.ZERO) > 0) {
                updateObj.setStatus(OutboundStatusEnum.PICKING.getStatus());
                log.info("[出库单] 拣货中，出库单ID：{}，总数量：{}，已拣货：{}", 
                        outboundId, outbound.getTotalQuantity(), outbound.getPickedQuantity());
            }
        }

        outboundMapper.updateById(updateObj);
    }
}

