package com.laby.module.wms.controller.admin.report;

import com.laby.framework.common.pojo.CommonResult;
import com.laby.module.wms.controller.admin.report.vo.InOutReportReqVO;
import com.laby.module.wms.controller.admin.report.vo.InOutReportRespVO;
import com.laby.module.wms.controller.admin.report.vo.InOutReportSummaryVO;
import com.laby.module.wms.service.report.InOutReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static com.laby.framework.common.pojo.CommonResult.success;

/**
 * 出入库统计 Controller
 *
 * @author laby
 */
@Tag(name = "管理后台 - 出入库统计")
@RestController
@RequestMapping("/wms/inout-report")
@Validated
@Slf4j
public class InOutReportController {

    @Resource
    private InOutReportService inOutReportService;

    /**
     * 获得出入库统计列表
     */
    @GetMapping("/list")
    @Operation(summary = "获得出入库统计列表")
    @PreAuthorize("@ss.hasPermission('wms:inout-report:query')")
    public CommonResult<List<InOutReportRespVO>> getInOutReportList(@Valid InOutReportReqVO reqVO) {
        return success(inOutReportService.getInOutReportList(reqVO));
    }

    /**
     * 获得出入库统计汇总
     */
    @GetMapping("/summary")
    @Operation(summary = "获得出入库统计汇总")
    @PreAuthorize("@ss.hasPermission('wms:inout-report:query')")
    public CommonResult<InOutReportSummaryVO> getInOutReportSummary(@Valid InOutReportReqVO reqVO) {
        return success(inOutReportService.getInOutReportSummary(reqVO));
    }

}

