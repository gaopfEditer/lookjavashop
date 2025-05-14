package com.mdd.front.service;

import com.alibaba.fastjson2.JSONArray;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.order.OrderSettleValidate;
import com.mdd.front.validate.selffetchorder.SelffetchOrderPickCodeVerifyValidate;
import com.mdd.front.validate.selffetchorder.SelffetchOrderSearchValidate;
import com.mdd.front.vo.order.*;
import com.mdd.common.core.PageResult;
import com.mdd.front.vo.selffetchOrder.SelffetchOrderDetailVo;
import com.mdd.front.vo.selffetchOrder.SelffetchOrderPickuplVo;

/**
 * 订单接口服务类
 */
public interface IOrderService {

    /**
     * 订单结算
     *
     * @author mjf
     * @param settleValidate SettleValidate
     * @return SettlementOrderResultVo
     */
    OrderSettleResultVo settlementOrder(OrderSettleValidate settleValidate);

    /**
     * 提交订单
     *
     * @author mjf
     * @param settleValidate OrderSettleValidate
     * @return SubmitOrderResultVo
     */
    OrderSubmitResultVo submitOrder(OrderSettleValidate settleValidate);


    /**
     * 订单列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param status Integer
     * @return List<OrderListVo>
     */
    PageResult<OrderListVo> list(PageValidate pageValidate, Integer status);

    /**
     * 订单详情
     *
     * @author mjf
     * @param id Integer
     * @return OrderDetailVo
     */
    OrderDetailVo detail(Integer id);

    /**
     * 确认收货
     *
     * @author mjf
     * @param id Integer
     */
    void confirm(Integer id);

    /**
     * 删除订单
     *
     * @author mjf
     * @param id Integer
     */
    void del(Integer id);

    /**
     * 取消订单
     *
     * @author mjf
     * @param id Integer
     */
    void cancel(Integer id);

    /**
     * 查询物流
     *
     * @author mjf
     * @param userId Integer
     * @param id Integer
     * @return OrderLogisticsVo
     */
    OrderLogisticsVo logistics(Integer userId, Integer id);

    /**
     * 订单可取消时间
     *
     * @author mjf
     * @param orderCreateTime Long
     * @return Long
     */
    Long getCancelUnpaidOrderTime(Long orderCreateTime);

    /**
     * 自提订单列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param status Integer
     * @return List<OrderListVo>
     */
    PageResult<OrderListVo> selffetchlist(PageValidate pageValidate, SelffetchOrderSearchValidate selffetchOrderSearchValidate);

    /**
     * 自提订单详情
     *
     * @author mjf
     * @param id Integer
     * @return OrderDetailVo
     */
    SelffetchOrderDetailVo selffetchDetail(Integer id);

    /**
     * 自提确认收货
     * @param id
     * @param items
     */
    void selffetchVerify(Integer id, JSONArray items);

    /**
     * 自提核销号
     */
    SelffetchOrderPickuplVo pickCodeVerify(SelffetchOrderPickCodeVerifyValidate selffetchOrderPickCodeVerifyValidate);
}
