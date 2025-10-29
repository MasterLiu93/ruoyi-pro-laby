package com.laby.module.wms.convert.picking;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.picking.vo.PickingTaskRespVO;
import com.laby.module.wms.dal.dataobject.picking.PickingTaskDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 拣货任务 Convert
 * 
 * 用于 DO、VO 之间的转换
 *
 * @author laby
 */
@Mapper
public interface PickingTaskConvert {

    PickingTaskConvert INSTANCE = Mappers.getMapper(PickingTaskConvert.class);

    /**
     * DO 转 RespVO
     *
     * @param bean DO对象
     * @return RespVO对象
     */
    @Mapping(target = "warehouseName", ignore = true)
    PickingTaskRespVO convert(PickingTaskDO bean);

    /**
     * DO列表 转 RespVO列表
     *
     * @param list DO列表
     * @return RespVO列表
     */
    List<PickingTaskRespVO> convertList(List<PickingTaskDO> list);

    /**
     * DO分页结果 转 RespVO分页结果
     *
     * @param page DO分页结果
     * @return RespVO分页结果
     */
    PageResult<PickingTaskRespVO> convertPage(PageResult<PickingTaskDO> page);
}

