package com.color.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.color.mall.common.utils.PageUtils;
import com.color.mall.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:36:40
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

