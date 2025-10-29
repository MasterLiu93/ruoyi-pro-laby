package com.laby.module.wms.service.carrier;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.carrier.vo.CarrierPageReqVO;
import com.laby.module.wms.controller.admin.carrier.vo.CarrierRespVO;
import com.laby.module.wms.controller.admin.carrier.vo.CarrierSaveReqVO;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 承运商信息 Service 接口
 * 
 * 功能说明：
 * - 提供承运商的 CRUD 操作
 * - 支持分页查询、简单列表查询
 * - 提供批量查询接口供其他模块使用
 * 
 * @author laby
 */
public interface CarrierService {

    /**
     * 创建承运商
     * 
     * @param createReqVO 创建信息
     * @return 承运商ID
     */
    Long createCarrier(@Valid CarrierSaveReqVO createReqVO);

    /**
     * 更新承运商
     * 
     * @param updateReqVO 更新信息
     */
    void updateCarrier(@Valid CarrierSaveReqVO updateReqVO);

    /**
     * 删除承运商
     * 
     * @param id 承运商ID
     */
    void deleteCarrier(Long id);

    /**
     * 获得承运商详情
     * 
     * @param id 承运商ID
     * @return 承运商详情
     */
    CarrierRespVO getCarrier(Long id);

    /**
     * 获得承运商分页列表
     * 
     * @param pageReqVO 分页查询条件
     * @return 分页列表
     */
    PageResult<CarrierRespVO> getCarrierPage(CarrierPageReqVO pageReqVO);

    /**
     * 获得承运商简单列表（用于下拉选择）
     * 
     * @return 承运商简单列表（仅包含ID和名称）
     */
    List<CarrierRespVO> getCarrierSimpleList();

    /**
     * 获得承运商 Map（用于批量关联查询）
     * Key: 承运商ID，Value: 承运商名称
     * 
     * @param ids 承运商ID列表
     * @return 承运商Map
     */
    Map<Long, String> getCarrierMap(Collection<Long> ids);
}

