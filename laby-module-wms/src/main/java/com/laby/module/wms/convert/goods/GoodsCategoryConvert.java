package com.laby.module.wms.convert.goods;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.goods.vo.category.GoodsCategoryRespVO;
import com.laby.module.wms.controller.admin.goods.vo.category.GoodsCategorySaveReqVO;
import com.laby.module.wms.dal.dataobject.goods.GoodsCategoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 商品分类 Convert
 *
 * @author laby
 */
@Mapper
public interface GoodsCategoryConvert {

    GoodsCategoryConvert INSTANCE = Mappers.getMapper(GoodsCategoryConvert.class);

    GoodsCategoryDO convert(GoodsCategorySaveReqVO bean);

    GoodsCategoryRespVO convert(GoodsCategoryDO bean);

    List<GoodsCategoryRespVO> convertList(List<GoodsCategoryDO> list);

    PageResult<GoodsCategoryRespVO> convertPage(PageResult<GoodsCategoryDO> page);

}

