package com.mdd.common.plugin.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.entity.setting.DevPayConfig;
import com.mdd.common.mapper.setting.DevPayConfigMapper;
import com.mdd.common.plugin.alipay.request.AliPayRefundRequest;
import com.mdd.common.plugin.alipay.request.AliPaymentRequest;
import com.mdd.common.plugin.wechat.request.RefundRequestV3;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.MapUtils;
import com.mdd.common.util.RequestUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class AliPayDriver {

    private static DevPayConfigMapper devPayConfigMapper;

    /**
     * 注入支付配置依赖
     */
    @Resource
    public void setDevPayConfigMapper(DevPayConfigMapper devPayConfigMapper) {
        AliPayDriver.devPayConfigMapper = devPayConfigMapper;
    }

    public static Object unifiedOrder(AliPaymentRequest params) throws AlipayApiException, UnsupportedEncodingException {

        // 订单参数
        String outTradeNo      = params.getOutTradeNo();
        BigDecimal orderAmount = params.getOrderAmount();
        String description     = params.getDescription();

        // 失效时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Long expireTime = System.currentTimeMillis() + (30 * 60 * 1000);
        String timeExpire = format.format(expireTime) + "+08:00";

        AlipayClient alipayClient = new DefaultAlipayClient(resetConfig());
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(outTradeNo);
        model.setTotalAmount(String.valueOf(orderAmount));
        model.setSubject(description);
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        model.setPassbackParams(URLEncoder.encode(params.getAttach(), "UTF-8"));
        // 商户ID为空
        //model.setSellerId("");
        model.setTimeExpire(timeExpire);
        request.setBizModel(model);
        request.setNotifyUrl(RequestUtils.uri() + "/api/pay/notifyAliPay");
        request.setReturnUrl(params.getUrl());
        return alipayClient.pageExecute(request).getBody();
    }

    public static AlipayTradeRefundResponse refund(AliPayRefundRequest req) throws AlipayApiException {
        AlipayConfig alipayConfig = resetConfig();
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(req.getOutTradeNo());
        model.setTradeNo(req.getTransactionId());
        model.setOutRequestNo(req.getOutRefundNo());
        model.setRefundAmount(String.valueOf(req.getRefundAmount()));
        request.setBizModel(model);
        return alipayClient.execute(request);
    }

    public static boolean checkSign(Map<String, String> param) throws AlipayApiException {
        AlipayConfig alipayConfig = resetConfig();
        return AlipaySignature.rsaCheckV1(param, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType());
    }

    public static Map<String, String> getNotifyParam(Map<String, String[]> requestParams){
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<>();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        return params;
    }

    private static AlipayConfig resetConfig() {
        DevPayConfig config = devPayConfigMapper.selectOne(
                new QueryWrapper<DevPayConfig>()
                        .eq("way", 3)
                        .last("limit 1"));

        Map<String, String> params = MapUtils.jsonToMap(config.getParams().toString());
        String appId = params.get("app_id");
        String alipayPublicKey  = params.get("alipay_public_key");
        String privateKey  = params.getOrDefault("private_key", "");

        // 初始化支付宝支付配置
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setAppId(appId);
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }

}
