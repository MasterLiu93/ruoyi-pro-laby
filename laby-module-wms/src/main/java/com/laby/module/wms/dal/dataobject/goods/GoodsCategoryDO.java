package com.laby.module.wms.dal.dataobject.goods;

import com.laby.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 商品分类 DO
 * 对应数据库表：wms_goods_category
 * 
 * 功能说明：
 * - 存储商品分类的基本信息，支持树形结构
 * - 最多支持3级分类（一级分类→二级分类→三级分类）
 * - 通过 parentId 关联父分类，实现层级关系
 * - 支持多租户隔离
 * 
 * @author laby
 */
@TableName("wms_goods_category")
@KeySequence("wms_goods_category_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsCategoryDO extends TenantBaseDO {

    /**
     * 分类ID（主键）
     */
    @TableId
    private Long id;
    
    /**
     * 分类编码，唯一标识
     * 示例：CAT-001（一级分类）, CAT-001-01（二级分类）
     * 命名规则：一级分类用3位数字，二级分类在一级基础上追加2位
     */
    private String categoryCode;
    
    /**
     * 分类名称
     * 示例：电子产品、手机通讯、智能手机
     */
    private String categoryName;
    
    /**
     * 父分类ID
     * 0 表示顶级分类（一级分类）
     * 非0 表示有父分类（二级或三级分类）
     * 关联：wms_goods_category.id
     */
    private Long parentId;
    
    /**
     * 层级
     * 1-一级分类，2-二级分类，3-三级分类
     * 说明：用于限制分类的层级深度，便于前端展示
     */
    private Integer level;
    
    /**
     * 排序
     * 数字越小越靠前
     * 默认值：0
     * 说明：同级分类之间的排序，用于前端展示顺序
     */
    private Integer sort;

    /**
     * 状态
     * 0-禁用，1-启用
     * 
     * 枚举 {@link com.laby.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;

}

