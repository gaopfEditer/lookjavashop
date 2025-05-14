package com.mdd.admin.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.LikeAdminThreadLocal;
import com.mdd.admin.service.IOrderManageService;
import com.mdd.admin.service.ISelffetchShopService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.order.OrderSearchValidate;
import com.mdd.admin.validate.order.OrderSendDeliveryValidate;
import com.mdd.admin.vo.order.*;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.RefundLog;
import com.mdd.common.entity.RefundRecord;
import com.mdd.common.entity.Verification;
import com.mdd.common.entity.delivery.ExpressCompany;
import com.mdd.common.entity.distribution.DistributionOrder;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.entity.goods.GoodsSku;
import com.mdd.common.entity.order.*;
import com.mdd.common.entity.log.LogOrder;
import com.mdd.common.entity.system.SystemAuthAdmin;
import com.mdd.common.entity.user.User;
import com.mdd.common.enums.*;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.RefundLogMapper;
import com.mdd.common.mapper.RefundRecordMapper;
import com.mdd.common.mapper.VerificationMapper;
import com.mdd.common.mapper.delivery.ExpressCompanyMapper;
import com.mdd.common.mapper.distribution.DistributionOrderMapper;
import com.mdd.common.mapper.goods.GoodsMapper;
import com.mdd.common.mapper.goods.GoodsSkuMapper;
import com.mdd.common.mapper.log.LogMoneyMapper;
import com.mdd.common.mapper.log.LogOrderAfterMapper;
import com.mdd.common.mapper.order.*;
import com.mdd.common.mapper.log.LogOrderMapper;
import com.mdd.common.mapper.system.SystemAuthAdminMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.plugin.alipay.AliPayDriver;
import com.mdd.common.plugin.alipay.request.AliPayRefundRequest;
import com.mdd.common.plugin.delivery.DeliveryDriver;
import com.mdd.common.plugin.delivery.vo.KdQueryTrackParam;
import com.mdd.common.plugin.delivery.vo.KdTrackResultVo;
import com.mdd.common.plugin.notice.NoticeDriver;
import com.mdd.common.plugin.notice.vo.NoticeSmsVo;
import com.mdd.common.plugin.wechat.WxPayDriver;
import com.mdd.common.plugin.wechat.request.RefundRequestV3;
import com.mdd.common.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 订单服务实现类
 */
@Service
public class OrderManageServiceImpl implements IOrderManageService {

    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderGoodsMapper orderGoodsMapper;

    @Resource
    OrderDeliveryMapper orderDeliveryMapper;

    @Resource
    ExpressCompanyMapper expressCompanyMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    LogOrderMapper logOrderMapper;

    @Resource
    LogOrderAfterMapper logOrderAfterMapper;

    @Resource
    LogMoneyMapper logMoneyMapper;

    @Resource
    RefundRecordMapper refundRecordMapper;

    @Resource
    RefundLogMapper refundLogMapper;

    @Resource
    OrderAfterMapper orderAfterMapper;

    @Resource
    OrderAfterGoodsMapper orderAfterGoodsMapper;

    @Resource
    SystemAuthAdminMapper systemAuthAdminMapper;

    @Resource
    DistributionOrderMapper distributionOrderMapper;

    @Resource
    DataSourceTransactionManager transactionManager;
    @Resource
    GoodsSkuMapper goodsSkuMapper;
    @Resource
    GoodsMapper goodsMapper;

    @Resource
    TransactionDefinition transactionDefinition;
    @Resource
    VerificationMapper verificationMapper;
    @Resource
    ISelffetchShopService iSelffetchShopService;

    @Override
    public PageResult<OrderManageListedVo> list(PageValidate pageValidate, OrderSearchValidate orderSearchValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        MPJQueryWrapper<Order> orderMPJQueryWrapper = new MPJQueryWrapper<>();
        Integer searchType = orderSearchValidate.getSearchType();
        String searchKeyword = orderSearchValidate.getSearchKeyword();
        Integer userSearchType = orderSearchValidate.getUserSearchType();
        String searchUserKeyword = orderSearchValidate.getSearchUserKeyword();
        String searchTimeType = orderSearchValidate.getSearchTimeType();
        Integer startTime = orderSearchValidate.getStartTime();
        Integer endTime = orderSearchValidate.getEndTime();

        // 订单搜索
        if(null != searchType && StringUtils.isNotEmpty(searchKeyword)){
            switch (searchType){
                case 1:
                    orderMPJQueryWrapper.like("t.order_sn","%"+searchKeyword+"%");
                    break;
                case 2:
                    orderMPJQueryWrapper.like("OG.goods_code","%"+searchKeyword+"%");
                    break;
                case 3:
                    orderMPJQueryWrapper.like("OG.goods_name","%"+searchKeyword+"%");
                    break;
            }
        }

        // 买家信息
        if(null != userSearchType && StringUtils.isNotEmpty(searchUserKeyword)){
            switch (userSearchType){
                case 1:
                    orderMPJQueryWrapper.like("U.sn","%"+searchUserKeyword+"%");
                    break;
                case 2:
                    orderMPJQueryWrapper.like("U.nickname","%"+searchUserKeyword+"%");
                    break;
                case 3:
                    orderMPJQueryWrapper.like("t.address_contact","%"+searchUserKeyword+"%");
                    break;
                case 4:
                    orderMPJQueryWrapper.like("t.address_mobile","%"+searchUserKeyword+"%");
                    break;
            }
        }

        // 时间搜索
        if(null != searchTimeType){
            String column = "t.create_time";
            if(searchTimeType.equals("pay")){
                column = "t.pay_time";
            }

            if(null != startTime){
                orderMPJQueryWrapper.ge(column, startTime);
            }

            if(null != endTime){
                orderMPJQueryWrapper.le(column, endTime);
            }
        }

        // 其它搜索
        orderMapper.setSearch(orderMPJQueryWrapper, orderSearchValidate, new String[]{
                "=:orderSource@t.order_source:int",
                "=:payStatus@t.pay_is:int",
                "=:payWay@t.pay_way:int",
        });

        if (StringUtils.isNotNull(orderSearchValidate.getPickupCode())) {
            orderMPJQueryWrapper.like("pickup_code", orderSearchValidate.getPickupCode());
        }

        if (StringUtils.isNotNull(orderSearchValidate.getDeliveryType())) {
            orderMPJQueryWrapper.eq("delivery_type", orderSearchValidate.getDeliveryType());
        }

        if (StringUtils.isNotNull(orderSearchValidate.getSelffetchShopId())) {
            orderMPJQueryWrapper.eq("selffetch_shop_id", orderSearchValidate.getSelffetchShopId());
        }

        if (StringUtils.isNotNull(orderSearchValidate.getOrderStatus())) {
            orderMPJQueryWrapper.eq("order_status", orderSearchValidate.getOrderStatus());
        }
        orderMPJQueryWrapper
                .leftJoin("?_order_goods OG ON t.id=OG.order_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_user U ON t.user_id=U.id".replace("?_", GlobalConfig.tablePrefix))
                .eq("t.is_delete", 0)
                .orderByDesc("t.id");

        Map<String, Object> statistics = __statistics(orderMPJQueryWrapper.clone());

        if (StringUtils.isNotNull(orderSearchValidate.getVerificationStatus())) {
            orderMPJQueryWrapper.eq("t.verification_status", orderSearchValidate.getVerificationStatus());
        }

        if (StringUtils.isNotNull(orderSearchValidate.getType()) && orderSearchValidate.getType() > 0) {
            orderMPJQueryWrapper.eq("t.order_status", orderSearchValidate.getType());
        }

        orderMPJQueryWrapper
                .select("DISTINCT t.id,t.user_id,t.order_sn,t.pay_is,t.pay_money,t.order_status,t.express_is,t.cancel_time," +
                        "t.address_contact,t.address_mobile,t.address_content,U.sn as user_sn,U.avatar,U.nickname, t.verification_status, t.selffetch_shop_id, t.verification_time, t.delivery_type, t.order_type");

        IPage<OrderInfoListVo> iPage = orderMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                OrderInfoListVo.class,
                orderMPJQueryWrapper);

        List<OrderManageListedVo> orderList = new ArrayList<>();
        for (OrderInfoListVo record : iPage.getRecords()) {
            OrderManageListedVo orderManageListedVo = new OrderManageListedVo();
            BeanUtils.copyProperties(record, orderManageListedVo);
            orderManageListedVo.setPayIsMsg(PaymentEnum.getPayStatusMsg(record.getPayIs()));
            orderManageListedVo.setOrderStatusMsg(OrderEnum.getOrderStatusMsg(record.getOrderStatus()));
            orderManageListedVo.setPayMoney(record.getPayIs().equals(1) ? record.getPayMoney().toString() : "-");
            orderManageListedVo.setSelffetchShopName(iSelffetchShopService.getNameById(record.getSelffetchShopId()));

            orderManageListedVo.setCancelBtn(this.__cancelBtn(record.getOrderStatus()));
            orderManageListedVo.setConfirmBtn(this.__confirmBtn(record.getOrderStatus()));
            orderManageListedVo.setDeleteBtn(this.__deleteBtn(record.getOrderStatus()));
            orderManageListedVo.setDeliverBtn(this.__deliverBtn(record.getOrderStatus()));
            orderManageListedVo.setLogisticsBtn(this.__logisticsBtn(record.getOrderStatus(), record.getExpressIs()));
            orderManageListedVo.setRefundBtn(this.__refundBtn(record.getOrderStatus(), record.getExpressIs()));
            orderManageListedVo.setAvatar(UrlUtils.toAbsoluteUrl(record.getAvatar()));

            List<OrderGoods> orderLists = orderGoodsMapper.selectList(
                    new QueryWrapper<OrderGoods>()
                        .eq("order_id", record.getId())
                        .select("id,goods_id,goods_name,goods_code,goods_image,goods_sku_id,goods_sku_value,goods_num,goods_price,need_pay_money"));

            List<OrderGoodsListVo> orderGoodsListVos = new ArrayList<>();
            for (OrderGoods list : orderLists) {
                list.setGoodsImage(UrlUtils.toAbsoluteUrl(list.getGoodsImage()));
                OrderGoodsListVo orderGoodsListVo = new OrderGoodsListVo();
                BeanUtils.copyProperties(list,orderGoodsListVo);
                orderGoodsListVos.add(orderGoodsListVo);
            }

            orderManageListedVo.setOrderGoodsList(orderGoodsListVos);
            orderManageListedVo.setVerificationTimeStr(record.getVerificationTime().equals(0L) ? "-" : TimeUtils.timestampToDate(record.getVerificationTime()));
            orderManageListedVo.setVerificationStatusStr(OrderEnum.getVerificationStatusMsg(record.getVerificationStatus()));
            orderManageListedVo.setDeliveryType(record.getDeliveryType());
            orderManageListedVo.setOrderType(record.getOrderType());
            orderManageListedVo.setOrderTypeStr(OrderEnum.getOrderTypeMsg(record.getOrderType()));
            orderList.add(orderManageListedVo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), orderList, statistics);
    }

    /**
     * 订单详情
     *
     * @author cjh
     * @param id 订单ID
     * @return OrderBcDetailVo
     */
    @Override
    public OrderManageDetailVo detail(Integer id){
        Order order = orderMapper.selectOne(
                new QueryWrapper<Order>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(order, "订单不存在!");

        // 订单用户
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("id", order.getUserId())
                .last("limit 1"));

        // 订单商品
        List<OrderGoods> orderGoodsListVos = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>().eq("order_id", order.getId()));
        List<OrderGoodsListVo> orderGoodsListVoList = new ArrayList<>();
        for (OrderGoods orderGoodsList : orderGoodsListVos) {
            OrderGoodsListVo orderGoodsListVo = new OrderGoodsListVo();
            BeanUtils.copyProperties(orderGoodsList,orderGoodsListVo);
            orderGoodsListVo.setAfterMsg(OrderAfterEnum.afterSaleMsg(orderGoodsList.getAfterSale()));
            orderGoodsListVo.setGoodsImage(UrlUtils.toAbsoluteUrl(orderGoodsList.getGoodsImage()));
            orderGoodsListVoList.add(orderGoodsListVo);
        }

        // 订单详情
        OrderManageDetailVo orderManageDetailVo = new OrderManageDetailVo();
        BeanUtils.copyProperties(order, orderManageDetailVo);
        orderManageDetailVo.setNickname(user.getNickname());
        orderManageDetailVo.setOrderGoodsList(orderGoodsListVoList);
        orderManageDetailVo.setOrderSourceMsg(ClientEnum.getMsgByCode(order.getOrderSource()));
        orderManageDetailVo.setPayWayMsg(PaymentEnum.getPayWayMsg(order.getPayWay()));
        orderManageDetailVo.setExpressIsMsg(DeliverEnum.getDeliverIsMsg(order.getExpressIs()));
        orderManageDetailVo.setDeliveryTypeMsg(DeliverEnum.getDeliverTypeMsg(order.getDeliveryType()));
        orderManageDetailVo.setOrderStatusMsg(OrderEnum.getOrderStatusMsg(order.getOrderStatus()));
        orderManageDetailVo.setPayIsMsg(PaymentEnum.getPayStatusMsg(order.getPayIs()));
        orderManageDetailVo.setPayTime(order.getPayTime() > 0 ? TimeUtils.timestampToDate(order.getPayTime()) : "-");
        orderManageDetailVo.setCancelTime(order.getCancelTime() > 0 ? TimeUtils.timestampToDate(order.getCancelTime()) : "-");
        orderManageDetailVo.setConfirmTime(order.getConfirmTime() > 0 ? TimeUtils.timestampToDate(order.getConfirmTime()) : "-");
        orderManageDetailVo.setExpressTime(order.getExpressTime() > 0 ? TimeUtils.timestampToDate(order.getExpressTime()) : "-");
        orderManageDetailVo.setCreateTime(TimeUtils.timestampToDate(order.getCreateTime()));
        orderManageDetailVo.setVerificationTimeStr(order.getVerificationTime().equals(0L) ? "-" : TimeUtils.timestampToDate(order.getVerificationTime()));
        orderManageDetailVo.setPickupCode(order.getPickupCode());
        orderManageDetailVo.setDeleteBtn(this.__deleteBtn(order.getOrderStatus()));
        orderManageDetailVo.setDeliverBtn(this.__deliverBtn(order.getOrderStatus()));
        orderManageDetailVo.setLogisticsBtn(this.__logisticsBtn(order.getOrderStatus(), order.getExpressIs()));
        orderManageDetailVo.setRefundBtn(this.__refundBtn(order.getOrderStatus(), order.getExpressIs()));
        orderManageDetailVo.setCancelBtn(this.__cancelBtn(order.getOrderStatus()));
        orderManageDetailVo.setConfirmBtn(this.__confirmBtn(order.getOrderStatus()));
        orderManageDetailVo.setSelffetchShopName(iSelffetchShopService.getNameById(order.getSelffetchShopId()));
        orderManageDetailVo.setSelffetchShopAddress(iSelffetchShopService.getAddressStrById(order.getSelffetchShopId()));
        orderManageDetailVo.setSelffetchShopMobile(iSelffetchShopService.getMobileById(order.getSelffetchShopId()));
        orderManageDetailVo.setVerificationByStr(geVerificationByStr(order.getVerificationBy(), order.getVerificationByType()));
        if(order.getExpressId() > 0){
            ExpressCompany expressCompany = expressCompanyMapper.selectOne(new QueryWrapper<ExpressCompany>()
                    .eq("id", order.getExpressId())
                    .eq("is_delete", 0)
                    .last("limit 1"));
            orderManageDetailVo.setExpressCompanyName(expressCompany.getName());
            orderManageDetailVo.setExpressConfirmTime(order.getConfirmTime() <= 0 ? "-" : TimeUtils.timestampToDate(order.getConfirmTime()));
        }

        List<LogOrder> orderLogList  = logOrderMapper.selectList(new QueryWrapper<LogOrder>().eq("order_id", order.getId()));
        List<OrderLogListVo> orderLogListVoList = new ArrayList<>();
        for (LogOrder orderLog : orderLogList) {
            OrderLogListVo orderLogListVo = new OrderLogListVo();
            BeanUtils.copyProperties(orderLog,orderLogListVo);
            switch (orderLog.getType()){
                case 1:
                    orderLogListVo.setOperatorName("系统");
                    break;
                case 2:
                    SystemAuthAdmin systemAuthAdmin = systemAuthAdminMapper.selectOne(new QueryWrapper<SystemAuthAdmin>()
                            .eq("id", orderLog.getOperatorId()).last("limit 1"));
                    orderLogListVo.setOperatorName(systemAuthAdmin.getNickname());
                    break;
                case 3:
                    User operatorUser = userMapper.selectOne(new QueryWrapper<User>()
                            .eq("id", orderLog.getOperatorId()).last("limit 1"));
                    orderLogListVo.setOperatorName(operatorUser.getNickname());
                    break;
            }
            orderLogListVo.setCreateTime(TimeUtils.timestampToDate(orderLog.getCreateTime()));
            orderLogListVoList.add(orderLogListVo);
        }

        orderManageDetailVo.setOrderLogList(orderLogListVoList);
        return orderManageDetailVo;
    }

    /**
     * 订单取消
     *
     * @author fzr
     * @param id 订单ID
     * @param adminId 管理员ID
     */
    @Override
    public void cancel(Integer id, Integer adminId) {
        Order order = orderMapper.selectOne(
                new QueryWrapper<Order>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(order, "订单不存在!");

        if (!order.getOrderStatus().equals(OrderEnum.ORDER_STATUS_WAIT_PAY.getCode()) &&
            !order.getOrderStatus().equals(OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode()) &&
            !order.getOrderStatus().equals(OrderEnum.ORDER_STATUS_TAKE_DELIVER.getCode())) {
            throw new OperateException("订单不允许取消");
        }

        OrderAfter orderAfterM = orderAfterMapper.selectOne(new QueryWrapper<OrderAfter>()
                .eq("order_id", id)
                .in("after_status", Arrays.asList(1, 2))
                .eq("is_delete", 0)
                .last("limit 1"));

        if (StringUtils.isNotNull(orderAfterM)) {
            throw new OperateException("正在售后的订单不能取消");
        }

        if (order.getOrderStatus().equals(OrderEnum.ORDER_STATUS_WAIT_PAY.getCode())) {
            // 未支付取消
            order.setOrderStatus(OrderEnum.ORDER_STATUS_CANCEL.getCode());
            order.setCancelTime(System.currentTimeMillis() / 1000);
            orderMapper.updateById(order);
        } else {
            // 已支付取消
            TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
            try {
                // 退款金额 (已发货不退运费)
                BigDecimal refundMoney = order.getNeedPayMoney();
                if (order.getExpressIs().equals(1)) {
                    refundMoney = order.getNeedPayMoney().subtract(order.getExpressMoney());
                }

                // 整单售后
                if (order.getPayIs().equals(PaymentEnum.OK_PAID.getCode())) {
                    OrderAfter orderAfter = null;
                    RefundRecord refundRecord = null;
                    RefundLog refundLog = null;
                    try {
                        // 写入售后订单
                        String afterSn = orderAfterMapper.randMakeOrderSn("after_sn");
                        orderAfter = new OrderAfter();
                        orderAfter.setAfterSn(afterSn);
                        orderAfter.setUserId(order.getUserId());
                        orderAfter.setOrderId(order.getId());
                        orderAfter.setRefundWay(1);
                        orderAfter.setAfterType(OrderAfterEnum.AFTER_TYPE_ORDER.getCode());
                        orderAfter.setRefundType(OrderAfterEnum.METHOD_ONLY_REFUND.getCode());
                        orderAfter.setRefundReason(LogOrderAfterEnum.BUYER_CANCEL_ORDER.getMsg());
                        orderAfter.setRefundMoney(refundMoney);
                        orderAfter.setRefundRemark("系统发起整单退款");
                        orderAfter.setAfterStatus(OrderAfterEnum.AFTER_STATUS_ING.getCode());
                        orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_ING_SELLER_REFUND_ING.getCode());
                        orderAfter.setCreateTime(System.currentTimeMillis() / 1000);
                        orderAfter.setUpdateTime(System.currentTimeMillis() / 1000);
                        orderAfterMapper.insert(orderAfter);

                        // 写入售后商品
                        List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>().eq("order_id", id));
                        for (OrderGoods item : orderGoodsList) {
                            // 退款金额 (已发货不退运费)
                            BigDecimal goodsRefundMoney = item.getNeedPayMoney();
                            if (order.getExpressIs().equals(1)) {
                                goodsRefundMoney = item.getNeedPayMoney().subtract(item.getExpressMoney());
                            }

                            OrderAfterGoods afterGoods = new OrderAfterGoods();
                            afterGoods.setOrderId(item.getOrderId());
                            afterGoods.setOrderAfterId(orderAfter.getId());
                            afterGoods.setOrderGoodsId(item.getId());
                            afterGoods.setGoodsId(item.getGoodsId());
                            afterGoods.setGoodsSkuId(item.getGoodsSkuId());
                            afterGoods.setGoodsNum(item.getGoodsNum());
                            afterGoods.setGoodsPrice(item.getGoodsPrice());
                            afterGoods.setRefundMoney(goodsRefundMoney);
                            afterGoods.setCreateTime(System.currentTimeMillis() / 1000);
                            afterGoods.setUpdateTime(System.currentTimeMillis() / 1000);
                            orderAfterGoodsMapper.insert(afterGoods);
                        }

                        // 生成售后日志
                        Integer roleSeller = LogOrderAfterEnum.TYPE_ADMIN.getCode();
                        logOrderAfterMapper.add(roleSeller, adminId, id, LogOrderAfterEnum.SELLER_CANCEL_ORDER.getMsg());

                        // 生成退款记录
                        String refundSn = refundRecordMapper.randMakeOrderSn("sn");
                        refundRecord = new RefundRecord();
                        refundRecord.setSn(refundSn);
                        refundRecord.setOrderSn(order.getOrderSn());
                        refundRecord.setOrderId(order.getId());
                        refundRecord.setUserId(order.getUserId());
                        refundRecord.setRefundType(RefundEnum.TYPE_ADMIN.getCode());
                        refundRecord.setOrderType(RefundEnum.getOrderType(RefundEnum.ORDER_TYPE_ORDER.getCode()));
                        refundRecord.setOrderAmount(order.getPayMoney());
                        refundRecord.setRefundAmount(orderAfter.getRefundMoney());
                        refundRecord.setTransactionId(order.getTransactionId());
                        refundRecord.setRefundWay(order.getPayWay());
                        refundRecord.setCreateTime(System.currentTimeMillis() / 1000);
                        refundRecord.setUpdateTime(System.currentTimeMillis() / 1000);
                        refundRecordMapper.insert(refundRecord);

                        // 生成退款日志
                        refundLog = new RefundLog();
                        refundLog.setSn(refundLogMapper.randMakeOrderSn("sn"));
                        refundLog.setRecordId(refundRecord.getId());
                        refundLog.setUserId(order.getUserId());
                        refundLog.setHandleId(adminId);
                        refundLog.setOrderAmount(order.getPayMoney());
                        refundLog.setRefundAmount(orderAfter.getRefundMoney());
                        refundLog.setRefundStatus(RefundEnum.REFUND_ING.getCode());
                        refundLog.setCreateTime(System.currentTimeMillis() / 1000);
                        refundLog.setUpdateTime(System.currentTimeMillis() / 1000);
                        refundLogMapper.insert(refundLog);

                        // 发起退款请求
                        switch (order.getPayWay()) {
                            case 1: // 余额
                                User user = userMapper.selectById(order.getUserId());
                                user.setMoney(user.getMoney().add(orderAfter.getRefundMoney()));
                                user.setUpdateTime(System.currentTimeMillis() / 1000);
                                userMapper.updateById(user);

                                Integer logMoneyType = LogMoneyEnum.BNW_INC_AFTER_SALE.getCode();
                                String logMoneyRemark = "售后退还余额";
                                logMoneyMapper.add(
                                        user.getId(),
                                        logMoneyType,
                                        orderAfter.getRefundMoney(),
                                        order.getId(),
                                        order.getOrderSn(),
                                        logMoneyRemark,
                                        null);

                                break;
                            case 2: // 微信
                                RefundRequestV3 requestV3 = new RefundRequestV3();
                                requestV3.setTransactionId(order.getTransactionId());
                                requestV3.setOutTradeNo(order.getOrderSn());
                                requestV3.setOutRefundNo(refundSn);
                                requestV3.setTotalAmount(AmountUtil.yuan2Fen(order.getPayMoney().toString()));
                                requestV3.setRefundAmount(AmountUtil.yuan2Fen(orderAfter.getRefundMoney().toString()));
                                WxPayDriver.refund(requestV3);
                                break;
                            case 3:
                                AliPayRefundRequest request = new AliPayRefundRequest();
                                request.setTransactionId(order.getTransactionId());
                                request.setOutTradeNo(order.getOrderSn());
                                request.setOutRefundNo(refundSn);
                                request.setTotalAmount(order.getPayMoney());
                                request.setRefundAmount(orderAfter.getRefundMoney());
                                AliPayDriver.refund(request);
                                break;
                        }

                        // 退款记录更新
                        refundRecord.setRefundStatus(RefundEnum.REFUND_SUCCESS.getCode());
                        refundRecord.setUpdateTime(System.currentTimeMillis() / 1000);
                        refundRecordMapper.updateById(refundRecord);

                        // 退款日志更新
                        refundLog.setRefundStatus(RefundEnum.REFUND_SUCCESS.getCode());
                        refundLog.setUpdateTime(System.currentTimeMillis() / 1000);
                        refundLogMapper.updateById(refundLog);

                        // 更新售后状态
                        List<OrderAfterGoods> orderAfterGoods = orderAfterGoodsMapper.selectList(new QueryWrapper<OrderAfterGoods>().eq("order_after_id", orderAfter.getId()));
                        for (OrderAfterGoods oag : orderAfterGoods) {
                            OrderGoods orderGoods = new OrderGoods();
                            orderGoods.setAfterSale(3);
                            orderGoods.setUpdateTime(System.currentTimeMillis() / 1000);
                            orderGoodsMapper.update(orderGoods, new QueryWrapper<OrderGoods>().eq("id", oag.getOrderGoodsId()));
                        }

                        // 变更成功状态
                        orderAfter.setRefundSn(refundSn);
                        orderAfter.setAfterStatus(OrderAfterEnum.AFTER_STATUS_SUCCESS.getCode());
                        orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_SUCCESS.getCode());
                        orderAfter.setUpdateTime(System.currentTimeMillis() / 1000);
                        orderAfterMapper.updateById(orderAfter);
                    } catch (Exception e) {
                        if (StringUtils.isNotNull(orderAfter)) {
                            orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_ING_SELLER_REFUND_ING.getCode());
                            orderAfter.setUpdateTime(System.currentTimeMillis() / 1000);
                            orderAfterMapper.updateById(orderAfter);
                        }

                        if (StringUtils.isNotNull(refundRecord)) {
                            refundRecord.setRefundStatus(RefundEnum.REFUND_ERROR.getCode());
                            refundRecord.setUpdateTime(System.currentTimeMillis() / (1000));
                            refundRecordMapper.updateById(refundRecord);
                        }

                        if (StringUtils.isNotNull(refundLog)) {
                            refundLog.setRefundMsg(e.getMessage());
                            refundLog.setRefundStatus(RefundEnum.REFUND_ERROR.getCode());
                            refundLog.setUpdateTime(System.currentTimeMillis() / 1000);
                            refundLogMapper.updateById(refundLog);
                        }

                        throw new Exception(e.getMessage());
                    }
                }

                // 订单状态
                order.setOrderStatus(OrderEnum.ORDER_STATUS_CANCEL.getCode());
                order.setCancelTime(System.currentTimeMillis() / 1000);
                orderMapper.updateById(order);

                // 订单日志
                Integer channel = OrderLogEnum.CHANNEL_ADMIN_CANCEL_ORDER.getCode();
                logOrderMapper.add(
                        order.getId(),
                        OrderLogEnum.TYPE_SYSTEM.getCode(),
                        channel,
                        adminId,
                        OrderLogEnum.getValue(channel));

                // 相关分销订单更新为失效
                DistributionOrder distributionOrder = new DistributionOrder();
                distributionOrder.setStatus(DistributionEnum.ORDER_EXPIRED.getCode());
                distributionOrder.setUpdateTime(System.currentTimeMillis() / 1000);
                distributionOrderMapper.update(distributionOrder, new QueryWrapper<DistributionOrder>()
                        .eq("order_id", order.getId()));

                //如果是自提的需要回滚库存
                int returnInventory = Integer.parseInt(ConfigUtils.get("trade", "returnInventory", "0"));
                if (returnInventory == 1) {
                    List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>()
                            .eq("order_id", order.getId()));

                    for (OrderGoods orderGoods : orderGoodsList) {
                        // 退回商品规格库存
                        goodsSkuMapper.update(null, new UpdateWrapper<GoodsSku>()
                                .eq("id", orderGoods.getGoodsSkuId())
                                .setSql("stock = stock + " + orderGoods.getGoodsNum()));
                        // 退回商品总库存
                        goodsMapper.update(null, new UpdateWrapper<Goods>()
                                .eq("id", orderGoods.getGoodsId())
                                .setSql("total_stock = total_stock+" + orderGoods.getGoodsNum()));
                    }
                }


                transactionManager.commit(transactionStatus);
            } catch (Exception e) {
                assert transactionManager != null;
                transactionManager.rollback(transactionStatus);
                throw new OperateException(e.getMessage());
            }
        }
    }

    /**
     * 订单备注
     *
     * @author fzr
     * @param orderId 订单ID
     * @param remarks 备注信息
     */
    @Override
    public void remarks(Integer orderId, String remarks) {
        Order order = orderMapper.selectOne(
                new QueryWrapper<Order>()
                        .eq("id", orderId)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(order, "订单不存在!");


        order.setShopRemark(remarks);
        order.setUpdateTime(System.currentTimeMillis() / 1000);
        orderMapper.updateById(order);
    }

    /**
     * 订单发货
     *
     * @author fzr
     * @param adminId 管理员ID
     * @param sendDeliveryValidate 发货参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendDelivery(Integer adminId, OrderSendDeliveryValidate sendDeliveryValidate) {
        Order order = orderMapper.selectById(sendDeliveryValidate.getOrderId());
        Assert.notNull(order, "订单不存在了!");

        Integer expressId = 0;
        String expressName = "";
        String invoiceNo = "";
        if (sendDeliveryValidate.getSendType().equals(1)) {
            ExpressCompany expressCompany = expressCompanyMapper.selectOne(
                    new QueryWrapper<ExpressCompany>()
                            .eq("id", sendDeliveryValidate.getExpressId())
                            .eq("is_delete", 0)
                            .last("limit 1"));

            Assert.notNull(expressCompany, "快递公司不存在!");
            expressId = expressCompany.getId();
            expressName = expressCompany.getName();
            invoiceNo = sendDeliveryValidate.getInvoiceNo();
        }

        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setOrderId(order.getId());
        orderDelivery.setUserId(order.getUserId());
        orderDelivery.setAdminId(adminId);
        orderDelivery.setOrderSn(order.getOrderSn());
        orderDelivery.setContact(order.getAddressContact());
        orderDelivery.setMobile(order.getAddressMobile());
        orderDelivery.setProvince(0);
        orderDelivery.setCity(0);
        orderDelivery.setDistrict(0);
        orderDelivery.setInvoiceNo(invoiceNo);
        orderDelivery.setExpressId(expressId);
        orderDelivery.setExpressName(expressName);

        orderDelivery.setExpressStatus(DeliverEnum.SHIPPED.getCode());
        orderDelivery.setSendType(sendDeliveryValidate.getSendType());
        orderDelivery.setRemark(sendDeliveryValidate.getRemark());
        orderDelivery.setCreateTime(System.currentTimeMillis() / 1000);
        orderDelivery.setUpdateTime(System.currentTimeMillis() / 1000);
        orderDeliveryMapper.insert(orderDelivery);

        order.setOrderStatus(OrderEnum.ORDER_STATUS_TAKE_DELIVER.getCode());
        order.setExpressIs(DeliverEnum.SHIPPED.getCode());
        order.setExpressNo(sendDeliveryValidate.getInvoiceNo());
        order.setExpressId(sendDeliveryValidate.getExpressId());
        order.setExpressTime(System.currentTimeMillis() / 1000);
        order.setUpdateTime(System.currentTimeMillis() / 1000);
        orderMapper.updateById(order);

        // 订单日志
        Integer channel = OrderLogEnum.CHANNEL_SHOP_DELIVERY_ORDER.getCode();
        logOrderMapper.add(
                order.getId(),
                OrderLogEnum.TYPE_SYSTEM.getCode(),
                channel,
                adminId,
                OrderLogEnum.getValue(channel));
        
        // 发货通知
        try {
            NoticeSmsVo UserParams = new NoticeSmsVo()
                    .setScene(NoticeEnum.ORDER_SHIP_NOTICE.getCode())
                    .setMobile(order.getAddressMobile())
                    .setExpire(900)
                    .setParams(new String[]{
                            "nickname:" + order.getAddressContact(),
                            "order_sn:" + order.getOrderSn()
                    });
            NoticeDriver.handle(UserParams);
        } catch (Exception ignored) {}
    }

    /**
     * 确认收货
     *
     * @author fzr
     * @param orderId 订单ID
     * @param adminId 管理员ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void takeDelivery(Integer orderId, Integer adminId) {
        Order order = orderMapper.selectById(orderId);
        Assert.notNull(order, "订单不存在了!");

        // 订单更新
        order.setOrderStatus(OrderEnum.ORDER_STATUS_COMPLETED.getCode());
        order.setAfterDeadline(getAfterSaleDeadline());
        order.setConfirmTime(System.currentTimeMillis() / 1000);
        order.setUpdateTime(System.currentTimeMillis() / 1000);
        orderMapper.updateById(order);

        // 订单日志
        Integer channel = OrderLogEnum.CHANNEL_SHOP_CONFIRM_ORDER.getCode();
        logOrderMapper.add(
                order.getId(),
                OrderLogEnum.TYPE_SYSTEM.getCode(),
                channel,
                adminId,
                OrderLogEnum.getValue(channel));
    }

    /**
     * 查看物流
     *
     * @author fzr
     * @param orderId 订单ID
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> logistics(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        Assert.notNull(order, "订单不存在!");

        ExpressCompany expressCompany = expressCompanyMapper.selectById(order.getExpressId());
        List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>().eq("order_id", orderId));

        List<Map<String, Object>> goods = new LinkedList<>();
        for (OrderGoods orderGoods : orderGoodsList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("goodsCode", orderGoods.getGoodsCode());
            map.put("goodsImage", UrlUtils.toAbsoluteUrl(orderGoods.getGoodsImage()));
            map.put("goodsName", orderGoods.getGoodsName());
            map.put("goodsSkuValue", orderGoods.getGoodsSkuValue());
            map.put("goodsNum", orderGoods.getGoodsNum());
            map.put("goodsPrice", orderGoods.getGoodsPrice());
            map.put("goodsMoney", orderGoods.getMoney());
            map.put("afterSale", orderGoods.getAfterSale());
            map.put("afterSaleMsg", OrderAfterEnum.afterSaleMsg(orderGoods.getAfterSale()));
            goods.add(map);
        }

        Map<String, Object> express = new LinkedHashMap<>();
        express.put("expressName", StringUtils.isNotNull(expressCompany) ? expressCompany.getName() : "-");
        express.put("expressNo", order.getExpressNo());
        express.put("expressTime", TimeUtils.timestampToDate(order.getExpressTime()));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("goods", goods);
        response.put("express", express);

        // 查询物流
        List<KdTrackResultVo> track = new LinkedList<>();
        String trackError = null;

        if (StringUtils.isNotNull(expressCompany)) {
            try {
                String expressEngine = ConfigUtils.get("logistics", "engine", "");
                if (StringUtils.isEmpty(expressEngine)) {
                    throw new OperateException("请联系管理员配置物流设置");
                }
                // 快递查询参数
                KdQueryTrackParam kdQueryTrackParam = new KdQueryTrackParam();
                // 物流公司编码
                String expressCode = expressCompany.getCodeKdniao();
                if (expressEngine.equals("kd100")) {
                    expressCode = expressCompany.getCodeKd100();
                    if (expressCode.equals("SF") || expressCode.equals("sf") || expressCode.equals("shunfeng")) {
                        kdQueryTrackParam.setPhone(order.getAddressMobile());
                    }
                } else {
                    if (expressCode.equals("SF")) {
                        kdQueryTrackParam.setPhone(order.getAddressMobile().substring(order.getAddressMobile().length() - 4));
                    }
                }
                kdQueryTrackParam.setCom(expressCode);
                kdQueryTrackParam.setNum(order.getExpressNo());
                track = (new DeliveryDriver()).queryTrack(kdQueryTrackParam);
            } catch (OperateException e) {
                trackError = e.getMsg();
            }
        }

        Map<String, Object> trajectory = new LinkedHashMap<>();
        trajectory.put("track", track);
        trajectory.put("trackError", trackError);
        response.put("trajectory", trajectory);

        return response;
    }

    /**
     * 当前售后时间
     *
     * @author mjf
     * @return Long
     */
    @Override
    public Long getAfterSaleDeadline() {
        float afterSalesDay = Float.parseFloat(ConfigUtils.get("trade", "afterSalesDay", "-1"));
        long afterSaleDeadline = TimeUtils.timestamp();
        if (afterSalesDay != -1) {
            afterSaleDeadline = (long)(afterSalesDay *24 * 60 * 60) + TimeUtils.timestamp();
        }
        return afterSaleDeadline;
    }

    @Override
    @Transactional
    public void selffetchVerify(Integer id, JSONArray items) {
        //判断格式是否正确
        if (items.size() == 0) {
            throw new OperateException("请提交至少一个自提项");
        }
        // 查找订单信息
        Order order = orderMapper.selectOne(
                new QueryWrapper<Order>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(order, "订单不存在!");
        //判断单据是否属于自提类型
        if (order.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_PICK.getCode()) == false) {
            throw new OperateException("该订单不是自提订单，不能核销");
        }
        //判断是否是否有可以自提
        for (Integer i = 0; i < items.size(); i++) {
            JSONObject itemJSON = JSONObject.parseObject(JSONObject.toJSONString(items.get(i)));
            if (StringUtils.isNull(itemJSON.getInteger("id"))) {
                throw new OperateException("核销商品格式错误，请重新提交");
            }
            OrderGoods orderGoods = orderGoodsMapper.selectOne(new QueryWrapper<OrderGoods>().eq("id", itemJSON.getInteger("id")));
            //判断是否售后中
            if (orderGoods.getAfterSale().equals(OrderGoodsEnum.AFTER_STATUS_ING.getCode())) { //售后中
                throw new OperateException(orderGoods.getGoodsName() + "正在售后中，不能核销");
            }
            if (orderGoods.getVerificationStatus() > 0) {
                throw new OperateException(orderGoods.getGoodsName() + "已经核销，不能重复核销");
            }

        }

        JSONArray snapshot = new JSONArray();

        if (order.getVerificationTime().equals(0L)) { //只记录第一次
            order.setVerificationTime(System.currentTimeMillis() / 1000);
            order.setVerificationBy(LikeAdminThreadLocal.getAdminId());
            order.setVerificationByType(OrderEnum.VERIFY_BYTYPE_ADMIN.getCode());

        }
        orderMapper.updateById(order);
        //开始核销
        for (Integer i = 0; i < items.size(); i++) {
            JSONObject goodsJSON = JSONObject.parseObject(JSONObject.toJSONString(items.get(i)));
            // 更新order goods 的核销状态
            OrderGoods orderGoods = new OrderGoods();
            orderGoods.setVerificationStatus(OrderGoodsEnum.VERIFYSTATUS_TRUE.getCode());
            orderGoods.setVerificationTime(System.currentTimeMillis() / 1000);
            orderGoodsMapper.update(orderGoods, new QueryWrapper<OrderGoods>().eq("id", goodsJSON.getInteger("id")));

            snapshot.add(new JSONObject() {{
                put("orderGoodsId", goodsJSON.getInteger("id"));
                put("goodsId", goodsJSON.getString("goods_id"));
                put("goodsName", goodsJSON.getString("goods_name"));
                put("goodsSkuId", goodsJSON.getInteger("goods_sku_id"));
                put("goodsSkuValue", goodsJSON.getInteger("goods_sku_value"));
                put("goodsOriginalPrice", goodsJSON.getInteger("goods_original_price"));
                put("goodsPrice", goodsJSON.getInteger("goods_price"));
                put("goodsNum", goodsJSON.getInteger("goods_num"));
                put("money", goodsJSON.getInteger("money"));
                put("goodsMoney", goodsJSON.getInteger("goods_money"));
                put("couponMoney", goodsJSON.getInteger("coupon_money"));
                put("needPayMoney", goodsJSON.getInteger("need_pay_money"));
                put("payMoney", goodsJSON.getInteger("pay_money"));
            }});
        }

        // 增加核销快照 la_verification
        Verification verification = new Verification();
        verification.setOrderId(order.getId());
        verification.setSelffetchShopId(order.getSelffetchShopId());
        verification.setHandleId(LikeAdminThreadLocal.getAdminId());
        verification.setVerificationScene(OrderGoodsEnum.VERIFICATION_SCENE_ADMIN.getCode());
        verification.setSnapshot(snapshot.toJSONString());
        verification.setCreateTime(System.currentTimeMillis() / 1000);
        verificationMapper.insert(verification);

        //如果全部核销完，更新order 的核销状态
        if (checkPickupAll(order.getId())) {
            order.setConfirmTime(System.currentTimeMillis() / 1000);
            order.setOrderStatus(OrderEnum.ORDER_STATUS_COMPLETED.getCode());
            order.setVerificationStatus(OrderGoodsEnum.VERIFYSTATUS_TRUE.getCode());
            orderMapper.updateById(order);
        }
    }

    @Override
    public List<OrderManageExportVo> exportExcel(OrderSearchValidate orderSearchValidate) {
        MPJQueryWrapper<Order> orderMPJQueryWrapper = new MPJQueryWrapper<>();
        Integer searchType = orderSearchValidate.getSearchType();
        String searchKeyword = orderSearchValidate.getSearchKeyword();
        Integer userSearchType = orderSearchValidate.getUserSearchType();
        String searchUserKeyword = orderSearchValidate.getSearchUserKeyword();
        String searchTimeType = orderSearchValidate.getSearchTimeType();
        Integer startTime = orderSearchValidate.getStartTime();
        Integer endTime = orderSearchValidate.getEndTime();

        // 订单搜索
        if(null != searchType && StringUtils.isNotEmpty(searchKeyword)){
            switch (searchType){
                case 1:
                    orderMPJQueryWrapper.like("t.order_sn","%"+searchKeyword+"%");
                    break;
                case 2:
                    orderMPJQueryWrapper.like("OG.goods_code","%"+searchKeyword+"%");
                    break;
                case 3:
                    orderMPJQueryWrapper.like("OG.goods_name","%"+searchKeyword+"%");
                    break;
            }
        }

        // 买家信息
        if(null != userSearchType && StringUtils.isNotEmpty(searchUserKeyword)){
            switch (userSearchType){
                case 1:
                    orderMPJQueryWrapper.like("U.sn","%"+searchUserKeyword+"%");
                    break;
                case 2:
                    orderMPJQueryWrapper.like("U.nickname","%"+searchUserKeyword+"%");
                    break;
                case 3:
                    orderMPJQueryWrapper.like("t.address_contact","%"+searchUserKeyword+"%");
                    break;
                case 4:
                    orderMPJQueryWrapper.like("t.address_mobile","%"+searchUserKeyword+"%");
                    break;
            }
        }

        // 时间搜索
        if(null != searchTimeType){
            String column = "t.create_time";
            if(searchTimeType.equals("pay")){
                column = "t.pay_time";
            }

            if(null != startTime){
                orderMPJQueryWrapper.ge(column, startTime);
            }

            if(null != endTime){
                orderMPJQueryWrapper.le(column, endTime);
            }
        }

        // 其它搜索
        orderMapper.setSearch(orderMPJQueryWrapper, orderSearchValidate, new String[]{
                "=:orderSource@t.order_source:int",
                "=:payStatus@t.pay_is:int",
                "=:payWay@t.pay_way:int",
        });

        if (StringUtils.isNotNull(orderSearchValidate.getPickupCode())) {
            orderMPJQueryWrapper.like("pickup_code", orderSearchValidate.getPickupCode());
        }

        if (StringUtils.isNotNull(orderSearchValidate.getDeliveryType())) {
            orderMPJQueryWrapper.eq("delivery_type", orderSearchValidate.getDeliveryType());
        }

        if (StringUtils.isNotNull(orderSearchValidate.getSelffetchShopId())) {
            orderMPJQueryWrapper.eq("selffetch_shop_id", orderSearchValidate.getSelffetchShopId());
        }

        if (StringUtils.isNotNull(orderSearchValidate.getOrderStatus())) {
            orderMPJQueryWrapper.eq("order_status", orderSearchValidate.getOrderStatus());
        }
        orderMPJQueryWrapper
                .leftJoin("?_order_goods OG ON t.id=OG.order_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_user U ON t.user_id=U.id".replace("?_", GlobalConfig.tablePrefix))
                .eq("t.is_delete", 0)
                .orderByDesc("t.id");

        Map<String, Object> statistics = __statistics(orderMPJQueryWrapper.clone());

        if (StringUtils.isNotNull(orderSearchValidate.getVerificationStatus())) {
            orderMPJQueryWrapper.eq("t.verification_status", orderSearchValidate.getVerificationStatus());
        }

        if (StringUtils.isNotNull(orderSearchValidate.getType()) && orderSearchValidate.getType() > 0) {
            orderMPJQueryWrapper.eq("t.order_status", orderSearchValidate.getType());
        }

        orderMPJQueryWrapper
                .select("DISTINCT t.id,t.user_id,t.order_sn,t.pay_is,t.pay_money,t.order_status,t.express_is,t.cancel_time," +
                        "t.address_contact,t.address_mobile,t.address_content,U.sn as user_sn,U.avatar,U.nickname, t.verification_status, t.selffetch_shop_id, t.verification_time, t.delivery_type, t.order_type");

        List<OrderManageListedVo> iPage = orderMapper.selectJoinList(
                OrderManageListedVo.class,
                orderMPJQueryWrapper);

        List<OrderManageExportVo> orderList = new ArrayList<>();
        for (OrderManageListedVo record : iPage) {
            OrderManageExportVo orderManageListedVo = new OrderManageExportVo();
            BeanUtils.copyProperties(record, orderManageListedVo);
            orderManageListedVo.setOrderSn(record.getOrderSn());
            orderManageListedVo.setContact(record.getAddressContact());
            orderManageListedVo.setMobile(record.getAddressMobile());

            List<OrderGoods> orderGoodsLists = orderGoodsMapper.selectList(
                    new QueryWrapper<OrderGoods>()
                            .eq("order_id", record.getId())
                            .select("goods_name,goods_sku_value,goods_num,goods_price"));

            String goodsName = "";
            for (OrderGoods list : orderGoodsLists) {
                goodsName += list.getGoodsName() + list.getGoodsSkuValue() + "\n";
            }
            orderManageListedVo.setGoodsInfo(goodsName);
            orderManageListedVo.setVerificationStatusStr(OrderEnum.getVerificationStatusMsg(record.getVerificationStatus()));
            orderManageListedVo.setOrderStatusStr(OrderEnum.getOrderStatusMsg(record.getOrderStatus()));
            orderManageListedVo.setSelffetchShop(iSelffetchShopService.getNameById(record.getSelffetchShopId()));
            orderManageListedVo.setSelffetchShopAddress(iSelffetchShopService.getAddressStrById(record.getSelffetchShopId()));
            orderManageListedVo.setVerificationTime(record.getVerificationTime().equals(0L) ? "-" : TimeUtils.timestampToDate(record.getVerificationTime()));

            orderList.add(orderManageListedVo);
        }

        return orderList;
    }

    /**
     * 订单统计
     *
     * @author cjh
     * @param mpjQueryWrapper 搜索器
     * @return Map<Object>
     */
    private Map<String, Object> __statistics(MPJQueryWrapper<Order> mpjQueryWrapper) {
        Integer waitPay     = OrderEnum.ORDER_STATUS_WAIT_PAY.getCode();
        Integer waitDeliver = OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode();
        Integer takeDeliver = OrderEnum.ORDER_STATUS_TAKE_DELIVER.getCode();
        Integer completed   = OrderEnum.ORDER_STATUS_COMPLETED.getCode();
        Integer cancel      = OrderEnum.ORDER_STATUS_CANCEL.getCode();
        Integer pickUpTrue      = OrderEnum.VERIFYSTATUS_TRUE.getCode();
        Integer pickUpFalse = OrderEnum.VERIFYSTATUS_FALSE.getCode();

        Map<String, Object> statistics = new LinkedHashMap<>();
        mpjQueryWrapper.select("DISTINCT t.id");

        // 全部
        MPJQueryWrapper<Order> allNumQuery = mpjQueryWrapper.clone();
        statistics.put("allNum", orderMapper.selectCount(allNumQuery.eq("t.is_delete", 0)));
        // 待付款
        MPJQueryWrapper<Order> waitPayNumQuery = mpjQueryWrapper.clone();
        statistics.put("waitPayNum", orderMapper.selectCount(waitPayNumQuery.eq("t.order_status", waitPay)));
        // 待发货
        MPJQueryWrapper<Order> waitDeliverNumQuery = mpjQueryWrapper.clone();
        statistics.put("waitDeliverNum", orderMapper.selectCount(waitDeliverNumQuery.eq("t.order_status", waitDeliver)));
        // 待收货
        MPJQueryWrapper<Order> takeDeliverNumQuery = mpjQueryWrapper.clone();
        statistics.put("takeDeliverNum", orderMapper.selectCount(takeDeliverNumQuery.eq("t.order_status", takeDeliver)));
        // 已完成
        MPJQueryWrapper<Order> completedNumQuery = mpjQueryWrapper.clone();
        statistics.put("completedNum", orderMapper.selectCount(completedNumQuery.eq("t.order_status", completed)));
        // 已取消
        MPJQueryWrapper<Order> cancelNumQuery = mpjQueryWrapper.clone();
        statistics.put("cancelNum", orderMapper.selectCount(cancelNumQuery.eq("t.order_status", cancel)));

        //已核销
        MPJQueryWrapper<Order> pickupNumQuery = mpjQueryWrapper.clone();
        statistics.put("pickupNum", orderMapper.selectCount(pickupNumQuery.eq("t.verification_status", pickUpTrue)));

        //未核销
        MPJQueryWrapper<Order> unPickupNumQuery = mpjQueryWrapper.clone();
        statistics.put("unPickupNum", orderMapper.selectCount(unPickupNumQuery.eq("t.verification_status", pickUpFalse)));

        return statistics;
    }

    /**
     * 获取取消按钮
     *
     * @author cjhao
     * @return Integer
     */
    private Integer __cancelBtn(Integer orderStatus) {
        int btn = 0;
        if(!orderStatus.equals(OrderEnum.ORDER_STATUS_CANCEL.getCode()) &&
           !orderStatus.equals(OrderEnum.ORDER_STATUS_COMPLETED.getCode())){
            btn = 1;
        }
        return btn;
    }

    /**
     * 确定按钮
     *
     * @author cjh
     * @param orderStatus 订单状态
     * @return Integer
     */
    private Integer __confirmBtn(Integer orderStatus) {
        int btn = 0;
        if(OrderEnum.ORDER_STATUS_TAKE_DELIVER.getCode() == orderStatus){
            btn = 1;
        }
        return btn;
    }

    /**
     * 删除按钮
     *
     * @author cjh
     * @param orderStatus 订单状态
     * @return Integer
     */
    private Integer __deleteBtn(Integer orderStatus) {
        int btn = 0;
        if(OrderEnum.ORDER_STATUS_COMPLETED.getCode() == orderStatus ||
           OrderEnum.ORDER_STATUS_CANCEL.getCode() == orderStatus) {
            btn = 1;
        }
        return btn;
    }

    /**
     * 发货按钮
     *
     * @author cjh
     * @param orderStatus 数据
     * @return Integer
     */
    private Integer __deliverBtn(Integer orderStatus) {
        int btn = 0;
        if(OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode() == orderStatus) {
            btn = 1;
        }
        return btn;
    }

    /**
     * 查看物流按钮
     *
     * @author cjhao
     * @param orderStatus 订单状态
     * @param expressIs 发货状态
     * @return Integer
     */
    private Integer __logisticsBtn(Integer orderStatus, Integer expressIs) {
        int btn = 0;
        if(OrderEnum.ORDER_STATUS_TAKE_DELIVER.getCode() == orderStatus &&
           1 == expressIs) {
            btn = 1;
        }
        return btn;
    }

    /**
     * 获取退款按钮
     *
     * @author cjh
     * @param orderStatus 订单状态
     * @param expressIs 发货状态
     * @return Integer
     */
    private Integer __refundBtn(Integer orderStatus, Integer expressIs) {
        int btn = 0;
        if(OrderEnum.ORDER_STATUS_TAKE_DELIVER.getCode() == orderStatus &&
                1 == expressIs) {
            btn = 1;
        }
        return btn;

    }

    /**
     * 检查是否所有单项都核销完毕
     *
     */
    @Override
    public Boolean checkPickupAll(Integer id) {
        OrderGoods orderGoods = orderGoodsMapper.selectOne(new QueryWrapper<OrderGoods>().eq("order_id", id).eq("verification_status", OrderGoodsEnum.VERIFYSTATUS_FALSE.getCode()).eq("after_sale", OrderGoodsEnum.AFTER_STATUS_NO.getCode()).last("limit 1"));    //无售后的记录 都被核销就算是完毕了
        if (StringUtils.isNull(orderGoods)) {
            //查下是否有售后中的记录
            List<Integer> afterStatus = new ArrayList<Integer>();
            afterStatus.add(OrderGoodsEnum.AFTER_STATUS_ING.getCode());
            afterStatus.add(OrderGoodsEnum.AFTER_STATUS_REJECT.getCode());
            OrderGoods afterGoods = orderGoodsMapper.selectOne(new QueryWrapper<OrderGoods>().eq("order_id", id).in("after_sale", afterStatus).last("limit 1"));
            if (StringUtils.isNull(afterGoods)) { //无售后中
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public List<String> getNamesByOid(Integer orderId) {
        List<String> ret = new ArrayList<>();
        List<OrderGoods> list = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>().eq("order_id", orderId).select("goods_name"));
        if (list.size() > 0) {
            for (OrderGoods item : list) {
                ret.add(item.getGoodsName());
            }
        }
        return ret;
    }

    private String geVerificationByStr(Integer verificationBy, Integer verificationByType) {
        if (verificationByType.equals(OrderEnum.VERIFY_BYTYPE_ADMIN.getCode())) { //管理员
            SystemAuthAdmin systemAuthAdmin = systemAuthAdminMapper.selectOne(new QueryWrapper<SystemAuthAdmin>()
                    .eq("id", verificationBy).last("limit 1"));

            return StringUtils.isNull(systemAuthAdmin) ? "" : systemAuthAdmin.getNickname();
        } else if (verificationByType.equals(OrderEnum.VERIFY_BYTYPE_USER.getCode())) {
            User user = userMapper.selectOne(new QueryWrapper<User>()
                    .eq("id", verificationBy)
                    .last("limit 1"));
            return StringUtils.isNull(user) ? "" : user.getNickname();
        } else {
            return "";
        }
    }
}
