package com.laby.module.wms.service.report;

import cn.hutool.core.date.DateUtil;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.report.vo.InOutReportReqVO;
import com.laby.module.wms.controller.admin.report.vo.InOutReportRespVO;
import com.laby.module.wms.controller.admin.report.vo.InOutReportSummaryVO;
import com.laby.module.wms.dal.dataobject.inbound.InboundDO;
import com.laby.module.wms.dal.dataobject.outbound.OutboundDO;
import com.laby.module.wms.dal.mysql.inbound.InboundMapper;
import com.laby.module.wms.dal.mysql.outbound.OutboundMapper;
import com.laby.module.wms.enums.InboundStatusEnum;
import com.laby.module.wms.enums.OutboundStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 出入库统计 Service 实现类
 *
 * @author laby
 */
@Service
@Validated
@Slf4j
public class InOutReportServiceImpl implements InOutReportService {

    @Resource
    private InboundMapper inboundMapper;

    @Resource
    private OutboundMapper outboundMapper;

    @Override
    public List<InOutReportRespVO> getInOutReportList(InOutReportReqVO reqVO) {
        // 1. 确定时间范围
        LocalDateTime startTime = reqVO.getStartTime() != null ? reqVO.getStartTime() : LocalDateTime.now().minusMonths(1);
        LocalDateTime endTime = reqVO.getEndTime() != null ? reqVO.getEndTime() : LocalDateTime.now();

        // 2. 查询入库数据（仅已完成的）
        LambdaQueryWrapperX<InboundDO> inboundWrapper = new LambdaQueryWrapperX<>();
        inboundWrapper.eq(InboundDO::getStatus, InboundStatusEnum.COMPLETED.getStatus());
        inboundWrapper.ge(InboundDO::getCompleteTime, startTime);
        inboundWrapper.le(InboundDO::getCompleteTime, endTime);
        if (reqVO.getWarehouseId() != null) {
            inboundWrapper.eq(InboundDO::getWarehouseId, reqVO.getWarehouseId());
        }
        if (reqVO.getSupplierId() != null) {
            inboundWrapper.eq(InboundDO::getSupplierId, reqVO.getSupplierId());
        }
        List<InboundDO> inboundList = inboundMapper.selectList(inboundWrapper);

        // 3. 查询出库数据（仅已发货的）
        LambdaQueryWrapperX<OutboundDO> outboundWrapper = new LambdaQueryWrapperX<>();
        outboundWrapper.eq(OutboundDO::getStatus, OutboundStatusEnum.SHIPPED.getStatus());
        outboundWrapper.ge(OutboundDO::getCompleteTime, startTime);
        outboundWrapper.le(OutboundDO::getCompleteTime, endTime);
        if (reqVO.getWarehouseId() != null) {
            outboundWrapper.eq(OutboundDO::getWarehouseId, reqVO.getWarehouseId());
        }
        if (reqVO.getCustomerId() != null) {
            outboundWrapper.eq(OutboundDO::getCustomerId, reqVO.getCustomerId());
        }
        List<OutboundDO> outboundList = outboundMapper.selectList(outboundWrapper);

        // 4. 按日期分组统计
        Map<LocalDate, InOutReportRespVO> reportMap = new TreeMap<>();

        // 统计入库
        for (InboundDO inbound : inboundList) {
            if (inbound.getCompleteTime() == null) continue;
            LocalDate date = inbound.getCompleteTime().toLocalDate();
            InOutReportRespVO vo = reportMap.computeIfAbsent(date, k -> {
                InOutReportRespVO newVo = new InOutReportRespVO();
                newVo.setStatisticDate(k);
                newVo.setInboundQuantity(BigDecimal.ZERO);
                newVo.setInboundOrderCount(0);
                newVo.setOutboundQuantity(BigDecimal.ZERO);
                newVo.setOutboundOrderCount(0);
                return newVo;
            });
            vo.setInboundQuantity(vo.getInboundQuantity().add(inbound.getTotalQuantity() != null ? inbound.getTotalQuantity() : BigDecimal.ZERO));
            vo.setInboundOrderCount(vo.getInboundOrderCount() + 1);
        }

        // 统计出库
        for (OutboundDO outbound : outboundList) {
            if (outbound.getCompleteTime() == null) continue;
            LocalDate date = outbound.getCompleteTime().toLocalDate();
            InOutReportRespVO vo = reportMap.computeIfAbsent(date, k -> {
                InOutReportRespVO newVo = new InOutReportRespVO();
                newVo.setStatisticDate(k);
                newVo.setInboundQuantity(BigDecimal.ZERO);
                newVo.setInboundOrderCount(0);
                newVo.setOutboundQuantity(BigDecimal.ZERO);
                newVo.setOutboundOrderCount(0);
                return newVo;
            });
            vo.setOutboundQuantity(vo.getOutboundQuantity().add(outbound.getTotalQuantity() != null ? outbound.getTotalQuantity() : BigDecimal.ZERO));
            vo.setOutboundOrderCount(vo.getOutboundOrderCount() + 1);
        }

        // 5. 计算净变化
        reportMap.values().forEach(vo -> {
            BigDecimal netChange = vo.getInboundQuantity().subtract(vo.getOutboundQuantity());
            vo.setNetChange(netChange);
        });

        return new ArrayList<>(reportMap.values());
    }

    @Override
    public InOutReportSummaryVO getInOutReportSummary(InOutReportReqVO reqVO) {
        // 1. 获取统计列表
        List<InOutReportRespVO> reportList = getInOutReportList(reqVO);

        // 2. 汇总数据
        InOutReportSummaryVO summary = new InOutReportSummaryVO();
        
        BigDecimal totalInbound = BigDecimal.ZERO;
        int inboundOrderCount = 0;
        BigDecimal totalOutbound = BigDecimal.ZERO;
        int outboundOrderCount = 0;

        for (InOutReportRespVO vo : reportList) {
            totalInbound = totalInbound.add(vo.getInboundQuantity());
            inboundOrderCount += vo.getInboundOrderCount();
            totalOutbound = totalOutbound.add(vo.getOutboundQuantity());
            outboundOrderCount += vo.getOutboundOrderCount();
        }

        summary.setTotalInboundQuantity(totalInbound);
        summary.setTotalInboundOrderCount(inboundOrderCount);
        summary.setTotalOutboundQuantity(totalOutbound);
        summary.setTotalOutboundOrderCount(outboundOrderCount);
        summary.setNetChange(totalInbound.subtract(totalOutbound));

        // 3. 计算周转率（出库/入库 * 100%）
        if (totalInbound.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal turnoverRate = totalOutbound.divide(totalInbound, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            summary.setTurnoverRate(turnoverRate.setScale(2, RoundingMode.HALF_UP));
        } else {
            summary.setTurnoverRate(BigDecimal.ZERO);
        }

        return summary;
    }

}

