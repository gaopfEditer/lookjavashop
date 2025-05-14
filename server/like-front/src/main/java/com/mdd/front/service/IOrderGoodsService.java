package com.mdd.front.service;


import com.mdd.common.entity.order.OrderGoods;
import com.mdd.front.vo.order.OrderGoodsDetailVo;

/**
 * 订单商品接口
 */
public interface IOrderGoodsService {


    /**
     * 订单商品详情
     *
     * @author mjf
     * @param id Integer
     * @return OrderGoodsDetailVo
     */
    OrderGoodsDetailVo detail(Integer id);

    /**
     * 根据id返回OrderGoods
     * @param id
     * @return
     */
    OrderGoods detailByid(Integer id);

}
