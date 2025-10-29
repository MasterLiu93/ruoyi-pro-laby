package com.laby.module.wms.service.report;

import com.laby.module.wms.controller.admin.report.vo.InOutReportReqVO;
import com.laby.module.wms.controller.admin.report.vo.InOutReportRespVO;
import com.laby.module.wms.controller.admin.report.vo.InOutReportSummaryVO;

import java.util.List;

/**
 * 出入库统计 Service 接口
 *
 * @author laby
 */
public interface InOutReportService {

    /**
     * 获取出入库统计列表
     *
     * @param reqVO 查询条件
     * @return 统计列表
     */
    List<InOutReportRespVO> getInOutReportList(InOutReportReqVO reqVO);

    /**
     * 获取出入库统计汇总
     *
     * @param reqVO 查询条件
     * @return 汇总数据
     */
    InOutReportSummaryVO getInOutReportSummary(InOutReportReqVO reqVO);

}

