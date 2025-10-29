package com.laby.module.wms.service.carrier;

import cn.hutool.core.collection.CollUtil;
import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.carrier.vo.CarrierPageReqVO;
import com.laby.module.wms.controller.admin.carrier.vo.CarrierRespVO;
import com.laby.module.wms.controller.admin.carrier.vo.CarrierSaveReqVO;
import com.laby.module.wms.convert.carrier.CarrierConvert;
import com.laby.module.wms.dal.dataobject.carrier.CarrierDO;
import com.laby.module.wms.dal.mysql.carrier.CarrierMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.laby.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.laby.framework.common.util.collection.CollectionUtils.convertMap;
import static com.laby.module.wms.enums.ErrorCodeConstants.*;

/**
 * 承运商信息 Service 实现类
 * 
 * 功能说明：
 * - 实现承运商的 CRUD 操作
 * - 提供业务校验（编码唯一性等）
 * - 支持分页查询和简单列表查询
 * 
 * @author laby
 */
@Service
@Validated
@Slf4j
public class CarrierServiceImpl implements CarrierService {

    @Resource
    private CarrierMapper carrierMapper;

    @Override
    public Long createCarrier(CarrierSaveReqVO createReqVO) {
        // 1. 校验承运商编码唯一性
        validateCarrierCodeUnique(null, createReqVO.getCarrierCode());

        // 2. 插入数据库
        CarrierDO carrier = CarrierConvert.INSTANCE.convert(createReqVO);
        carrierMapper.insert(carrier);

        log.info("[承运商] 创建成功，承运商ID：{}，编码：{}", carrier.getId(), carrier.getCarrierCode());
        return carrier.getId();
    }

    @Override
    public void updateCarrier(CarrierSaveReqVO updateReqVO) {
        // 1. 校验承运商是否存在
        validateCarrierExists(updateReqVO.getId());

        // 2. 校验承运商编码唯一性
        validateCarrierCodeUnique(updateReqVO.getId(), updateReqVO.getCarrierCode());

        // 3. 更新数据库
        CarrierDO updateObj = CarrierConvert.INSTANCE.convert(updateReqVO);
        carrierMapper.updateById(updateObj);

        log.info("[承运商] 更新成功，承运商ID：{}", updateReqVO.getId());
    }

    @Override
    public void deleteCarrier(Long id) {
        // 1. 校验承运商是否存在
        validateCarrierExists(id);

        // TODO: 2. 校验是否有关联的出库单，如有则不允许删除
        // 可以添加类似于 hasOutboundByCarrierId 的校验

        // 3. 删除（逻辑删除）
        carrierMapper.deleteById(id);

        log.info("[承运商] 删除成功，承运商ID：{}", id);
    }

    @Override
    public CarrierRespVO getCarrier(Long id) {
        CarrierDO carrier = carrierMapper.selectById(id);
        return CarrierConvert.INSTANCE.convert(carrier);
    }

    @Override
    public PageResult<CarrierRespVO> getCarrierPage(CarrierPageReqVO pageReqVO) {
        // 1. 查询分页数据
        PageResult<CarrierDO> pageResult = carrierMapper.selectPage(pageReqVO);
        
        // 2. 转换为 VO
        return CarrierConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    public List<CarrierRespVO> getCarrierSimpleList() {
        // 查询所有启用状态的承运商
        List<CarrierDO> list = carrierMapper.selectList(CarrierDO::getStatus, 1);
        return CarrierConvert.INSTANCE.convertList(list);
    }

    @Override
    public Map<Long, String> getCarrierMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        List<CarrierDO> list = carrierMapper.selectList(CarrierDO::getId, ids);
        return convertMap(list, CarrierDO::getId, CarrierDO::getCarrierName);
    }

    /**
     * 校验承运商是否存在
     * 
     * @param id 承运商ID
     */
    private void validateCarrierExists(Long id) {
        if (carrierMapper.selectById(id) == null) {
            throw exception(CARRIER_NOT_EXISTS);
        }
    }

    /**
     * 校验承运商编码唯一性
     * 
     * @param id 承运商ID（修改时传入，新增时为null）
     * @param carrierCode 承运商编码
     */
    private void validateCarrierCodeUnique(Long id, String carrierCode) {
        CarrierDO carrier = carrierMapper.selectOne(CarrierDO::getCarrierCode, carrierCode);
        if (carrier == null) {
            return;
        }
        // 如果是新增，或者修改时编码与其他承运商重复，则抛出异常
        if (id == null || !id.equals(carrier.getId())) {
            throw exception(CARRIER_CODE_DUPLICATE);
        }
    }
}

