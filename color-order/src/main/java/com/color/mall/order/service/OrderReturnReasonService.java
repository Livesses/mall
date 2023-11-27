package com.color.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.color.mall.common.utils.PageUtils;
import com.color.mall.order.entity.OrderReturnReasonEntity;


import java.util.Map;

/**
 * 退货原因
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:56:16
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

