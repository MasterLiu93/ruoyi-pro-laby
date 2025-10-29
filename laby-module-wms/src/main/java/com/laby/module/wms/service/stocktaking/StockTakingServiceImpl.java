package com.laby.module.wms.service.stocktaking;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPageReqVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingRespVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingSaveReqVO;
import com.laby.module.wms.convert.stocktaking.StockTakingConvert;
import com.laby.module.wms.dal.dataobject.stocktaking.StockTakingDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.mysql.stocktaking.StockTakingMapper;
import com.laby.module.wms.dal.mysql.warehouse.WarehouseMapper;
import com.laby.module.wms.enums.StockTakingStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.module.wms.enums.ErrorCodeConstants.*;

/**
 * 盘点单 Service 实现类
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class StockTakingServiceImpl implements StockTakingService {

    @Resource
    private StockTakingMapper stockTakingMapper;

    @Resource
    private WarehouseMapper warehouseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStockTaking(StockTakingSaveReqVO createReqVO) {
        // 1. 转换为 DO 对象
        StockTakingDO taking = StockTakingConvert.INSTANCE.convert(createReqVO);

        // 2. 生成盘点单号（格式：TAKE + yyyyMMdd + 4位序列号）
        String takingNo = generateTakingNo();
        taking.setTakingNo(takingNo);

        // 3. 设置初始状态为待盘点
        taking.setStatus(StockTakingStatusEnum.PENDING.getStatus());

        // 4. 插入数据库
        stockTakingMapper.insert(taking);

        log.info("[盘点单] 创建盘点单，盘点单号：{}，商品：{}", takingNo, taking.getGoodsName());

        return taking.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockTaking(StockTakingSaveReqVO updateReqVO) {
        // 1. 校验盘点单是否存在
        StockTakingDO taking = validateStockTakingExists(updateReqVO.getId());

        // 2. 校验状态是否允许修改（只有待盘点状态可以修改）
        if (!StockTakingStatusEnum.PENDING.getStatus().equals(taking.getStatus())) {
            throw exception(STOCK_TAKING_NOT_ALLOW_UPDATE);
        }

        // 3. 转换并更新
        StockTakingDO updateObj = StockTakingConvert.INSTANCE.convert(updateReqVO);
        updateObj.setId(updateReqVO.getId());
        stockTakingMapper.updateById(updateObj);

        log.info("[盘点单] 更新盘点单，盘点单号：{}", taking.getTakingNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStockTaking(Long id) {
        // 1. 校验盘点单是否存在
        StockTakingDO taking = validateStockTakingExists(id);

        // 2. 校验状态是否允许删除（只有待盘点状态可以删除）
        if (!StockTakingStatusEnum.PENDING.getStatus().equals(taking.getStatus())) {
            throw exception(STOCK_TAKING_NOT_ALLOW_UPDATE);
        }

        // 3. 删除
        stockTakingMapper.deleteById(id);

        log.info("[盘点单] 删除盘点单，盘点单号：{}", taking.getTakingNo());
    }

    @Override
    public StockTakingRespVO getStockTaking(Long id) {
        // 1. 查询盘点单
        StockTakingDO taking = validateStockTakingExists(id);

        // 2. 转换为 VO
        StockTakingRespVO respVO = StockTakingConvert.INSTANCE.convert(taking);

        // 3. 填充仓库名称
        if (taking.getWarehouseId() != null) {
            WarehouseDO warehouse = warehouseMapper.selectById(taking.getWarehouseId());
            if (warehouse != null) {
                respVO.setWarehouseName(warehouse.getWarehouseName());
            }
        }

        return respVO;
    }

    @Override
    public PageResult<StockTakingRespVO> getStockTakingPage(StockTakingPageReqVO pageReqVO) {
        // 1. 分页查询
        PageResult<StockTakingDO> pageResult = stockTakingMapper.selectPage(pageReqVO);

        // 2. 转换为 VO
        PageResult<StockTakingRespVO> voPageResult = StockTakingConvert.INSTANCE.convertPage(pageResult);

        // 3. 填充仓库名称
        voPageResult.getList().forEach(item -> {
            if (item.getWarehouseId() != null) {
                WarehouseDO warehouse = warehouseMapper.selectById(item.getWarehouseId());
                if (warehouse != null) {
                    item.setWarehouseName(warehouse.getWarehouseName());
                }
            }
        });

        return voPageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitStockTaking(Long id, BigDecimal actualQuantity, String diffReason) {
        // 1. 校验盘点单是否存在
        StockTakingDO taking = validateStockTakingExists(id);

        // 2. 校验状态是否允许提交（只有待盘点状态可以提交）
        if (!StockTakingStatusEnum.PENDING.getStatus().equals(taking.getStatus())) {
            throw exception(STOCK_TAKING_NOT_ALLOW_UPDATE);
        }

        // 3. 更新实盘数量和状态
        StockTakingDO updateObj = new StockTakingDO();
        updateObj.setId(id);
        updateObj.setActualQuantity(actualQuantity);
        updateObj.setDiffReason(diffReason);
        updateObj.setStatus(StockTakingStatusEnum.COUNTED.getStatus());
        updateObj.setOperateTime(LocalDateTime.now());
        // TODO: 获取当前登录用户名作为盘点人
        updateObj.setOperator("系统");
        stockTakingMapper.updateById(updateObj);

        log.info("[盘点单] 提交盘点，盘点单号：{}，账面：{}，实盘：{}", 
                taking.getTakingNo(), taking.getBookQuantity(), actualQuantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewStockTaking(Long id) {
        // 1. 校验盘点单是否存在
        StockTakingDO taking = validateStockTakingExists(id);

        // 2. 校验状态是否允许复核（只有已盘点状态可以复核）
        if (!StockTakingStatusEnum.COUNTED.getStatus().equals(taking.getStatus())) {
            throw exception(STOCK_TAKING_NOT_ALLOW_UPDATE);
        }

        // 3. 更新状态为已复核
        StockTakingDO updateObj = new StockTakingDO();
        updateObj.setId(id);
        updateObj.setStatus(StockTakingStatusEnum.REVIEWED.getStatus());
        updateObj.setReviewTime(LocalDateTime.now());
        // TODO: 获取当前登录用户名作为复核人
        updateObj.setReviewer("系统");
        stockTakingMapper.updateById(updateObj);

        log.info("[盘点单] 复核盘点，盘点单号：{}", taking.getTakingNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustStockTaking(Long id) {
        // 1. 校验盘点单是否存在
        StockTakingDO taking = validateStockTakingExists(id);

        // 2. 校验状态是否允许调整（只有已复核状态可以调整）
        if (!StockTakingStatusEnum.REVIEWED.getStatus().equals(taking.getStatus())) {
            throw exception(STOCK_TAKING_NOT_ALLOW_UPDATE);
        }

        // 3. 计算差异数量
        BigDecimal diffQuantity = taking.getActualQuantity().subtract(taking.getBookQuantity());

        // 4. TODO: 调整库存（调用库存服务）
        // if (diffQuantity.compareTo(BigDecimal.ZERO) != 0) {
        //     inventoryService.adjustInventory(taking.getWarehouseId(), taking.getLocationId(), 
        //         taking.getGoodsId(), diffQuantity, "盘点调整");
        // }

        // 5. 更新状态为已调整
        StockTakingDO updateObj = new StockTakingDO();
        updateObj.setId(id);
        updateObj.setStatus(StockTakingStatusEnum.ADJUSTED.getStatus());
        stockTakingMapper.updateById(updateObj);

        log.info("[盘点单] 调整库存，盘点单号：{}，差异：{}", taking.getTakingNo(), diffQuantity);
    }

    /**
     * 校验盘点单是否存在
     */
    private StockTakingDO validateStockTakingExists(Long id) {
        StockTakingDO taking = stockTakingMapper.selectById(id);
        if (taking == null) {
            throw exception(STOCK_TAKING_NOT_EXISTS);
        }
        return taking;
    }

    /**
     * 生成盘点单号
     * 格式：TAKE + yyyyMMdd + 4位序列号
     */
    private String generateTakingNo() {
        String date = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        String sequence = String.format("%04d", IdUtil.getSnowflakeNextId() % 10000);
        return "TAKE" + date + sequence;
    }

}

