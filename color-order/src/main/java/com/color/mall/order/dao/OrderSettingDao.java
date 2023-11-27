package com.color.mall.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.color.mall.order.entity.OrderSettingEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:56:16
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {

}
