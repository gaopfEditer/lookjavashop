package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mdd.common.plugin.alipay.AliPayDriver;
import com.mdd.common.plugin.alipay.request.AliPaymentRequest;
import com.mdd.front.service.IDistributionService;
import com.mdd.front.service.IOrderService;
import com.mdd.front.service.ISeckillService;
import com.mdd.front.vo.pay.PayStatusVo;
import com.mdd.front.vo.pay.PayWayListVo;
import com.mdd.common.entity.RechargeOrder;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.entity.order.Order;
import com.mdd.common.entity.order.OrderGoods;
import com.mdd.common.entity.setting.DevPayConfig;
import com.mdd.common.entity.setting.DevPayWay;
import com.mdd.common.entity.user.User;
import com.mdd.common.entity.user.UserAuth;
import com.mdd.common.enums.*;
import com.mdd.common.exception.OperateException;
import com.mdd.common.exception.PaymentException;
import com.mdd.common.mapper.goods.GoodsMapper;
import com.mdd.common.mapper.log.LogMoneyMapper;
import com.mdd.common.mapper.RechargeOrderMapper;
import com.mdd.common.mapper.log.LogOrderMapper;
import com.mdd.common.mapper.order.OrderGoodsMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.common.mapper.setting.DevPayConfigMapper;
import com.mdd.common.mapper.setting.DevPayWayMapper;
import com.mdd.common.mapper.user.UserAuthMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.plugin.notice.NoticeDriver;
import com.mdd.common.plugin.notice.vo.NoticeSmsVo;
import com.mdd.common.plugin.wechat.WxPayDriver;
import com.mdd.common.plugin.wechat.request.PaymentRequestV3;
import com.mdd.common.util.*;
import com.mdd.front.service.IPayService;
import com.mdd.front.validate.PaymentValidate;
import com.mdd.front.vo.pay.PayWayInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Service
public class PayServiceImpl implements IPayService {

    @Resource
    UserMapper userMapper;

    @Resource
    UserAuthMapper userAuthMapper;

    @Resource
    DevPayWayMapper devPayWayMapper;

    @Resource
    DevPayConfigMapper devPayConfigMapper;

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderGoodsMapper orderGoodsMapper;

    @Resource
    GoodsMapper goodsMapper;

    @Resource
    LogMoneyMapper logMoneyMapper;

    @Resource
    LogOrderMapper logOrderMapper;

    @Resource
    ISeckillService iSeckillService;

    @Resource
    IOrderService iOrderService;

    @Resource
    IDistributionService iDistributionService;

    @Resource
    DataSourceTransactionManager transactionManager ;

    @Resource
    TransactionDefinition transactionDefinition;

    /**
     * 支付方式
     *
     * @author fzr
     * @param from 场景
     * @param orderId 订单ID
     * @param terminal 终端
     * @return List<PayWayListedVo>
     */
    @Override
    public PayWayListVo payWay(String from, Integer orderId, Integer terminal, Integer userId) {
        List<DevPayWay> devPayWays = devPayWayMapper.selectList(
                new QueryWrapper<DevPayWay>()
                        .eq("scene", terminal)
                        .eq("status", 1));

        PayWayListVo vo = new PayWayListVo();
        if (from.equals("recharge")) {
            RechargeOrder rechargeOrder = rechargeOrderMapper.selectById(orderId);
            vo.setOrderAmount(rechargeOrder.getOrderAmount());
        }

        if (from.equals("order")) {
            Order order = orderMapper.selectById(orderId);
            vo.setOrderAmount(order.getNeedPayMoney());
            // 订单允许取消时间
            vo.setCancelTime(iOrderService.getCancelUnpaidOrderTime(order.getCreateTime()));
        }

        Integer walletType = PaymentEnum.WALLET_PAY.getCode();
        List<PayWayInfoVo> list = new LinkedList<>();
        for (DevPayWay way : devPayWays) {
            if (from.equals("recharge") && way.getPayConfigId().equals(walletType)) {
                continue;
            }

            DevPayConfig devPayConfig = devPayConfigMapper.selectById(way.getPayConfigId());
            PayWayInfoVo infoVo = new PayWayInfoVo();
            infoVo.setId(devPayConfig.getId());
            infoVo.setName(devPayConfig.getName());
            infoVo.setIcon(UrlUtils.toAbsoluteUrl(devPayConfig.getIcon()));
            infoVo.setIsDefault(way.getIsDefault());
            list.add(infoVo);
        }
        vo.setList(list);

        // 用户余额
        User user = userMapper.selectById(userId);
        vo.setUserMoney(user.getMoney());

        return vo;
    }

    /**
     * 订单状态
     *
     * @author fzr
     * @param from 场景
     * @param orderId 订单ID
     * @return PayStatusVo
     */
    @Override
    public PayStatusVo payStatus(String from, Integer orderId) {
        PayStatusVo vo = new PayStatusVo();
        boolean orderExist = false;

        switch (from) {
            case "recharge":
                RechargeOrder rechargeOrder = rechargeOrderMapper.selectById(orderId);
                if (StringUtils.isNotNull(rechargeOrder)) {
                    orderExist = true;
                    vo.setPayStatus(rechargeOrder.getPayStatus());
                    vo.setPayWay(PaymentEnum.getPayWayMsg(rechargeOrder.getPayWay()));
                    vo.setOrderId(rechargeOrder.getId());
                    vo.setOrderSn(rechargeOrder.getOrderSn());
                    vo.setOrderAmount(rechargeOrder.getOrderAmount());
                    vo.setPayTime(StringUtils.isNotEmpty(vo.getPayTime()) ? TimeUtils.timestampToDate(vo.getPayTime()) : "");
                }
                break;
            case "order":
                Order order = orderMapper.selectById(orderId);
                if (StringUtils.isNotNull(order)) {
                    orderExist = true;
                    vo.setPayStatus(order.getPayIs());
                    vo.setPayWay(PaymentEnum.getPayWayMsg(order.getPayWay()));
                    vo.setOrderId(order.getId());
                    vo.setOrderSn(order.getOrderSn());
                    vo.setOrderAmount(order.getNeedPayMoney());
                    vo.setPayTime(StringUtils.isNotNull(order.getPayTime()) ? TimeUtils.timestampToDate(order.getPayTime()) : "");
                }
                break;
        }

        if (!orderExist) {
            throw new OperateException("订单不存在!");
        }

        return vo;
    }

    /**
     * 发起支付
     *
     * @param params 参数
     * @param terminal 终端
     * @return Object
     */
    public Object prepay(PaymentValidate params, Integer terminal) {
        try {
            String openId = null;
            UserAuth userAuth = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                    .eq("user_id", params.getUserId())
                    .eq("terminal", terminal)
                    .last("limit 1"));

            if (StringUtils.isNotNull(userAuth)) {
                openId = userAuth.getOpenid();
            }
            // 定义支付返回结果
            Map<String, String> map = new LinkedHashMap<>();

            switch (params.getPayWay()) {
                case 1: // 余额支付
                    String attach = params.getAttach();
                    String orderSn = params.getOutTradeNo();
                    this.handlePaidNotify(attach, orderSn, null);
                    return Collections.emptyList();
                case 2: // 微信支付
                    // 应付金额为0时
                    if (params.getOrderAmount().compareTo(BigDecimal.ZERO) == 0) {
                        this.handlePaidNotify(params.getAttach(), params.getOutTradeNo(), null);
                        return Collections.emptyList();
                    }

                    PaymentRequestV3 requestV3 = new PaymentRequestV3();
                    requestV3.setTerminal(terminal);
                    requestV3.setOpenId(openId);
                    requestV3.setAttach(params.getAttach());
                    requestV3.setOutTradeNo(params.getOutTradeNo());
                    requestV3.setOrderAmount(params.getOrderAmount());
                    requestV3.setDescription(params.getDescription());
                    Object result = WxPayDriver.unifiedOrder(requestV3);
                    if (terminal == ClientEnum.H5.getCode()) {
                        Assert.notNull(params.getRedirectUrl(), "redirectUrl参数缺失");
                        String redirectUrl = RequestUtils.domain() + "/mobile" + params.getRedirectUrl();
                        String h5Url = result.toString() + "&redirect_url=" + URLEncoder.encode(redirectUrl, "UTF-8");
                        map.put("url", h5Url);
                        return map;
                    }
                    return result;
                case 3:
                    // 应付金额为0时
                    if (params.getOrderAmount().compareTo(BigDecimal.ZERO) == 0) {
                        this.handlePaidNotify(params.getAttach(), params.getOutTradeNo(), null);
                        return Collections.emptyList();
                    }
                    AliPaymentRequest requestAiliPay = new AliPaymentRequest();
                    requestAiliPay.setOutTradeNo(params.getOutTradeNo());
                    requestAiliPay.setOrderAmount(params.getOrderAmount());
                    requestAiliPay.setDescription(params.getDescription());
                    requestAiliPay.setAttach(params.getAttach());
                    requestAiliPay.setUrl(params.getRedirectUrl());
                    Object resultAliPay = AliPayDriver.unifiedOrder(requestAiliPay);
                    map.put("form", resultAliPay.toString());
                    return map;
            }
        } catch (OperateException e) {
            throw new PaymentException(e.getMsg());
        } catch (Exception e) {
            throw new PaymentException(e.getMessage());
        }

        throw new PaymentException("支付发起异常");
    }

    /**
     * 支付回调处理
     *
     * @author fzr
     * @param attach 场景码
     * @param outTradeNo 订单编号
     * @param transactionId 流水号
     */
    @Override
    public void handlePaidNotify(String attach, String outTradeNo, String transactionId) {
        // 开启事务
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            switch (attach) {
                case "order":
                    this.orderCallback(outTradeNo, transactionId);
                    break;
                case "recharge":
                    this.rechargeCallback(outTradeNo, transactionId);
                    break;
            }
            // 提交事务
            transactionManager.commit(transactionStatus);
        } catch (OperateException e) {
            // 事务回滚
            transactionManager.rollback(transactionStatus);
            throw new OperateException(e.getMsg());
        } catch (Exception e) {
            // 事务回滚
            transactionManager.rollback(transactionStatus);
            throw new OperateException(e.getMessage());
        }
    }


    /**
     * 获取商户号
     * @param payWay 支付方式
     * @return 商户号
     */
    @Override
    public Map<String, String> getMchId(Integer payWay) {
        DevPayConfig devPayConfig = devPayConfigMapper.selectOne(
                new QueryWrapper<DevPayConfig>()
                        .eq("id", payWay)
                        .last("limit 1"));
        Assert.notNull(devPayConfig, "支付方式不存在！");

        String env = YmlUtils.get("like.production");
        boolean envStatus = StringUtils.isNotNull(env) && env.equals("true");

        Map<String, String> params = MapUtils.jsonToMap(devPayConfig.getParams().toString());
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("mch_id", envStatus ? "******" : params.getOrDefault("mch_id", ""));
        return map;
    }

    /**
     * 余额充值回调
     *
     * @author fzr
     * @param outTradeNo 订单号
     * @param transactionId 流水号
     */
    private void rechargeCallback(String outTradeNo, String transactionId) {
        for (int i=0; i<=0; i++) {
            RechargeOrder rechargeOrder = rechargeOrderMapper.selectOne(
                    new QueryWrapper<RechargeOrder>()
                            .eq("order_sn", outTradeNo)
                            .last("limit 1"));

            if (StringUtils.isNull(rechargeOrder)) {
                log.error("充值订单不存在: {} : {}", outTradeNo, transactionId);
                break;
            }

            if (rechargeOrder.getPayStatus().equals(PaymentEnum.OK_PAID.getCode())) {
                log.error("充值订单已支付: {} : {}", outTradeNo, transactionId);
                break;
            }

            rechargeOrder.setPayStatus(1);
            rechargeOrder.setTransactionId(transactionId);
            rechargeOrder.setPayTime(System.currentTimeMillis() / 1000);
            rechargeOrder.setUpdateTime(System.currentTimeMillis() / 1000);
            rechargeOrderMapper.updateById(rechargeOrder);

            User user = userMapper.selectById(rechargeOrder.getUserId());
            user.setMoney(user.getMoney().add(rechargeOrder.getOrderAmount()));
            user.setUpdateTime(System.currentTimeMillis() / 1000);
            userMapper.updateById(user);

            logMoneyMapper.add(rechargeOrder.getUserId(),
                    LogMoneyEnum.UM_INC_RECHARGE.getCode(),
                    rechargeOrder.getOrderAmount(),
                    rechargeOrder.getId(),
                    rechargeOrder.getOrderSn(),
                    "用户充值余额", null);
        }
    }

    /**
     * 订单支付回调
     *
     * @author mjf
     * @param outTradeNo String
     * @param transactionId String
     */
    private void orderCallback(String outTradeNo, String transactionId) {
        Order order = orderMapper.selectOne(
                new QueryWrapper<Order>()
                        .eq("order_sn", outTradeNo)
                        .last("limit 1"));

        if (StringUtils.isNull(order)) {
            log.error("订单不存在: {} : {}", outTradeNo, transactionId);
            throw new OperateException("订单不存在");
        }

        if (order.getPayIs().equals(PaymentEnum.OK_PAID.getCode())) {
            log.error("订单已支付: {} : {}", outTradeNo, transactionId);
            throw new OperateException("订单已支付");
        }

        // 余额支付方式时扣减余额，记录日志
        if (order.getPayWay().equals(PaymentEnum.WALLET_PAY.getCode())) {
            User user = userMapper.selectById(order.getUserId());
            if (user.getMoney().compareTo(order.getNeedPayMoney()) < 0) {
                log.error("余额不足: {} : {}", outTradeNo, transactionId);
                throw new OperateException("余额不足");
            }
            user.setMoney(user.getMoney().subtract(order.getNeedPayMoney()));
            user.setUpdateTime(System.currentTimeMillis() / 1000);
            userMapper.updateById(user);

            // 余额日志
            logMoneyMapper.dec(order.getUserId(),
                    LogMoneyEnum.UM_DEC_PAY_ORDER.getCode(),
                    order.getNeedPayMoney(),
                    order.getId(),
                    order.getOrderSn(),
                    "余额支付订单", null);
        }

        // 更新订单信息
        order.setPayIs(1);
        order.setOrderStatus(OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode());
        if (order.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_PICK.getCode())) {
            order.setOrderStatus(OrderEnum.ORDER_STATUS_TAKE_DELIVER.getCode());
        }
        order.setTransactionId(transactionId);
        order.setPayMoney(order.getNeedPayMoney());
        order.setPayTime(System.currentTimeMillis() / 1000);
        order.setUpdateTime(System.currentTimeMillis() / 1000);
        orderMapper.updateById(order);

        // 更新订单商品信息
        orderGoodsMapper.update(null, new UpdateWrapper<OrderGoods>()
                .eq("order_id", order.getId())
                .setSql("pay_money = need_pay_money"));

        // 更新商品销量
        List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>()
                .eq("order_id", order.getId()));
        for (OrderGoods orderGoodsItem : orderGoodsList) {
            goodsMapper.update(null, new UpdateWrapper<Goods>()
                    .eq("id", orderGoodsItem.getGoodsId())
                    .setSql("sales_num = sales_num+" + orderGoodsItem.getGoodsNum()));
        }

        // 秒杀商品更新
        if (order.getOrderType().equals(OrderEnum.ORDER_SOURCE_SECKILL.getCode())) {
            iSeckillService.updateSeckillGoodsSales(order, orderGoodsList.get(0).getGoodsSkuId());
        }

        // 订单日志
        logOrderMapper.add(
                order.getId(),
                OrderLogEnum.TYPE_USER.getCode(),
                OrderLogEnum.CHANNEL_PAY_ORDER.getCode(),
                order.getUserId(),
                OrderLogEnum.CHANNEL_PAY_ORDER.getMsg()
        );

        if (order.getOrderType().equals(OrderEnum.ORDER_SOURCE_NORMAL.getCode())) {
            // 分销订单
            iDistributionService.addDistributionOrder(order.getId());
            // 更新分销等级
            iDistributionService.updateDistributionLevel(order.getUserId());
        }

        try {
            // 订单通知用户
            NoticeSmsVo UserParams = new NoticeSmsVo()
                    .setScene(NoticeEnum.ORDER_PAY_NOTICE.getCode())
                    .setMobile(order.getAddressMobile())
                    .setExpire(900)
                    .setParams(new String[]{
                            "nickname:" + order.getAddressContact(),
                            "order_sn:" + order.getOrderSn(),
                            "order_money:" + order.getNeedPayMoney()
                    });
            NoticeDriver.handle(UserParams);

            // 订单通知商家
            String platformMobile = ConfigUtils.get("contact", "mobile", "");
            if (StringUtils.isNotBlank(platformMobile)) {
                NoticeSmsVo params = new NoticeSmsVo()
                        .setScene(NoticeEnum.SELLER_ORDER_PAY_NOTICE.getCode())
                        .setMobile(platformMobile)
                        .setExpire(900)
                        .setParams(new String[]{
                                "nickname:" + order.getAddressContact(),
                                "order_sn:" + order.getOrderSn(),
                                "order_money:" + order.getNeedPayMoney()
                        });
                NoticeDriver.handle(params);
            }
        } catch (Exception ignored) {}
    }


}
