package com.laby.module.wms.service.stocktaking;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPlanPageReqVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPlanRespVO;
import com.laby.module.wms.controller.admin.stocktaking.vo.StockTakingPlanSaveReqVO;
import com.laby.module.wms.convert.stocktaking.StockTakingPlanConvert;
import com.laby.module.wms.dal.dataobject.stocktaking.StockTakingPlanDO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import com.laby.module.wms.dal.mysql.stocktaking.StockTakingPlanMapper;
import com.laby.module.wms.dal.mysql.warehouse.WarehouseMapper;
import com.laby.module.wms.enums.StockTakingPlanStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.module.wms.enums.ErrorCodeConstants.*;

/**
 * 盘点计划 Service 实现类
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class StockTakingPlanServiceImpl implements StockTakingPlanService {

    @Resource
    private StockTakingPlanMapper stockTakingPlanMapper;

    @Resource
    private WarehouseMapper warehouseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStockTakingPlan(StockTakingPlanSaveReqVO createReqVO) {
        // 1. 转换为 DO 对象
        StockTakingPlanDO plan = StockTakingPlanConvert.INSTANCE.convert(createReqVO);

        // 2. 生成计划编号（格式：PLAN + yyyyMMdd + 4位序列号）
        String planNo = generatePlanNo();
        plan.setPlanNo(planNo);

        // 3. 设置初始状态为待审核，初始化计数器
        plan.setStatus(StockTakingPlanStatusEnum.PENDING_AUDIT.getStatus());
        plan.setTotalCount(0);
        plan.setCompletedCount(0);
        plan.setDiffCount(0);

        // 4. 插入数据库
        stockTakingPlanMapper.insert(plan);

        log.info("[盘点计划] 创建盘点计划，计划编号：{}，计划名称：{}", planNo, plan.getPlanName());

        return plan.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockTakingPlan(StockTakingPlanSaveReqVO updateReqVO) {
        // 1. 校验盘点计划是否存在
        StockTakingPlanDO plan = validateStockTakingPlanExists(updateReqVO.getId());

        // 2. 校验状态是否允许修改（只有待审核状态可以修改）
        if (!StockTakingPlanStatusEnum.PENDING_AUDIT.getStatus().equals(plan.getStatus())) {
            throw exception(STOCK_TAKING_PLAN_NOT_ALLOW_UPDATE);
        }

        // 3. 转换并更新
        StockTakingPlanDO updateObj = StockTakingPlanConvert.INSTANCE.convert(updateReqVO);
        updateObj.setId(updateReqVO.getId());
        stockTakingPlanMapper.updateById(updateObj);

        log.info("[盘点计划] 更新盘点计划，计划编号：{}", plan.getPlanNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStockTakingPlan(Long id) {
        // 1. 校验盘点计划是否存在
        StockTakingPlanDO plan = validateStockTakingPlanExists(id);

        // 2. 校验状态是否允许删除（只有待审核状态可以删除）
        if (!StockTakingPlanStatusEnum.PENDING_AUDIT.getStatus().equals(plan.getStatus())) {
            throw exception(STOCK_TAKING_PLAN_NOT_ALLOW_DELETE);
        }

        // 3. 删除
        stockTakingPlanMapper.deleteById(id);

        log.info("[盘点计划] 删除盘点计划，计划编号：{}", plan.getPlanNo());
    }

    @Override
    public StockTakingPlanRespVO getStockTakingPlan(Long id) {
        // 1. 查询盘点计划
        StockTakingPlanDO plan = validateStockTakingPlanExists(id);

        // 2. 转换为 VO
        StockTakingPlanRespVO respVO = StockTakingPlanConvert.INSTANCE.convert(plan);

        // 3. 填充仓库名称
        if (plan.getWarehouseId() != null) {
            WarehouseDO warehouse = warehouseMapper.selectById(plan.getWarehouseId());
            if (warehouse != null) {
                respVO.setWarehouseName(warehouse.getWarehouseName());
            }
        }

        return respVO;
    }

    @Override
    public PageResult<StockTakingPlanRespVO> getStockTakingPlanPage(StockTakingPlanPageReqVO pageReqVO) {
        // 1. 分页查询
        PageResult<StockTakingPlanDO> pageResult = stockTakingPlanMapper.selectPage(pageReqVO);

        // 2. 转换为 VO
        PageResult<StockTakingPlanRespVO> voPageResult = StockTakingPlanConvert.INSTANCE.convertPage(pageResult);

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
    public void auditStockTakingPlan(Long id) {
        // 1. 校验盘点计划是否存在
        StockTakingPlanDO plan = validateStockTakingPlanExists(id);

        // 2. 校验状态是否允许审核（只有待审核状态可以审核）
        if (!StockTakingPlanStatusEnum.PENDING_AUDIT.getStatus().equals(plan.getStatus())) {
            throw exception(STOCK_TAKING_PLAN_NOT_ALLOW_AUDIT);
        }

        // 3. 更新状态为待执行
        StockTakingPlanDO updateObj = new StockTakingPlanDO();
        updateObj.setId(id);
        updateObj.setStatus(StockTakingPlanStatusEnum.PENDING.getStatus());
        updateObj.setAuditTime(LocalDateTime.now());
        // TODO: 获取当前登录用户名作为审核人
        updateObj.setAuditUser("系统");
        stockTakingPlanMapper.updateById(updateObj);

        log.info("[盘点计划] 审核盘点计划，计划编号：{}", plan.getPlanNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelStockTakingPlan(Long id) {
        // 1. 校验盘点计划是否存在
        StockTakingPlanDO plan = validateStockTakingPlanExists(id);

        // 2. 校验状态是否允许取消（待审核、待执行、执行中状态可以取消）
        if (!StockTakingPlanStatusEnum.PENDING_AUDIT.getStatus().equals(plan.getStatus()) 
                && !StockTakingPlanStatusEnum.PENDING.getStatus().equals(plan.getStatus())
                && !StockTakingPlanStatusEnum.PROCESSING.getStatus().equals(plan.getStatus())) {
            throw exception(STOCK_TAKING_PLAN_NOT_ALLOW_CANCEL);
        }

        // 3. 更新状态为已取消
        StockTakingPlanDO updateObj = new StockTakingPlanDO();
        updateObj.setId(id);
        updateObj.setStatus(StockTakingPlanStatusEnum.CANCELLED.getStatus());
        stockTakingPlanMapper.updateById(updateObj);

        log.info("[盘点计划] 取消盘点计划，计划编号：{}", plan.getPlanNo());
    }

    /**
     * 校验盘点计划是否存在
     */
    private StockTakingPlanDO validateStockTakingPlanExists(Long id) {
        StockTakingPlanDO plan = stockTakingPlanMapper.selectById(id);
        if (plan == null) {
            throw exception(STOCK_TAKING_PLAN_NOT_EXISTS);
        }
        return plan;
    }

    /**
     * 生成计划编号
     * 格式：PLAN + yyyyMMdd + 4位序列号
     */
    private String generatePlanNo() {
        String date = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        String sequence = String.format("%04d", IdUtil.getSnowflakeNextId() % 10000);
        return "PLAN" + date + sequence;
    }

}

