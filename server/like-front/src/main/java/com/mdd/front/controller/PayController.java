package com.mdd.front.controller;

import com.alipay.api.AlipayApiException;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.mdd.common.plugin.alipay.AliPayDriver;
import com.mdd.common.util.MapUtils;
import com.mdd.front.vo.pay.PayStatusVo;
import com.mdd.front.vo.pay.PayWayListVo;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.entity.RechargeOrder;
import com.mdd.common.entity.order.Order;
import com.mdd.common.enums.ClientEnum;
import com.mdd.common.enums.PaymentEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.RechargeOrderMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.common.plugin.wechat.WxPayDriver;
import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.front.service.IPayService;
import com.mdd.front.validate.PaymentValidate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

@RestController
@RequestMapping("/api/pay")
@Api(tags = "支付管理")
public class PayController {

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    OrderMapper orderMapper;

    @Resource
    IPayService iPayService;

    @GetMapping("/payWay")
    @ApiOperation("支付方式")
    public AjaxResult<PayWayListVo> payWay(@Validated @NotNull(message = "from参数丢失") @RequestParam String from,
                                           @Validated @NotNull(message = "orderId参数丢失") @RequestParam Integer orderId) {
        Integer terminal = LikeFrontThreadLocal.getTerminal();
        Integer userId = LikeFrontThreadLocal.getUserId();

        PayWayListVo list = iPayService.payWay(from, orderId, terminal, userId);
        return AjaxResult.success(list);
    }

    @GetMapping("/payStatus")
    @ApiOperation(("支付状态"))
    public AjaxResult<PayStatusVo> payStatus(@Validated @NotNull(message = "from参数丢失") @RequestParam String from,
                                             @Validated @NotNull(message = "orderId参数丢失") @RequestParam Integer orderId) {
        PayStatusVo vo = iPayService.payStatus(from, orderId);
        return AjaxResult.success(vo);
    }

    @GetMapping("/getMchId")
    @ApiOperation("获取商户号")
    public AjaxResult getMchId(@Validated @NotNull(message = "payWay参数丢失") @RequestParam Integer payWay){
        Map<String, String> map = iPayService.getMchId(payWay);
        return AjaxResult.success(map);
    }

    @PostMapping("/prepay")
    @ApiOperation("发起支付")
    public AjaxResult<Object> prepay(@Validated @RequestBody PaymentValidate requestObj) {
        // 接收参数
        String scene     = requestObj.getScene();
        Integer payWay   = requestObj.getPayWay();
        Integer orderId  = requestObj.getOrderId();
        Integer terminal = LikeFrontThreadLocal.getTerminal();

        // 订单处理
        int payStatus = 0;
        switch (scene) {
            case "recharge":
                RechargeOrder rechargeOrder = rechargeOrderMapper.selectById(orderId);

                Assert.notNull(rechargeOrder, "订单不存在");
                Assert.isTrue(!payWay.equals(PaymentEnum.WALLET_PAY.getCode()), "支付类型不被支持");

                requestObj.setUserId(rechargeOrder.getUserId());
                requestObj.setOutTradeNo(rechargeOrder.getOrderSn());
                requestObj.setOrderAmount(rechargeOrder.getOrderAmount());
                requestObj.setDescription("余额充值");
                requestObj.setAttach("recharge");
                payStatus = rechargeOrder.getPayStatus();

                rechargeOrder.setPayWay(payWay);
                rechargeOrderMapper.updateById(rechargeOrder);
                break;
            case "order":
                Order order = orderMapper.selectById(orderId);
                Assert.notNull(order, "订单不存在");
                requestObj.setUserId(order.getUserId());
                requestObj.setOutTradeNo(order.getOrderSn());
                requestObj.setOrderAmount(order.getNeedPayMoney());
                requestObj.setDescription("订单下单");
                requestObj.setAttach("order");
                payStatus = order.getPayIs();
                order.setPayWay(payWay);
                orderMapper.updateById(order);
                break;
        }

        // 订单校验
        if (payStatus != 0) {
            throw new OperateException("订单已支付");
        }

        // 发起支付
        Object result = iPayService.prepay(requestObj, terminal);
        return AjaxResult.success(result);
    }

    @NotLogin
    @PostMapping("/notifyMnp")
    @ApiOperation("微信支付回调")
    public AjaxResult<Object> notifyMnp(@RequestBody String jsonData, HttpServletRequest request) throws WxPayException {
        // 构建签名
        SignatureHeader signatureHeader = new SignatureHeader();
        signatureHeader.setSignature(request.getHeader("wechatpay-signature"));
        signatureHeader.setNonce(request.getHeader("wechatpay-nonce"));
        signatureHeader.setSerial(request.getHeader("wechatpay-serial"));
        signatureHeader.setTimeStamp(request.getHeader("wechatpay-timestamp"));

        // 解密数据
        WxPayService wxPayService = WxPayDriver.handler(ClientEnum.MNP.getCode());
        WxPayNotifyV3Result.DecryptNotifyResult notifyResult = wxPayService.parseOrderNotifyV3Result(jsonData, signatureHeader).getResult();

        // 取出数据
        String transactionId = notifyResult.getTransactionId();
        String outTradeNo = notifyResult.getOutTradeNo();
        String attach =  notifyResult.getAttach();

        // 处理回调
        iPayService.handlePaidNotify(attach, outTradeNo, transactionId);
        return AjaxResult.success();
    }

    @NotLogin
    @PostMapping("/notifyAliPay")
    @ApiOperation("支付宝支付回调")
    public AjaxResult<Object> notifyAliPay(HttpServletRequest request) throws AlipayApiException, WxPayException, UnsupportedEncodingException {
        Map<String, String> param = AliPayDriver.getNotifyParam(request.getParameterMap());
        try{
            if (!AliPayDriver.checkSign(param)){
                return AjaxResult.failed("签名验证错误！");
            }
        }catch (Exception e){
            return AjaxResult.failed("支付宝异步回调通知异常！");
        }
        if ("TRADE_SUCCESS".equals(param.get("trade_status"))){
            String outTradeNo = param.get("out_trade_no");
            String transactionId = param.get("trade_no");
            String attach = URLDecoder.decode(param.get("passback_params"), "UTF-8");
            // 处理回调
            iPayService.handlePaidNotify(attach, outTradeNo, transactionId);
            return AjaxResult.success();
        }else {
            return AjaxResult.failed("支付失败！");
        }
    }

}
