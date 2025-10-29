package com.laby.module.wms.dal.mysql.warehouse;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.warehouse.vo.location.WarehouseLocationPageReqVO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseLocationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 库位 Mapper
 * 数据访问层，使用 MyBatis Plus
 * 
 * 功能说明：
 * - 继承 BaseMapperX，提供基础的 CRUD 方法
 * - 使用 LambdaQueryWrapperX 构建动态查询条件
 * - 只返回 DO 对象，不返回 VO（关联查询在 Service 层处理）
 * - 支持按库区ID查询库位列表（用于前端三级联动下拉框）
 * - 支持统计指定库区下的库位数量
 * - 支持多租户自动隔离（由 TenantLineHandler 拦截器实现）
 *
 * @author laby
 */
@Mapper
public interface WarehouseLocationMapper extends BaseMapperX<WarehouseLocationDO> {

    /**
     * 分页查询库位列表
     *
     * 支持的查询条件：
     * - 仓库ID（精确查询）
     * - 库区ID（精确查询）
     * - 库位编码（模糊查询）
     * - 库位类型（精确查询）
     * - 状态（精确查询：空闲、占用、锁定、禁用）
     * - 创建时间范围（区间查询）
     *
     * 排序规则：
     * - 按 ID 降序
     *
     * @param reqVO 分页查询参数
     * @return 库位分页数据
     */
    default PageResult<WarehouseLocationDO> selectPage(WarehouseLocationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WarehouseLocationDO>()
                .eqIfPresent(WarehouseLocationDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(WarehouseLocationDO::getAreaId, reqVO.getAreaId())
                .likeIfPresent(WarehouseLocationDO::getLocationCode, reqVO.getLocationCode())
                .eqIfPresent(WarehouseLocationDO::getLocationType, reqVO.getLocationType())
                .eqIfPresent(WarehouseLocationDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(WarehouseLocationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(WarehouseLocationDO::getId));
    }

    /**
     * 根据仓库ID、库区ID和库位编码查询库位
     *
     * 说明：
     * - 用于校验库位编码的全局唯一性
     * - 如果不存在返回 null
     *
     * @param warehouseId 仓库ID
     * @param areaId 库区ID
     * @param locationCode 库位编码
     * @return 库位信息，不存在返回 null
     */
    default WarehouseLocationDO selectByCode(Long warehouseId, Long areaId, String locationCode) {
        return selectOne(WarehouseLocationDO::getWarehouseId, warehouseId,
                WarehouseLocationDO::getAreaId, areaId,
                WarehouseLocationDO::getLocationCode, locationCode);
    }

    /**
     * 查询指定库区的所有库位
     *
     * 说明：
     * - 主要用于前端"仓库-库区-库位"三级联动下拉框
     * - 返回该库区下的所有库位（不限制状态）
     *
     * @param areaId 库区ID
     * @return 该库区下的所有库位列表
     */
    default List<WarehouseLocationDO> selectListByAreaId(Long areaId) {
        return selectList(WarehouseLocationDO::getAreaId, areaId);
    }

    /**
     * 统计指定库区下的库位数量
     *
     * 说明：
     * - 用于删除库区前的关联数据校验
     * - 如果数量大于0，不允许删除库区
     *
     * @param areaId 库区ID
     * @return 该库区下的库位数量
     */
    default Long selectCountByAreaId(Long areaId) {
        return selectCount(WarehouseLocationDO::getAreaId, areaId);
    }

    /**
     * 统计指定仓库下的库位数量
     *
     * 说明：
     * - 用于仓库统计信息展示
     * - 可用于判断仓库是否可以删除
     *
     * @param warehouseId 仓库ID
     * @return 该仓库下的库位数量
     */
    default Long selectCountByWarehouseId(Long warehouseId) {
        return selectCount(WarehouseLocationDO::getWarehouseId, warehouseId);
    }

}
