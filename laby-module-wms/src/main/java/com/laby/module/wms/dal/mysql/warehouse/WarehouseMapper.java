package com.laby.module.wms.dal.mysql.warehouse;

import com.laby.framework.common.pojo.PageResult;
import com.laby.framework.mybatis.core.mapper.BaseMapperX;
import com.laby.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.laby.module.wms.controller.admin.warehouse.vo.warehouse.WarehousePageReqVO;
import com.laby.module.wms.dal.dataobject.warehouse.WarehouseDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 仓库 Mapper
 * 数据访问层，使用 MyBatis Plus
 * 
 * 功能说明：
 * - 继承 BaseMapperX，提供基础的 CRUD 方法
 * - 使用 LambdaQueryWrapperX 构建动态查询条件
 * - 只返回 DO 对象，不返回 VO（关联查询在 Service 层处理）
 * - 支持多租户自动隔离（由 TenantLineHandler 拦截器实现）
 * - 所有查询方法都使用 default 修饰，提供默认实现
 *
 * @author laby
 */
@Mapper
public interface WarehouseMapper extends BaseMapperX<WarehouseDO> {

    /**
     * 分页查询仓库列表
     *
     * 支持的查询条件：
     * - 仓库编码（模糊查询）
     * - 仓库名称（模糊查询）
     * - 仓库类型（精确查询）
     * - 状态（精确查询）
     * - 创建时间范围（区间查询）
     *
     * 排序规则：
     * - 按 ID 降序
     *
     * @param reqVO 分页查询参数
     * @return 仓库分页数据
     */
    default PageResult<WarehouseDO> selectPage(WarehousePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WarehouseDO>()
                .likeIfPresent(WarehouseDO::getWarehouseCode, reqVO.getWarehouseCode())
                .likeIfPresent(WarehouseDO::getWarehouseName, reqVO.getWarehouseName())
                .eqIfPresent(WarehouseDO::getWarehouseType, reqVO.getWarehouseType())
                .eqIfPresent(WarehouseDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(WarehouseDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(WarehouseDO::getId));
    }

    /**
     * 根据仓库编码查询仓库
     *
     * 说明：
     * - 用于校验仓库编码唯一性
     * - 如果不存在返回 null
     *
     * @param warehouseCode 仓库编码
     * @return 仓库信息，不存在返回 null
     */
    default WarehouseDO selectByCode(String warehouseCode) {
        return selectOne(WarehouseDO::getWarehouseCode, warehouseCode);
    }

    /**
     * 根据状态查询仓库列表
     *
     * 说明：
     * - 主要用于查询所有启用的仓库
     * - 用于前端下拉框选择
     *
     * @param status 状态（0-禁用，1-启用）
     * @return 指定状态的仓库列表
     */
    default List<WarehouseDO> selectListByStatus(Integer status) {
        return selectList(WarehouseDO::getStatus, status);
    }

}
