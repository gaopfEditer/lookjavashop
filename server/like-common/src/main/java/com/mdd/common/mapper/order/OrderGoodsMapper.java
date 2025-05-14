package com.mdd.common.mapper.order;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.order.OrderGoods;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单商品Mapper
 */
@Mapper
public interface OrderGoodsMapper extends IBaseMapper<OrderGoods> {
}
