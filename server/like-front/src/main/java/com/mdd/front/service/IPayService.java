package com.mdd.front.service;

import com.github.binarywang.wxpay.exception.WxPayException;
import com.mdd.front.validate.PaymentValidate;
import com.mdd.front.vo.pay.PayStatusVo;
import com.mdd.front.vo.pay.PayWayListVo;

import java.util.Map;

/**
 * 支付接口服务类
 */
public interface IPayService {

    /**
     * 支付方式
     *
     * @param from 场景
     * @param orderId 订单ID
     * @param terminal 终端
     * @param userId 用户id
     * @return List<PayWayListedVo>
     */
    PayWayListVo payWay(String from, Integer orderId, Integer terminal, Integer userId);

    /**
     * 支付状态
     *
     * @author fzr
     * @param from 场景
     * @param orderId 订单ID
     * @return PayStatusVo
     */
    PayStatusVo payStatus(String from, Integer orderId);

    /**
     * 发起支付
     *
     * @param params 参数
     * @param terminal 终端
     * @return Object
     */
    Object prepay(PaymentValidate params, Integer terminal);

    /**
     * 支付回调处理
     *
     * @param attach 场景码
     * @param outTradeNo 订单编号
     * @param transactionId 流水号
     */
    void handlePaidNotify(String attach, String outTradeNo, String transactionId) throws WxPayException;


    /**
     * 获取商户号
     * @param payWay  支付方式
     * @return 商户号
     */
    Map<String, String> getMchId(Integer payWay);

}
