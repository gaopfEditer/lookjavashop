package com.mdd.admin.crontab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.admin.service.IOrderManageService;
import com.mdd.common.entity.RechargeOrder;
import com.mdd.common.entity.delivery.ExpressCompany;
import com.mdd.common.entity.order.Order;
import com.mdd.common.entity.order.OrderDelivery;
import com.mdd.common.entity.user.UserAuth;
import com.mdd.common.enums.ClientEnum;
import com.mdd.common.enums.OrderEnum;
import com.mdd.common.enums.PaymentEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.RechargeOrderMapper;
import com.mdd.common.mapper.delivery.ExpressCompanyMapper;
import com.mdd.common.mapper.order.OrderDeliveryMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.common.mapper.user.UserAuthMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.util.AccessTokenUtil;
import com.mdd.common.util.ListUtils;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;

@Component("wechatMiniExpressSendSyncJob")
public class WechatMiniExpressSendSyncJob {
    @Resource
    OrderMapper orderMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    UserAuthMapper userAuthMapper;
    @Resource
    IOrderManageService iOrderManageService;
    @Resource
    OrderDeliveryMapper orderDeliveryMapper;
    @Resource
    ExpressCompanyMapper expressCompanyMapper;
    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    //提交到微信
    public void handle() {
        // 快递方式
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getDeliveryType, OrderEnum.DELIVERY_TYPE_EXPRESS.getCode())
                .eq(Order::getExpressIs, 1)
                .eq(Order::getPayIs, 1)
                .eq(Order::getPayWay, PaymentEnum.WX_PAY.getCode())
                .eq(Order::getWechatMiniExpressSync, 0)
                .in(Order::getOrderStatus, Arrays.asList(
                    OrderEnum.ORDER_STATUS_TAKE_DELIVER.getCode(),
                    OrderEnum.ORDER_STATUS_COMPLETED.getCode()
                ))
                .orderByDesc(Order::getId)
                .last("limit 60");
        List<Order> orderList = orderMapper.selectList(queryWrapper);

        // 自提
        LambdaQueryWrapper<Order> pickupWrapper = new LambdaQueryWrapper<>();
        pickupWrapper.eq(Order::getDeliveryType, OrderEnum.DELIVERY_TYPE_PICK.getCode())
                .eq(Order::getPayIs, 1)
                .eq(Order::getPayWay, PaymentEnum.WX_PAY.getCode())
                .eq(Order::getWechatMiniExpressSync, 0)
                .in(Order::getOrderStatus, Arrays.asList(
                    OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode(),
                    OrderEnum.ORDER_STATUS_PICKUP_DELIVER.getCode(),
                    OrderEnum.ORDER_STATUS_COMPLETED.getCode()
                ))
                .orderByDesc(Order::getId)
                .last("limit 60");
        List<Order> orderListPickup = orderMapper.selectList(pickupWrapper);

        //充值订单
        LambdaQueryWrapper<RechargeOrder> rechargeWrapper = new LambdaQueryWrapper<>();
        rechargeWrapper.eq(RechargeOrder::getPayStatus, 1)
                .eq(RechargeOrder::getWechatMiniExpressSync, 0)
                .orderByDesc(RechargeOrder::getId)
                .last("limit 60");
        List<RechargeOrder> rechargeOrderList = rechargeOrderMapper.selectList(rechargeWrapper);

        if (orderList.size() > 0) { //快递配送存在没上传的记录
            for (Order order : orderList) {
                syncOrder(order);
            }
        }
        if (orderListPickup.size() > 0) { //到店自提存在没上传的记录
            for (Order order : orderListPickup) {
                syncOrder(order);
            }
        }
        if (rechargeOrderList.size() > 0) {
            for (RechargeOrder rechargeOrder : rechargeOrderList) {
                syncRechargeOrder(rechargeOrder);
            }
        }
    }

    /**
     * @param array $order
     * @return bool
     * @notes 订单录入
     * @author damonyuan
     */
    public void syncOrder(Order order) {
        try {
            UserAuth userAuth = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                    .eq("user_id", order.getUserId())
                    .eq("terminal", ClientEnum.MNP.getCode())
                    .last("limit 1")
            );
            if (StringUtils.isNull(userAuth)) {
                order.setWechatMiniExpressSync(2);
                order.setWechatMiniExpressSyncTime(System.currentTimeMillis() / 1000);
                orderMapper.updateById(order);
                return;
            }
            List<String> orderGoodsNameList = iOrderManageService.getNamesByOid(order.getId());
            if (orderGoodsNameList.size() > 0) {
                String goodsName = ListUtils.listToStringByStr(orderGoodsNameList, " ");

                JSONObject shippingItem = new JSONObject();
                shippingItem.put("item_desc", StringUtils.substring(goodsName, 0, 120));
                JSONObject data = new JSONObject();
                data.put("order_key", new JSONObject() {{
                    put("order_number_type", 2);
                    put("transaction_id", order.getTransactionId());
                }});
                data.put("delivery_mode", 1);
                data.put("upload_time", TimeUtils.nowTime());
                data.put("payer", new JSONObject() {{
                    put("openid", userAuth.getOpenid());
                }});
                OrderDelivery orderDelivery = orderDeliveryMapper.selectOne(new QueryWrapper<OrderDelivery>().eq("order_id", order.getId()));
                if (StringUtils.isNull(orderDelivery)) {
                    data.put("logistics_type", 3);
                } else {
                    ExpressCompany expressCompany = expressCompanyMapper.selectOne(new QueryWrapper<ExpressCompany>().eq("id", orderDelivery.getExpressId()));
                    if (StringUtils.isNotEmpty(orderDelivery.getInvoiceNo()) && StringUtils.isNotEmpty(orderDelivery.getMobile()) && StringUtils.isNotEmpty(expressCompany.getCodeKd())) {
                        shippingItem.put("tracking_no", orderDelivery.getInvoiceNo());
                        shippingItem.put("express_company", expressCompany.getCodeKd());
                        shippingItem.put("contact", new JSONObject() {{
                            put("receiver_contact", StringUtils.desensitizePhoneNumber(orderDelivery.getMobile()));
                        }});
                    } else {
                        data.put("logistics_type", 3);
                    }
                }

                data.put("shipping_list", new JSONArray() {{
                    add(shippingItem);
                }});
                if (order.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_EXPRESS.getCode())) {
                    data.put("logistics_type", StringUtils.isNull(data.getInteger("logistics_type")) ? 1 : data.getInteger("logistics_type"));
                }
                if (order.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_PICK.getCode())) {
                    data.put("logistics_type", 4);
                }
                if (order.getDeliveryType().equals(OrderEnum.DELIVERY_VIRTUAL.getCode())) {
                    data.put("logistics_type", 3);
                }
                String access_token = AccessTokenUtil.getAccessToken();
                if (access_token != null) {
                    String url = "https://api.weixin.qq.com/wxa/sec/order/upload_shipping_info?access_token=" + access_token;


                    String result = sendPostRequest(url, data.toJSONString());
                    JSONObject ret = JSONObject.parseObject(result);
                    if (StringUtils.isNull(ret.getInteger("errcode")) || ret.getInteger("errcode") != 0) {
                        if (StringUtils.isNotNull(ret.getInteger("errcode")) && ret.getInteger("errcode").equals(40001)) {
                            return;
                        }
                        order.setWechatMiniExpressSync(2);
                        order.setWechatMiniExpressSyncTime(System.currentTimeMillis() / 1000);
                        orderMapper.updateById(order);
                        return;
                    } else {
                        order.setWechatMiniExpressSync(1);
                        order.setWechatMiniExpressSyncTime(System.currentTimeMillis() / 1000);
                        orderMapper.updateById(order);
                        return;
                    }
                }
            } else {
                order.setWechatMiniExpressSync(2);
                order.setWechatMiniExpressSyncTime(System.currentTimeMillis() / 1000);
                orderMapper.updateById(order);
                return;
            }
        } catch (OperateException e) {
            throw new OperateException(e.getMessage());
        }
    }

    public static String sendPostRequest(String url, String jsonData) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8"); // 使用 UTF-8 编码
            osw.write(jsonData);
            osw.flush();
            osw.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param array $recharge
     * @return bool
     * @notes 充值录入
     * @author damonuan
     */
    public void syncRechargeOrder(RechargeOrder rechargeOrder) {
        try {
            UserAuth userAuth = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                    .eq("user_id", rechargeOrder.getUserId())
                    .eq("terminal", ClientEnum.MNP.getCode())
                    .last("limit 1")
            );
            if (StringUtils.isNull(userAuth)) {
                rechargeOrder.setWechatMiniExpressSync(2);
                rechargeOrder.setWechatMiniExpressSyncTime(System.currentTimeMillis() / 1000);
                rechargeOrderMapper.updateById(rechargeOrder);
                return;
            }
            JSONObject shippingItem = new JSONObject();
            shippingItem.put("item_desc", "余额充值");
            JSONObject data = new JSONObject();
            data.put("order_key", new JSONObject() {{
                put("order_number_type", 2);
                put("transaction_id", rechargeOrder.getTransactionId());
            }});
            data.put("delivery_mode", 1);
            data.put("upload_time", TimeUtils.nowTime());
            data.put("payer", new JSONObject() {{
                put("openid", userAuth.getOpenid());
            }});
            data.put("logistics_type", 3);
            data.put("shipping_list", new JSONArray() {{
                add(shippingItem);
            }});

            String access_token = AccessTokenUtil.getAccessToken();
            if (access_token != null) {
                String url = "https://api.weixin.qq.com/wxa/sec/order/upload_shipping_info?access_token=" + access_token;
                String result = sendPostRequest(url, data.toJSONString());
                JSONObject ret = JSONObject.parseObject(result);
                if (StringUtils.isNull(ret.getInteger("errcode")) || ret.getInteger("errcode") != 0) {
                    if (StringUtils.isNotNull(ret.getInteger("errcode")) && ret.getInteger("errcode").equals(40001)) {
                        return;
                    }
                    rechargeOrder.setWechatMiniExpressSync(2);
                    rechargeOrder.setWechatMiniExpressSyncTime(System.currentTimeMillis() / 1000);
                    rechargeOrderMapper.updateById(rechargeOrder);
                    return;
                } else {
                    rechargeOrder.setWechatMiniExpressSync(1);
                    rechargeOrder.setWechatMiniExpressSyncTime(System.currentTimeMillis() / 1000);
                    rechargeOrderMapper.updateById(rechargeOrder);
                    return;
                }
            }
        } catch (OperateException e) {
            throw new OperateException(e.getMessage());
        }
    }
}
