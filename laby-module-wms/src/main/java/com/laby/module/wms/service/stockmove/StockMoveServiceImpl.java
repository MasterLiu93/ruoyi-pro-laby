package com.laby.module.wms.service.stockmove;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.stockmove.vo.StockMovePageReqVO;
import com.laby.module.wms.controller.admin.stockmove.vo.StockMoveRespVO;
import com.laby.module.wms.controller.admin.stockmove.vo.StockMoveSaveReqVO;
import com.laby.module.wms.convert.stockmove.StockMoveConvert;
import com.laby.module.wms.dal.dataobject.stockmove.StockMoveDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.mysql.stockmove.StockMoveMapper;
import com.laby.module.wms.dal.mysql.warehouse.WarehouseMapper;
import com.laby.module.wms.enums.StockMoveStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.module.wms.enums.ErrorCodeConstants.*;

/**
 * 移库管理 Service 实现类
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class StockMoveServiceImpl implements StockMoveService {

    @Resource
    private StockMoveMapper stockMoveMapper;

    @Resource
    private WarehouseMapper warehouseMapper;

    /**
     * 创建移库单
     *
     * @param createReqVO 创建信息
     * @return 移库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStockMove(StockMoveSaveReqVO createReqVO) {
        // 1. 校验源库位和目标库位不能相同
        if (createReqVO.getFromLocationId().equals(createReqVO.getToLocationId())) {
            throw exception(STOCK_MOVE_SAME_LOCATION);
        }

        // 2. 转换为 DO 对象
        StockMoveDO stockMove = StockMoveConvert.INSTANCE.convert(createReqVO);

        // 3. 生成移库单号（格式：MOVE + yyyyMMdd + 4位序列号）
        String moveNo = generateMoveNo();
        stockMove.setMoveNo(moveNo);

        // 4. 设置初始状态为待执行
        stockMove.setStatus(StockMoveStatusEnum.PENDING.getStatus());

        // 5. 插入数据库
        stockMoveMapper.insert(stockMove);

        log.info("[移库管理] 创建移库单，移库单号：{}，商品：{}，数量：{}", 
                moveNo, stockMove.getGoodsName(), stockMove.getQuantity());

        return stockMove.getId();
    }

    /**
     * 更新移库单
     *
     * @param updateReqVO 更新信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockMove(StockMoveSaveReqVO updateReqVO) {
        // 1. 校验移库单是否存在
        StockMoveDO stockMove = validateStockMoveExists(updateReqVO.getId());

        // 2. 校验移库单状态是否允许修改（只有待执行状态可以修改）
        if (!StockMoveStatusEnum.PENDING.getStatus().equals(stockMove.getStatus())) {
            throw exception(STOCK_MOVE_NOT_ALLOW_UPDATE);
        }

        // 3. 校验源库位和目标库位不能相同
        if (updateReqVO.getFromLocationId().equals(updateReqVO.getToLocationId())) {
            throw exception(STOCK_MOVE_SAME_LOCATION);
        }

        // 4. 转换为 DO 对象并更新
        StockMoveDO updateObj = StockMoveConvert.INSTANCE.convert(updateReqVO);
        updateObj.setId(updateReqVO.getId());
        stockMoveMapper.updateById(updateObj);

        log.info("[移库管理] 更新移库单，移库单号：{}", stockMove.getMoveNo());
    }

    /**
     * 删除移库单
     *
     * @param id 移库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStockMove(Long id) {
        // 1. 校验移库单是否存在
        StockMoveDO stockMove = validateStockMoveExists(id);

        // 2. 校验移库单状态是否允许删除（只有待执行状态可以删除）
        if (!StockMoveStatusEnum.PENDING.getStatus().equals(stockMove.getStatus())) {
            throw exception(STOCK_MOVE_NOT_ALLOW_DELETE);
        }

        // 3. 删除移库单
        stockMoveMapper.deleteById(id);

        log.info("[移库管理] 删除移库单，移库单号：{}", stockMove.getMoveNo());
    }

    /**
     * 获得移库单
     *
     * @param id 移库单ID
     * @return 移库单
     */
    @Override
    public StockMoveRespVO getStockMove(Long id) {
        // 1. 查询移库单
        StockMoveDO stockMove = validateStockMoveExists(id);

        // 2. 转换为 VO
        StockMoveRespVO respVO = StockMoveConvert.INSTANCE.convert(stockMove);

        // 3. 填充仓库名称
        if (stockMove.getWarehouseId() != null) {
            WarehouseDO warehouse = warehouseMapper.selectById(stockMove.getWarehouseId());
            if (warehouse != null) {
                respVO.setWarehouseName(warehouse.getWarehouseName());
            }
        }

        return respVO;
    }

    /**
     * 获得移库单分页
     *
     * @param pageReqVO 分页查询
     * @return 移库单分页
     */
    @Override
    public PageResult<StockMoveRespVO> getStockMovePage(StockMovePageReqVO pageReqVO) {
        // 1. 分页查询
        PageResult<StockMoveDO> pageResult = stockMoveMapper.selectPage(pageReqVO);

        // 2. 转换为 VO
        PageResult<StockMoveRespVO> voPageResult = StockMoveConvert.INSTANCE.convertPage(pageResult);

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

    /**
     * 执行移库
     *
     * @param id 移库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeStockMove(Long id) {
        // 1. 校验移库单是否存在
        StockMoveDO stockMove = validateStockMoveExists(id);

        // 2. 校验移库单状态是否允许执行（只有待执行状态可以执行）
        if (!StockMoveStatusEnum.PENDING.getStatus().equals(stockMove.getStatus())) {
            throw exception(STOCK_MOVE_NOT_ALLOW_EXECUTE);
        }

        // 3. 更新移库单状态为执行中
        StockMoveDO updateObj = new StockMoveDO();
        updateObj.setId(id);
        updateObj.setStatus(StockMoveStatusEnum.PROCESSING.getStatus());
        stockMoveMapper.updateById(updateObj);

        log.info("[移库管理] 执行移库，移库单号：{}", stockMove.getMoveNo());
    }

    /**
     * 完成移库
     *
     * @param id 移库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeStockMove(Long id) {
        // 1. 校验移库单是否存在
        StockMoveDO stockMove = validateStockMoveExists(id);

        // 2. 校验移库单状态是否允许完成（只有执行中状态可以完成）
        if (!StockMoveStatusEnum.PROCESSING.getStatus().equals(stockMove.getStatus())) {
            throw exception(STOCK_MOVE_NOT_ALLOW_COMPLETE);
        }

        // 3. 更新移库单状态为已完成，并记录操作人和操作时间
        StockMoveDO updateObj = new StockMoveDO();
        updateObj.setId(id);
        updateObj.setStatus(StockMoveStatusEnum.COMPLETED.getStatus());
        updateObj.setOperateTime(LocalDateTime.now());
        // TODO: 获取当前登录用户名作为操作人
        updateObj.setOperator("系统");
        stockMoveMapper.updateById(updateObj);

        // 4. TODO: 更新库存（从源库位扣减，目标库位增加）
        // 这里需要调用库存服务的接口来更新库存
        // stockService.moveStock(stockMove.getFromLocationId(), stockMove.getToLocationId(), 
        //                        stockMove.getGoodsId(), stockMove.getQuantity());

        log.info("[移库管理] 完成移库，移库单号：{}，商品：{}，数量：{}", 
                stockMove.getMoveNo(), stockMove.getGoodsName(), stockMove.getQuantity());
    }

    /**
     * 取消移库
     *
     * @param id 移库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelStockMove(Long id) {
        // 1. 校验移库单是否存在
        StockMoveDO stockMove = validateStockMoveExists(id);

        // 2. 校验移库单状态是否允许取消（待执行和执行中状态可以取消）
        if (!StockMoveStatusEnum.PENDING.getStatus().equals(stockMove.getStatus()) 
                && !StockMoveStatusEnum.PROCESSING.getStatus().equals(stockMove.getStatus())) {
            throw exception(STOCK_MOVE_NOT_ALLOW_CANCEL);
        }

        // 3. 更新移库单状态为已取消
        StockMoveDO updateObj = new StockMoveDO();
        updateObj.setId(id);
        updateObj.setStatus(StockMoveStatusEnum.CANCELLED.getStatus());
        stockMoveMapper.updateById(updateObj);

        log.info("[移库管理] 取消移库，移库单号：{}", stockMove.getMoveNo());
    }

    /**
     * 校验移库单是否存在
     *
     * @param id 移库单ID
     * @return 移库单
     */
    private StockMoveDO validateStockMoveExists(Long id) {
        StockMoveDO stockMove = stockMoveMapper.selectById(id);
        if (stockMove == null) {
            throw exception(STOCK_MOVE_NOT_EXISTS);
        }
        return stockMove;
    }

    /**
     * 生成移库单号
     * 格式：MOVE + yyyyMMdd + 4位序列号
     *
     * @return 移库单号
     */
    private String generateMoveNo() {
        String date = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        String sequence = String.format("%04d", IdUtil.getSnowflakeNextId() % 10000);
        return "MOVE" + date + sequence;
    }

}

