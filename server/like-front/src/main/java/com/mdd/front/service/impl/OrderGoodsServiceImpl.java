package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.front.service.IOrderGoodsService;
import com.mdd.front.vo.order.OrderGoodsDetailVo;
import com.mdd.common.entity.order.OrderGoods;
import com.mdd.common.mapper.order.OrderGoodsMapper;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 订单商品实现类
 */
@Service
public class OrderGoodsServiceImpl implements IOrderGoodsService {


    @Resource
    OrderGoodsMapper orderGoodsMapper;

    /**
     * 获取订单商品详情
     *
     * @author mjf
     * @param id Integer
     * @return GoodsDetailVo
     */
    @Override
    public OrderGoodsDetailVo detail(Integer id) {
        OrderGoods orderGoods = orderGoodsMapper.selectOne(new QueryWrapper<OrderGoods>()
                .eq("id", id));
        OrderGoodsDetailVo vo = new OrderGoodsDetailVo();
        BeanUtils.copyProperties(orderGoods, vo);
        vo.setGoodsImage(UrlUtils.toAbsoluteUrl(orderGoods.getGoodsImage()));
        return vo;
    }

    /**
     * 获取订单商品详情
     *
     * @author mjf
     * @param id Integer
     * @return GoodsDetailVo
     */
    @Override
    public OrderGoods detailByid(Integer id) {
        OrderGoods orderGoods = orderGoodsMapper.selectOne(new QueryWrapper<OrderGoods>()
                .eq("id", id));
        return orderGoods;
    }

}
