package com.laby.module.wms.service.report;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.report.vo.InventoryReportReqVO;
import com.laby.module.wms.controller.admin.report.vo.InventoryReportRespVO;
import com.laby.module.wms.controller.admin.report.vo.InventoryReportSummaryVO;

/**
 * 库存报表 Service 接口
 *
 * @author laby
 */
public interface InventoryReportService {

    /**
     * 获取库存报表分页
     *
     * @param reqVO 查询条件
     * @return 库存报表分页
     */
    PageResult<InventoryReportRespVO> getInventoryReportPage(InventoryReportReqVO reqVO);

    /**
     * 获取库存报表汇总
     *
     * @param reqVO 查询条件
     * @return 汇总数据
     */
    InventoryReportSummaryVO getInventoryReportSummary(InventoryReportReqVO reqVO);

}

