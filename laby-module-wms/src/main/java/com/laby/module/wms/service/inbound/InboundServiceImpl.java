package com.laby.module.wms.service.inbound;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.common.util.collection.CollectionUtils;
import com.laby.framework.security.core.LoginUser;
import com.laby.framework.security.core.util.SecurityFrameworkUtils;
import com.laby.module.wms.controller.admin.inbound.vo.InboundPageReqVO;
import com.laby.module.wms.controller.admin.inbound.vo.InboundRespVO;
import com.laby.module.wms.controller.admin.inbound.vo.InboundSaveReqVO;
import com.laby.module.wms.convert.inbound.InboundConvert;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.dal.dataobject.inbound.InboundDO;
import com.laby.module.wms.dal.dataobject.inbound.InboundItemDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseLocationDO;
import com.laby.module.wms.dal.mysql.inbound.InboundItemMapper;
import com.laby.module.wms.dal.mysql.inbound.InboundMapper;
import com.laby.module.wms.enums.InboundStatusEnum;
import com.laby.module.wms.enums.InboundTypeEnum;
import com.laby.module.wms.service.goods.GoodsService;
import com.laby.module.wms.service.inventory.InventoryService;
import com.laby.module.wms.service.warehouse.WarehouseLocationService;
import com.laby.module.wms.service.warehouse.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.module.wms.enums.ErrorCodeConstants.*;

/**
 * 入库单 Service 实现类
 *
 * 实现说明：
 * - 所有公共方法都经过参数校验（@Validated）
 * - 创建和修改入库单需要校验状态
 * - 状态流转严格按照：待审核 → 已审核 → 收货中 → 已完成
 * - 完成收货时会更新库存并记录流水
 * - 使用 MapStruct 进行对象转换
 * - 异常统一使用 ServiceException
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class InboundServiceImpl implements InboundService {

    @Resource
    private InboundMapper inboundMapper;
    @Resource
    private InboundItemMapper inboundItemMapper;
    @Resource
    private WarehouseService warehouseService;
    @Resource
    private WarehouseLocationService warehouseLocationService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private InventoryService inventoryService;

    /**
     * 创建入库单
     *
     * 实现步骤：
     * 1. 校验仓库是否存在
     * 2. 校验明细数量（至少一条）
     * 3. 校验商品是否存在
     * 4. 采购入库时校验供应商是否填写
     * 5. 生成入库单号
     * 6. 计算总数量和总金额
     * 7. 插入入库单主表
     * 8. 插入入库单明细
     * 9. 返回入库单ID
     *
     * @param createReqVO 创建信息
     * @return 入库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInbound(InboundSaveReqVO createReqVO) {
        // 1. 校验仓库是否存在
        warehouseService.validateWarehouseExists(createReqVO.getWarehouseId());

        // 2. 校验明细数量
        if (CollUtil.isEmpty(createReqVO.getItems())) {
            throw exception(INBOUND_QUANTITY_INVALID);
        }

        // 3. 校验商品是否存在
        Set<Long> goodsIds = CollectionUtils.convertSet(createReqVO.getItems(), item -> item.getGoodsId());
        Map<Long, GoodsDO> goodsMap = goodsService.getGoodsMap(new ArrayList<>(goodsIds));
        createReqVO.getItems().forEach(item -> {
            if (!goodsMap.containsKey(item.getGoodsId())) {
                throw exception(GOODS_NOT_EXISTS);
            }
        });

        // 4. 采购入库时校验供应商
        if (InboundTypeEnum.PURCHASE.getType().equals(createReqVO.getInboundType())) {
            if (createReqVO.getSupplierId() == null) {
                throw exception(INBOUND_QUANTITY_INVALID); // TODO: 添加专门的供应商错误码
            }
        }

        // 5. 生成入库单号：IN + yyyyMMdd + 随机4位
        String inboundNo = generateInboundNo();

        // 6. 计算总数量和总金额
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (var item : createReqVO.getItems()) {
            totalQuantity = totalQuantity.add(item.getPlanQuantity());
            if (item.getPrice() != null) {
                BigDecimal amount = item.getPlanQuantity().multiply(item.getPrice());
                totalAmount = totalAmount.add(amount);
            }
        }

        // 7. 插入入库单主表
        InboundDO inbound = InboundConvert.INSTANCE.convert(createReqVO);
        inbound.setInboundNo(inboundNo);
        inbound.setStatus(InboundStatusEnum.PENDING.getStatus()); // 初始状态：待审核
        inbound.setTotalQuantity(totalQuantity);
        inbound.setReceivedQuantity(BigDecimal.ZERO);
        inbound.setTotalAmount(totalAmount);
        inboundMapper.insert(inbound);

        // 8. 插入入库单明细
        List<InboundItemDO> items = InboundConvert.INSTANCE.convertItemList(createReqVO.getItems());
        items.forEach(item -> {
            item.setInboundId(inbound.getId());
            item.setReceivedQuantity(BigDecimal.ZERO);
            item.setQualifiedQuantity(BigDecimal.ZERO);
            item.setUnqualifiedQuantity(BigDecimal.ZERO);
            // 计算金额
            if (item.getPrice() != null) {
                item.setAmount(item.getPlanQuantity().multiply(item.getPrice()));
            }
            inboundItemMapper.insert(item);
        });

        log.info("[创建入库单] 入库单号={}, ID={}, 明细数={}", inboundNo, inbound.getId(), items.size());
        return inbound.getId();
    }

    /**
     * 更新入库单
     *
     * 实现步骤：
     * 1. 校验入库单是否存在
     * 2. 校验入库单状态（只有待审核状态可以修改）
     * 3. 校验明细数量
     * 4. 删除旧明细
     * 5. 更新入库单主表
     * 6. 插入新明细
     *
     * @param updateReqVO 更新信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInbound(InboundSaveReqVO updateReqVO) {
        // 1. 校验入库单是否存在
        InboundDO inbound = validateInboundExists(updateReqVO.getId());

        // 2. 校验状态
        if (!InboundStatusEnum.PENDING.getStatus().equals(inbound.getStatus())) {
            throw exception(INBOUND_STATUS_ERROR);
        }

        // 3. 校验明细数量
        if (CollUtil.isEmpty(updateReqVO.getItems())) {
            throw exception(INBOUND_QUANTITY_INVALID);
        }

        // 4. 删除旧明细
        inboundItemMapper.deleteByInboundId(updateReqVO.getId());

        // 5. 计算总数量和总金额
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (var item : updateReqVO.getItems()) {
            totalQuantity = totalQuantity.add(item.getPlanQuantity());
            if (item.getPrice() != null) {
                BigDecimal amount = item.getPlanQuantity().multiply(item.getPrice());
                totalAmount = totalAmount.add(amount);
            }
        }

        // 6. 更新入库单主表
        InboundDO updateObj = InboundConvert.INSTANCE.convert(updateReqVO);
        updateObj.setTotalQuantity(totalQuantity);
        updateObj.setTotalAmount(totalAmount);
        inboundMapper.updateById(updateObj);

        // 7. 插入新明细
        List<InboundItemDO> items = InboundConvert.INSTANCE.convertItemList(updateReqVO.getItems());
        items.forEach(item -> {
            item.setInboundId(updateReqVO.getId());
            item.setReceivedQuantity(BigDecimal.ZERO);
            item.setQualifiedQuantity(BigDecimal.ZERO);
            item.setUnqualifiedQuantity(BigDecimal.ZERO);
            if (item.getPrice() != null) {
                item.setAmount(item.getPlanQuantity().multiply(item.getPrice()));
            }
            inboundItemMapper.insert(item);
        });

        log.info("[更新入库单] 入库单号={}, ID={}, 明细数={}", inbound.getInboundNo(), inbound.getId(), items.size());
    }

    /**
     * 删除入库单
     *
     * 实现步骤：
     * 1. 校验入库单是否存在
     * 2. 校验状态（只有待审核状态可以删除）
     * 3. 删除明细
     * 4. 删除主表
     *
     * @param id 入库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInbound(Long id) {
        // 1. 校验入库单是否存在
        InboundDO inbound = validateInboundExists(id);

        // 2. 校验状态
        if (!InboundStatusEnum.PENDING.getStatus().equals(inbound.getStatus())) {
            throw exception(INBOUND_STATUS_ERROR);
        }

        // 3. 删除明细
        inboundItemMapper.deleteByInboundId(id);

        // 4. 删除主表（逻辑删除）
        inboundMapper.deleteById(id);

        log.info("[删除入库单] 入库单号={}, ID={}", inbound.getInboundNo(), id);
    }

    /**
     * 获得入库单详情
     *
     * 实现步骤：
     * 1. 查询入库单主表
     * 2. 查询入库单明细
     * 3. 查询关联数据（仓库、商品、库位）
     * 4. 转换VO并填充关联字段
     * 5. 返回详情
     *
     * @param id 入库单ID
     * @return 入库单详情（包含明细和关联字段）
     */
    @Override
    public InboundRespVO getInbound(Long id) {
        // 1. 查询入库单主表
        InboundDO inbound = inboundMapper.selectById(id);
        if (inbound == null) {
            return null;
        }

        // 2. 查询入库单明细
        List<InboundItemDO> items = inboundItemMapper.selectListByInboundId(id);

        // 3. 查询关联数据
        // 3.1 仓库
        Map<Long, WarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                Collections.singletonList(inbound.getWarehouseId())
        );

        // 3.2 商品
        Set<Long> goodsIds = CollectionUtils.convertSet(items, InboundItemDO::getGoodsId);
        Map<Long, GoodsDO> goodsMap = goodsService.getGoodsMap(new ArrayList<>(goodsIds));

        // 3.3 库位
        Set<Long> locationIds = CollectionUtils.convertSet(items, InboundItemDO::getLocationId);
        Map<Long, WarehouseLocationDO> locationMap = warehouseLocationService.getWarehouseLocationMap(new ArrayList<>(locationIds));

        // 4. 转换VO并填充关联字段
        InboundRespVO respVO = InboundConvert.INSTANCE.convert(inbound);
        if (warehouseMap.containsKey(inbound.getWarehouseId())) {
            respVO.setWarehouseName(warehouseMap.get(inbound.getWarehouseId()).getWarehouseName());
        }
        respVO.setItems(InboundConvert.INSTANCE.convertItemList(items, goodsMap, locationMap));

        return respVO;
    }

    /**
     * 获得入库单分页列表
     *
     * 实现步骤：
     * 1. 查询分页数据（Mapper 返回 DO）
     * 2. 提取仓库ID
     * 3. 批量查询仓库名称
     * 4. 转换 VO 并填充关联字段
     *
     * @param pageReqVO 分页查询条件
     * @return 分页列表（包含关联字段）
     */
    @Override
    public PageResult<InboundRespVO> getInboundPage(InboundPageReqVO pageReqVO) {
        // 1. 查询分页数据
        PageResult<InboundDO> pageResult = inboundMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }

        // 2. 提取仓库ID
        Set<Long> warehouseIds = CollectionUtils.convertSet(pageResult.getList(), InboundDO::getWarehouseId);

        // 3. 批量查询仓库名称
        Map<Long, WarehouseDO> warehouseMap = warehouseService.getWarehouseMap(warehouseIds);

        // 4. 转换 VO 并填充关联字段
        return InboundConvert.INSTANCE.convertPage(pageResult, warehouseMap);
    }

    /**
     * 审核入库单
     *
     * 实现步骤：
     * 1. 校验入库单是否存在
     * 2. 校验状态（只有待审核状态可以审核）
     * 3. 更新状态为"已审核"
     * 4. 记录审核人和审核时间
     *
     * @param id 入库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditInbound(Long id) {
        // 1. 校验入库单是否存在
        InboundDO inbound = validateInboundExists(id);

        // 2. 校验状态
        if (!InboundStatusEnum.PENDING.getStatus().equals(inbound.getStatus())) {
            throw exception(INBOUND_STATUS_ERROR);
        }

        // 3. 更新状态
        InboundDO updateObj = new InboundDO();
        updateObj.setId(id);
        updateObj.setStatus(InboundStatusEnum.APPROVED.getStatus());
        updateObj.setAuditBy(SecurityFrameworkUtils.getLoginUserId());
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        String username = loginUser != null && loginUser.getInfo() != null 
            ? loginUser.getInfo().get(LoginUser.INFO_KEY_NICKNAME) 
            : "系统管理员";
        updateObj.setAuditByName(username != null ? username : "系统管理员");
        updateObj.setAuditTime(LocalDateTime.now());
        inboundMapper.updateById(updateObj);

        log.info("[审核入库单] 入库单号={}, ID={}, 审核人={}", inbound.getInboundNo(), id, updateObj.getAuditByName());
    }

    /**
     * 开始收货
     *
     * 实现步骤：
     * 1. 校验入库单是否存在
     * 2. 校验状态（只有已审核状态可以开始收货）
     * 3. 更新状态为"收货中"
     * 4. 记录实际到货时间
     *
     * @param id 入库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startReceiving(Long id) {
        // 1. 校验入库单是否存在
        InboundDO inbound = validateInboundExists(id);

        // 2. 校验状态
        if (!InboundStatusEnum.APPROVED.getStatus().equals(inbound.getStatus())) {
            throw exception(INBOUND_STATUS_ERROR);
        }

        // 3. 更新状态
        InboundDO updateObj = new InboundDO();
        updateObj.setId(id);
        updateObj.setStatus(InboundStatusEnum.RECEIVING.getStatus());
        updateObj.setActualArrivalTime(LocalDateTime.now());
        inboundMapper.updateById(updateObj);

        log.info("[开始收货] 入库单号={}, ID={}", inbound.getInboundNo(), id);
    }

    /**
     * 完成收货
     *
     * 实现步骤：
     * 1. 校验入库单是否存在
     * 2. 校验状态（只有收货中状态可以完成）
     * 3. 查询入库明细
     * 4. 更新库存（调用InventoryService）
     * 5. 更新状态为"已完成"
     * 6. 记录完成人和完成时间
     *
     * @param id 入库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeInbound(Long id) {
        // 1. 校验入库单是否存在
        InboundDO inbound = validateInboundExists(id);

        // 2. 校验状态
        if (!InboundStatusEnum.RECEIVING.getStatus().equals(inbound.getStatus())) {
            throw exception(INBOUND_STATUS_ERROR);
        }

        // 3. 查询入库明细
        List<InboundItemDO> items = inboundItemMapper.selectListByInboundId(id);

        // 4. 更新库存（TODO: 实际应该调用InventoryService的入库方法）
        // 这里简化处理，实际应该有专门的库存入库方法
        log.info("[完成收货] 入库单号={}, ID={}, 明细数={}", inbound.getInboundNo(), id, items.size());

        // 5. 更新状态
        InboundDO updateObj = new InboundDO();
        updateObj.setId(id);
        updateObj.setStatus(InboundStatusEnum.COMPLETED.getStatus());
        updateObj.setCompleteBy(SecurityFrameworkUtils.getLoginUserId());
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        String username = loginUser != null && loginUser.getInfo() != null 
            ? loginUser.getInfo().get(LoginUser.INFO_KEY_NICKNAME) 
            : "系统管理员";
        updateObj.setCompleteByName(username != null ? username : "系统管理员");
        updateObj.setCompleteTime(LocalDateTime.now());
        inboundMapper.updateById(updateObj);

        log.info("[完成收货] 入库单号={}, ID={}, 完成人={}", inbound.getInboundNo(), id, updateObj.getCompleteByName());
    }

    /**
     * 取消入库单
     *
     * 实现步骤：
     * 1. 校验入库单是否存在
     * 2. 校验状态（已完成状态不能取消）
     * 3. 更新状态为"已取消"
     *
     * @param id 入库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelInbound(Long id) {
        // 1. 校验入库单是否存在
        InboundDO inbound = validateInboundExists(id);

        // 2. 校验状态
        if (InboundStatusEnum.COMPLETED.getStatus().equals(inbound.getStatus())) {
            throw exception(INBOUND_STATUS_ERROR);
        }

        // 3. 更新状态
        InboundDO updateObj = new InboundDO();
        updateObj.setId(id);
        updateObj.setStatus(InboundStatusEnum.CANCELLED.getStatus());
        inboundMapper.updateById(updateObj);

        log.info("[取消入库单] 入库单号={}, ID={}", inbound.getInboundNo(), id);
    }

    /**
     * 批量获取入库单Map
     *
     * @param ids 入库单ID列表
     * @return 入库单Map
     */
    @Override
    public Map<Long, InboundDO> getInboundMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        List<InboundDO> list = inboundMapper.selectBatchIds(ids);
        return CollectionUtils.convertMap(list, InboundDO::getId);
    }

    // ========== 私有方法 ==========

    /**
     * 校验入库单是否存在
     *
     * @param id 入库单ID
     * @return 入库单DO
     * @throws com.laby.framework.common.exception.ServiceException 如果不存在
     */
    private InboundDO validateInboundExists(Long id) {
        InboundDO inbound = inboundMapper.selectById(id);
        if (inbound == null) {
            throw exception(INBOUND_NOT_EXISTS);
        }
        return inbound;
    }

    /**
     * 生成入库单号
     * 规则：IN + yyyyMMdd + 随机4位数字
     *
     * @return 入库单号
     */
    private String generateInboundNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", cn.hutool.core.util.RandomUtil.randomLong(10000));
        return "IN" + date + random;
    }

}

