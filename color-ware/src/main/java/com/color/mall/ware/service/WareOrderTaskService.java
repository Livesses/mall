package com.color.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.color.mall.common.utils.PageUtils;
import com.color.mall.ware.entity.WareOrderTaskEntity;


import java.util.Map;

/**
 * 库存工作单
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

