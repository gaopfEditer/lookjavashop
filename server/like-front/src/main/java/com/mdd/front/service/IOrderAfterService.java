package com.mdd.front.service;

import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.order.OrderAfterCreateValidate;
import com.mdd.front.validate.order.OrderAfterDeliveryValidate;
import com.mdd.front.vo.order.OrderAfterCreateVo;
import com.mdd.front.vo.order.OrderAfterInfoVo;
import com.mdd.front.vo.order.OrderAfterDetailVo;
import com.mdd.front.vo.order.OrderAfterListVo;
import com.mdd.common.core.PageResult;

/**
 * 订单售后服务类
 */
public interface IOrderAfterService {

    /**
     * 售后列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param userId Integer
     * @param type String
     * @return List<OrderAfterListVo>
     */
    PageResult<OrderAfterListVo> list(PageValidate pageValidate, Integer userId, String type);


    /**
     * 申请售后
     *
     * @author mjf
     * @param userId Integer
     * @param orderAfterCreateValidate OrderAfterCreateValidate
     * @return OrderAfterCreateVo
     */
    OrderAfterCreateVo add(Integer userId, OrderAfterCreateValidate orderAfterCreateValidate);


    /**
     * 售后id
     *
     * @author mjf
     * @param id Integer
     * @return OrderAfterDetailVo
     */
    OrderAfterDetailVo detail(Integer userId, Integer id);

    /**
     * 撤销售后
     *
     * @author mjf
     * @param userId Integer
     * @param id Integer
     */
    void cancel(Integer userId, Integer id);

    /**
     * 买家退货
     *
     * @author mjf
     * @param userId Integer
     * @param deliveryValidate OrderAfterDeliveryValidate
     */
    void delivery(Integer userId, OrderAfterDeliveryValidate deliveryValidate);


    /**
     * 订单信息
     *
     * @author mjf
     * @param userId Integer
     * @param orderGoodsId Integer
     * @return OrderAfterInfoVo
     */
    OrderAfterInfoVo orderInfo(Integer userId, Integer orderGoodsId);
}
