package com.color.mall.product.service;

import com.color.mall.product.entity.AttrAttrgroupRelationEntity;
import com.color.mall.product.vo.AttrGroupRelationVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.color.mall.common.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 21:08:49
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBatch(List<AttrGroupRelationVo> vos);

}

