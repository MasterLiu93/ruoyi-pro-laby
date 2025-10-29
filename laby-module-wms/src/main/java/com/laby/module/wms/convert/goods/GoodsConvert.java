package com.laby.module.wms.convert.goods;

import com.laby.framework.common.pojo.PageResult;
import com.laby.module.wms.controller.admin.goods.vo.goods.GoodsRespVO;
import com.laby.module.wms.controller.admin.goods.vo.goods.GoodsSaveReqVO;
import com.laby.module.wms.dal.dataobject.goods.GoodsDO;
import com.laby.module.wms.dal.dataobject.goods.GoodsCategoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品信息 Convert
 *
 * @author laby
 */
@Mapper
public interface GoodsConvert {

    GoodsConvert INSTANCE = Mappers.getMapper(GoodsConvert.class);

    GoodsDO convert(GoodsSaveReqVO bean);

    GoodsRespVO convert(GoodsDO bean);

    List<GoodsRespVO> convertList(List<GoodsDO> list);

    PageResult<GoodsRespVO> convertPage(PageResult<GoodsDO> page);

    default PageResult<GoodsRespVO> convertPage(PageResult<GoodsDO> page, Map<Long, GoodsCategoryDO> categoryMap) {
        PageResult<GoodsRespVO> result = convertPage(page);
        if (result != null && result.getList() != null) {
            result.getList().forEach(vo -> {
                if (vo.getCategoryId() != null && categoryMap.containsKey(vo.getCategoryId())) {
                    vo.setCategoryName(categoryMap.get(vo.getCategoryId()).getCategoryName());
                }
            });
        }
        return result;
    }

}

