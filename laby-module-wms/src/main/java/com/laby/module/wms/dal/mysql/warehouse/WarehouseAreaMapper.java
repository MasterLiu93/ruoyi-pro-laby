package com.laby.module.wms.dal.mysql.warehouse;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.warehouse.vo.area.WarehouseAreaPageReqVO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseAreaDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 库区 Mapper
 * 数据访问层，使用 MyBatis Plus
 * 
 * 功能说明：
 * - 继承 BaseMapperX，提供基础的 CRUD 方法
 * - 使用 LambdaQueryWrapperX 构建动态查询条件
 * - 只返回 DO 对象，不返回 VO（关联查询在 Service 层处理）
 * - 支持按仓库ID查询库区列表（用于前端联动下拉框）
 * - 支持统计指定仓库下的库区数量
 * - 支持多租户自动隔离（由 TenantLineHandler 拦截器实现）
 *
 * @author laby
 */
@Mapper
public interface WarehouseAreaMapper extends BaseMapperX<WarehouseAreaDO> {

    /**
     * 分页查询库区列表
     *
     * 支持的查询条件：
     * - 仓库ID（精确查询）
     * - 库区编码（模糊查询）
     * - 库区名称（模糊查询）
     * - 库区类型（精确查询）
     * - 状态（精确查询）
     * - 创建时间范围（区间查询）
     *
     * 排序规则：
     * - 按 ID 降序
     *
     * @param reqVO 分页查询参数
     * @return 库区分页数据
     */
    default PageResult<WarehouseAreaDO> selectPage(WarehouseAreaPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WarehouseAreaDO>()
                .eqIfPresent(WarehouseAreaDO::getWarehouseId, reqVO.getWarehouseId())
                .likeIfPresent(WarehouseAreaDO::getAreaCode, reqVO.getAreaCode())
                .likeIfPresent(WarehouseAreaDO::getAreaName, reqVO.getAreaName())
                .eqIfPresent(WarehouseAreaDO::getAreaType, reqVO.getAreaType())
                .eqIfPresent(WarehouseAreaDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(WarehouseAreaDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(WarehouseAreaDO::getId));
    }

    /**
     * 根据仓库ID和库区编码查询库区
     *
     * 说明：
     * - 用于校验库区编码在同一仓库内的唯一性
     * - 如果不存在返回 null
     *
     * @param warehouseId 仓库ID
     * @param areaCode 库区编码
     * @return 库区信息，不存在返回 null
     */
    default WarehouseAreaDO selectByCode(Long warehouseId, String areaCode) {
        return selectOne(WarehouseAreaDO::getWarehouseId, warehouseId,
                WarehouseAreaDO::getAreaCode, areaCode);
    }

    /**
     * 查询指定仓库的所有库区
     *
     * 说明：
     * - 主要用于前端"仓库-库区"联动下拉框
     * - 返回该仓库下的所有库区（不限制状态）
     *
     * @param warehouseId 仓库ID
     * @return 该仓库下的所有库区列表
     */
    default List<WarehouseAreaDO> selectListByWarehouseId(Long warehouseId) {
        return selectList(WarehouseAreaDO::getWarehouseId, warehouseId);
    }

    /**
     * 统计指定仓库下的库区数量
     *
     * 说明：
     * - 用于删除仓库前的关联数据校验
     * - 如果数量大于0，不允许删除仓库
     *
     * @param warehouseId 仓库ID
     * @return 该仓库下的库区数量
     */
    default Long selectCountByWarehouseId(Long warehouseId) {
        return selectCount(WarehouseAreaDO::getWarehouseId, warehouseId);
    }

}
