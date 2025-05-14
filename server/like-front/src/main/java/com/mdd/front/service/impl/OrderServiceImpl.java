package com.mdd.front.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.common.entity.Verification;
import com.mdd.common.entity.distribution.DistributionOrder;
import com.mdd.common.entity.selffetchshop.SelffetchShop;
import com.mdd.common.mapper.VerificationMapper;
import com.mdd.common.mapper.distribution.DistributionOrderMapper;
import com.mdd.common.mapper.selffetchshop.SelffetchShopMapper;
import com.mdd.common.plugin.alipay.AliPayDriver;
import com.mdd.common.plugin.alipay.request.AliPayRefundRequest;
import com.mdd.common.plugin.alipay.request.AliPaymentRequest;
import com.mdd.front.service.*;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.order.BuyGoodsValidate;
import com.mdd.front.validate.order.OrderSettleValidate;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.RefundLog;
import com.mdd.common.entity.RefundRecord;
import com.mdd.common.entity.cart.Cart;
import com.mdd.common.entity.coupon.Coupon;
import com.mdd.common.entity.coupon.CouponList;
import com.mdd.common.entity.delivery.ExpressCompany;
import com.mdd.common.entity.delivery.ExpressTpl;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.entity.goods.GoodsSku;
import com.mdd.common.entity.order.Order;
import com.mdd.common.entity.order.OrderAfter;
import com.mdd.common.entity.order.OrderAfterGoods;
import com.mdd.common.entity.order.OrderGoods;
import com.mdd.common.entity.seckill.SeckillActivity;
import com.mdd.common.entity.seckill.SeckillGoodsSku;
import com.mdd.common.entity.user.User;
import com.mdd.common.enums.*;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.RefundLogMapper;
import com.mdd.common.mapper.RefundRecordMapper;
import com.mdd.common.mapper.cart.CartMapper;
import com.mdd.common.mapper.coupon.CouponListMapper;
import com.mdd.common.mapper.coupon.CouponMapper;
import com.mdd.common.mapper.delivery.ExpressCompanyMapper;
import com.mdd.common.mapper.delivery.ExpressTplMapper;
import com.mdd.common.mapper.goods.GoodsMapper;
import com.mdd.common.mapper.goods.GoodsSkuMapper;
import com.mdd.common.mapper.log.LogMoneyMapper;
import com.mdd.common.mapper.log.LogOrderAfterMapper;
import com.mdd.common.mapper.order.OrderAfterGoodsMapper;
import com.mdd.common.mapper.order.OrderAfterMapper;
import com.mdd.common.mapper.order.OrderGoodsMapper;
import com.mdd.common.mapper.log.LogOrderMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.front.validate.selffetchorder.SelffetchOrderPickCodeVerifyValidate;
import com.mdd.front.validate.selffetchorder.SelffetchOrderSearchValidate;
import com.mdd.front.vo.order.*;
import com.mdd.front.vo.selffetchOrder.SelffetchOrderDetailVo;
import com.mdd.front.vo.selffetchOrder.SelffetchOrderPickuplVo;
import com.mdd.front.vo.selffetchshop.SelffetchShopDetailVo;
import com.mdd.front.vo.user.UserAddressVo;
import com.mdd.common.mapper.seckill.SeckillActivityMapper;
import com.mdd.common.mapper.seckill.SeckillGoodsSkuMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.plugin.delivery.DeliveryDriver;
import com.mdd.common.plugin.delivery.vo.KdQueryTrackParam;
import com.mdd.common.plugin.delivery.vo.KdTrackResultVo;
import com.mdd.common.plugin.wechat.WxPayDriver;
import com.mdd.common.plugin.wechat.request.RefundRequestV3;
import com.mdd.common.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements IOrderService {

    @Resource
    GoodsMapper goodsMapper;

    @Resource
    GoodsSkuMapper goodsSkuMapper;

    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderGoodsMapper orderGoodsMapper;

    @Resource
    OrderAfterMapper orderAfterMapper;

    @Resource
    LogOrderMapper logOrderMapper;

    @Resource
    ExpressTplMapper expressTplMapper;

    @Resource
    CartMapper cartMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    LogOrderAfterMapper logOrderAfterMapper;

    @Resource
    LogMoneyMapper logMoneyMapper;

    @Resource
    RefundRecordMapper refundRecordMapper;

    @Resource
    RefundLogMapper refundLogMapper;

    @Resource
    OrderAfterGoodsMapper orderAfterGoodsMapper;

    @Resource
    ExpressCompanyMapper expressCompanyMapper;

    @Resource
    SeckillActivityMapper seckillActivityMapper;

    @Resource
    SeckillGoodsSkuMapper seckillGoodsSkuMapper;

    @Resource
    CouponMapper couponMapper;

    @Resource
    CouponListMapper couponListMapper;

    @Resource
    DistributionOrderMapper distributionOrderMapper;
    @Resource
    VerificationMapper verificationMapper;
    @Resource
    SelffetchShopMapper selffetchShopMapper;

    @Resource
    IUserAddressService iUserAddressService;

    @Resource
    DataSourceTransactionManager transactionManager;

    @Resource
    TransactionDefinition transactionDefinition;
    @Resource
    ISelffetchShopService iSelffetchShopService;
    @Resource
    ISelffetchVerifierService iSelffetchVerifierService;
    @Resource
    IOrderGoodsService iOrderGoodsService;

    /**
     * 结算订单
     *
     * @param orderSettleValidate OrderSettleValidate
     * @return OrderSettleResultVo
     * @author mjf
     */
    @Override
    public OrderSettleResultVo settlementOrder(OrderSettleValidate orderSettleValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        // 商品信息
        List<OrderGoodsInfoVo> orderGoodsList = getOrderGoodsInfo(userId, orderSettleValidate);
        // 用户地址
        UserAddressVo userAddress = iUserAddressService.getUserAddressById(userId, orderSettleValidate.getAddressId());

        // 订单商品数量
        Integer totalNum = 0;
        // 商品总金额
        BigDecimal goodsAmount = BigDecimal.ZERO;
        for (OrderGoodsInfoVo goodsInfoVo : orderGoodsList) {
            // 商品金额
            if (goodsInfoVo.getBuyAble().equals(true)) { //只有可购买的记录
                // 商品数量
                totalNum += goodsInfoVo.getNum();
                goodsAmount = goodsAmount.add(goodsInfoVo.getGoodsAmount());
            }
        }

        // 秒杀活动ID
        int seckillId = 0;
        // 订单类型
        int orderType = OrderEnum.ORDER_SOURCE_NORMAL.getCode();
        if (orderGoodsList.get(0).getGoodsType().equals(OrderGoodsEnum.GOODS_TYPE_SECKILL.getCode())) {
            orderType = OrderEnum.ORDER_SOURCE_SECKILL.getCode();
            seckillId = orderSettleValidate.getBuyGoods().get(0).getSeckillId();
        }

        //计算优惠券
        Integer couponListId = orderSettleValidate.getCouponListId();
        BigDecimal couponAmount = BigDecimal.ZERO;
        if (orderType == OrderEnum.ORDER_SOURCE_NORMAL.getCode()) {
            couponAmount = calculateCoupon(orderGoodsList, couponListId, userId);
        }

        // 计算运费
        BigDecimal totalFreight = calculateFreight(orderGoodsList);
        // 订单总金额
        BigDecimal totalAmount = goodsAmount.add(totalFreight);
        // 订单应付金额
        BigDecimal orderAmount = totalAmount.subtract(couponAmount);

        // 配送方式
        String deliveryName = ConfigUtils.getMap("express", "method").getOrDefault("expressName", "快递发货");

        // 订单结算数据
        OrderSettleResultVo settleResultVo = new OrderSettleResultVo();
        settleResultVo.setOrderAmount(orderAmount);
        settleResultVo.setGoodsAmount(goodsAmount);
        settleResultVo.setTotalNum(totalNum);
        if (orderSettleValidate.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_PICK.getCode())) {
            settleResultVo.setAddress(orderSettleValidate.getAddress());
        } else {
            settleResultVo.setAddress(userAddress);
        }
        settleResultVo.setFreightAmount(totalFreight);
        settleResultVo.setTotalAmount(totalAmount);
        settleResultVo.setGoods(orderGoodsList);
        if (StringUtils.isNotNull(orderSettleValidate.getDeliveryType())) {
            settleResultVo.setDeliveryType(orderSettleValidate.getDeliveryType());
            settleResultVo.setSelffetchShopId(orderSettleValidate.getSelffetchShopId());
        } else {
            settleResultVo.setDeliveryType(1);
        }

        settleResultVo.setDeliveryTypeDesc(deliveryName);
        settleResultVo.setRemark(orderSettleValidate.getRemark());
        settleResultVo.setSeckillId(seckillId);
        settleResultVo.setOrderType(orderType);
        settleResultVo.setCouponAmount(couponAmount);
        settleResultVo.setCouponListId(couponListId);

        //获取上个自提单信息
        Order lastOrder = orderMapper.selectOne(new QueryWrapper<Order>()
                .eq("user_id", userId)
                .eq("delivery_type", OrderEnum.DELIVERY_TYPE_PICK.getCode())
                .orderByDesc("id")
                .last("limit 1")
        );
        if (StringUtils.isNotNull(lastOrder)) {
            settleResultVo.setLastAddressContact(lastOrder.getAddressContact());
            settleResultVo.setLastAddressMobile(lastOrder.getAddressMobile());
            settleResultVo.setLastSelffetchShop(iSelffetchShopService.lastDetailById(lastOrder.getSelffetchShopId()));
        } else {
            settleResultVo.setLastAddressContact("");
            settleResultVo.setLastAddressContact("");
            settleResultVo.setLastSelffetchShop(null);
        }

        return settleResultVo;
    }

    /**
     * 提交订单
     *
     * @param orderSettleValidate OrderSettleValidate
     * @return SubmitOrderResultVo
     * @author mjf
     */
    @Override
    public OrderSubmitResultVo submitOrder(OrderSettleValidate orderSettleValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        // 购买类型 [buyNow=立即购买 cart=购物车购买]
        String buyType = orderSettleValidate.getBuyType();
        // 计算订单金额
        OrderSettleResultVo settleResultVo = settlementOrder(orderSettleValidate);

        // 订单商品信息
        List<BuyGoodsValidate> buyGoodsValidate = orderSettleValidate.getBuyGoods();
        List<OrderGoodsInfoVo> orderGoodsList = settleResultVo.getGoods();

        // 添加订单，订单日志
        Order order = addOrder(settleResultVo, userId);

        // 购物车下单，清除购物车
        this.__deleteCart(buyType, buyGoodsValidate);
        // 扣除库存
        this.__updateGoodsStock(orderGoodsList);
        // 删除优惠券
        this.__updateCouponList(settleResultVo.getCouponListId(), order.getId(), userId);

        // 返回订单参数
        OrderSubmitResultVo submitResultVo = new OrderSubmitResultVo();
        submitResultVo.setOrderId(order.getId());
        submitResultVo.setOrderSn(order.getOrderSn());
        submitResultVo.setNeedPayMoney(order.getNeedPayMoney());
        return submitResultVo;
    }

    /**
     * 订单列表
     *
     * @param pageValidate PageValidate
     * @param status       Integer
     * @return PageResult<OrderListVo>
     * @author mjf
     */
    @Override
    public PageResult<OrderListVo> list(PageValidate pageValidate, Integer status) {
        // 分页
        Integer userId = LikeFrontThreadLocal.getUserId();
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        // 订单状态
        if (status > 0) {
            orderQueryWrapper.eq("order_status", status);
        }
        orderQueryWrapper.eq("is_delete", 0)
                .eq("user_id", userId)
                .orderByDesc("id");
        IPage<Order> iPage = orderMapper.selectPage(new Page<>(pageNo, pageSize), orderQueryWrapper);
        // 处理数据
        List<OrderListVo> orderList = new ArrayList<>();
        for (Order record : iPage.getRecords()) {
            OrderListVo orderListVo = new OrderListVo();
            BeanUtils.copyProperties(record, orderListVo);
            // 订单商品
            List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>().
                    eq("order_id", record.getId())
                    .select("id,goods_id,goods_name,goods_sku_id,goods_sku_value,goods_image,goods_num,goods_price," +
                            "goods_original_price,need_pay_money,goods_money"));
            List<OrderGoodsListVo> orderGoodsListVos = new ArrayList<>();
            for (OrderGoods orderGoods : orderGoodsList) {
                orderGoods.setGoodsImage(UrlUtils.toAbsoluteUrl(orderGoods.getGoodsImage()));
                OrderGoodsListVo orderGoodsListVo = new OrderGoodsListVo();
                BeanUtils.copyProperties(orderGoods, orderGoodsListVo);
                orderGoodsListVos.add(orderGoodsListVo);
            }
            // 订单商品信息
            orderListVo.setOrderGoodsList(orderGoodsListVos);
            // 订单信息
            orderListVo.setOrderStatus(record.getOrderStatus());
            orderListVo.setOrderStatusMsg(OrderEnum.getOrderStatusMsg(record.getOrderStatus()));
            orderListVo.setCancelTime(getCancelUnpaidOrderTime(record.getCreateTime()));
            // 订单按钮
            orderListVo.setPayBtn(__payBtn(record));
            orderListVo.setCancelBtn(__cancelBtn(record));
            orderListVo.setConfirmBtn(__confirmBtn(record));
            orderListVo.setCommentBtn(__commentBtn(record));
            orderListVo.setDeleteBtn(__deleteBtn(record));
            orderListVo.setRefundBtn(__refundBtn(record));
            orderListVo.setLogisticsBtn(__logisticsBtn(record));
            orderList.add(orderListVo);
        }
        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), orderList);
    }

    /**
     * 订单详情
     *
     * @param id Integer
     * @return OrderDetailVo
     * @author mjf
     */
    @Override
    public OrderDetailVo detail(Integer id) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        // 订单
        Order order = orderMapper.selectOne(new QueryWrapper<Order>()
                .eq("id", id)
                .eq("user_id", userId)
                .eq("is_delete", 0)
                .last("limit 1"));

        Assert.notNull(order, "订单信息不存在");

        // 订单商品
        List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>()
                .eq("order_id", order.getId()));

        // 订单商品处理
        List<OrderGoodsListVo> orderGoodsListVoList = new ArrayList<>();
        for (OrderGoods orderGoods : orderGoodsList) {
            OrderGoodsListVo orderGoodsListVo = new OrderGoodsListVo();
            BeanUtils.copyProperties(orderGoods, orderGoodsListVo);
            orderGoodsListVo.setGoodsImage(UrlUtils.toAbsoluteUrl(orderGoods.getGoodsImage()));
            orderGoodsListVo.setAfterSalesBtn(this.__afterApplyBtn(order, orderGoods.getId()));
            orderGoodsListVo.setAfterSalesMsg(this.__afterApplyMsg(order, orderGoods.getId()));
            orderGoodsListVoList.add(orderGoodsListVo);
        }

        // 订单信息处理
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        BeanUtils.copyProperties(order, orderDetailVo);
        orderDetailVo.setOrderTypeMsg(OrderEnum.getOrderTypeMsg(order.getOrderType()));
        orderDetailVo.setOrderGoodsList(orderGoodsListVoList);
        orderDetailVo.setCreateTime(TimeUtils.timestampToDate(order.getCreateTime()));

        String expressTime = order.getExpressTime() > 0 ? TimeUtils.timestampToDate(order.getExpressTime()) : "-";
        String payTime = order.getPayTime() > 0 ? TimeUtils.timestampToDate(order.getPayTime()) : "-";
        String confirmTime = order.getConfirmTime() > 0 ? TimeUtils.timestampToDate(order.getConfirmTime()) : "-";
        orderDetailVo.setExpressTime(expressTime);
        orderDetailVo.setPayTime(payTime);
        orderDetailVo.setConfirmTime(confirmTime);

        orderDetailVo.setCancelTime(getCancelUnpaidOrderTime(order.getCreateTime()));
        orderDetailVo.setOrderStatus(order.getOrderStatus());
        orderDetailVo.setOrderStatusMsg(OrderEnum.getOrderStatusMsg(order.getOrderStatus(), order.getDeliveryType()));

        // 订单按钮
        orderDetailVo.setPayBtn(__payBtn(order));
        orderDetailVo.setCancelBtn(__cancelBtn(order));
        orderDetailVo.setConfirmBtn(__confirmBtn(order));
        orderDetailVo.setDeleteBtn(__deleteBtn(order));
        orderDetailVo.setCommentBtn(__commentBtn(order));
        orderDetailVo.setLogisticsBtn(__logisticsBtn(order));
        orderDetailVo.setRefundBtn(__refundBtn(order));
        orderDetailVo.setDeliveryTypeStr(DeliverEnum.getDeliverTypeMsg(order.getDeliveryType()));
        orderDetailVo.setPickupCode(order.getPickupCode());
        if (order.getSelffetchShopId().equals(0) == false) {
            SelffetchShopDetailVo selffetchShopDetailVo = iSelffetchShopService.detail(order.getSelffetchShopId());
            orderDetailVo.setSelffetchShop(selffetchShopDetailVo);
        }
        return orderDetailVo;
    }

    /**
     * 确认收货
     *
     * @param id Integer
     * @author mjf
     */
    @Override
    public void confirm(Integer id) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        // 订单
        Order order = orderMapper.selectOne(new QueryWrapper<Order>()
                .eq("id", id)
                .eq("user_id", userId)
                .eq("is_delete", 0)
                .last("limit 1"));

        Assert.notNull(order, "订单信息不存在");
        if (order.getOrderStatus() <= OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode()) {
            throw new OperateException("订单未发货");
        }
        if (order.getOrderStatus() >= OrderEnum.ORDER_STATUS_COMPLETED.getCode()) {
            throw new OperateException("订单已完成");
        }

        // 更新信息
        order.setOrderStatus(OrderEnum.ORDER_STATUS_COMPLETED.getCode());
        order.setConfirmTime(System.currentTimeMillis() / 1000);
        // 售后截止时间
        order.setAfterDeadline(__getAfterSaleDeadline());
        orderMapper.updateById(order);
        // 订单日志
        logOrderMapper.add(order.getId(),
                OrderLogEnum.TYPE_USER.getCode(),
                OrderLogEnum.CHANNEL_USER_CONFIRM_ORDER.getCode(),
                userId, OrderLogEnum.CHANNEL_USER_CONFIRM_ORDER.getMsg());
    }

    /**
     * 删除订单
     *
     * @param id Integer
     * @author mjf
     */
    @Override
    public void del(Integer id) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        // 订单
        Order order = orderMapper.selectOne(new QueryWrapper<Order>()
                .eq("id", id)
                .eq("user_id", userId)
                .eq("is_delete", 0)
                .last("limit 1"));

        Assert.notNull(order, "订单信息不存在");
        if (order.getOrderStatus() != OrderEnum.ORDER_STATUS_CANCEL.getCode()) {
            throw new OperateException("订单无法删除");
        }

        // 在售后中则无法删除
        OrderAfter orderAfter = orderAfterMapper.selectOne(new QueryWrapper<OrderAfter>()
                .eq("order_id", order.getId())
                .eq("after_status", OrderAfterEnum.AFTER_STATUS_ING.getCode())
                .last("limit 1"));

        Assert.notNull(orderAfter, "订单处于售后中，不可删除");

        order.setIsDelete(1);
        order.setUpdateTime(System.currentTimeMillis() / 1000);
        orderMapper.updateById(order);

        // 订单日志
        logOrderMapper.add(order.getId(),
                OrderLogEnum.TYPE_USER.getCode(),
                OrderLogEnum.CHANNEL_USER_DEL_ORDER.getCode(),
                userId, OrderLogEnum.CHANNEL_USER_DEL_ORDER.getMsg());
    }

    /**
     * 取消订单
     *
     * @param id Integer
     * @author mjf
     */
    @Override
    public void cancel(Integer id) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        // 订单
        Order order = orderMapper.selectOne(new QueryWrapper<Order>()
                .eq("id", id)
                .eq("user_id", userId)
                .eq("is_delete", 0)
                .last("limit 1"));

        Assert.notNull(order, "订单信息不存在");
        // 订单已取消
        if (order.getOrderStatus() == OrderEnum.ORDER_STATUS_CANCEL.getCode()) {
            throw new OperateException("订单不可取消");
        }

        if (order.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_PICK.getCode())) {   //到店自提
            if (order.getOrderStatus() > OrderEnum.ORDER_STATUS_PICKUP_DELIVER.getCode()) {
                throw new OperateException("订单不可取消");
            }
        } else {    //其他单据
            // 订单已发货
            if (order.getOrderStatus() > OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode()) {
                throw new OperateException("订单不可取消");
            }
        }
        // 订单待发货时一定时间内可取消
        if ((order.getOrderStatus() == OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode()) || (order.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_PICK.getCode()) && order.getOrderStatus().equals(OrderEnum.ORDER_STATUS_PICKUP_DELIVER.getCode()))) {
            // 订单允许取消时长
            float cancelUnshippedOrderTime = Float.parseFloat(ConfigUtils.get("trade", "cancelUnshippedOrderTime", "-1"));
            if (cancelUnshippedOrderTime == -1) {
                throw new OperateException("订单不可取消");
            }
            long ableCancelTime = (long) (cancelUnshippedOrderTime * 60) + order.getPayTime();
            if (TimeUtils.timestamp() > ableCancelTime) {
                throw new OperateException("订单不可取消");
            }

            // 校验是否已产生售后记录
            OrderAfter orderAfter = orderAfterMapper.selectOne(new QueryWrapper<OrderAfter>()
                    .eq("order_id", order.getId())
                    .in("after_status", Arrays.asList(OrderAfterEnum.AFTER_STATUS_ING.getCode(), OrderAfterEnum.AFTER_STATUS_SUCCESS.getCode()))
                    .last("limit 1"));
            if (orderAfter != null) {
                throw new OperateException("订单已发生售后，不可取消");
            }
        }

        // 取消订单
        this.__handleCancelOrder(order);
    }

    /**
     * 查询物流
     *
     * @param userId Integer
     * @param id     Integer
     * @return OrderLogisticsVo
     * @author mjf
     */
    @Override
    public OrderLogisticsVo logistics(Integer userId, Integer id) {
        Order order = orderMapper.selectOne(new QueryWrapper<Order>()
                .eq("user_id", userId)
                .eq("id", id)
                .last("limit 1"));
        Assert.notNull(order, "订单信息不存在");

        if (order.getExpressIs() != 1) {
            throw new OperateException("订单未发货");
        }

        OrderGoods orderGoods = orderGoodsMapper.selectOne(new QueryWrapper<OrderGoods>()
                .eq("order_id", order.getId())
                .orderByAsc("id")
                .last("limit 1"));

        OrderLogisticsVo vo = new OrderLogisticsVo();
        vo.setGoodsName(orderGoods.getGoodsName());
        vo.setGoodsImage(UrlUtils.toAbsoluteUrl(orderGoods.getGoodsImage()));
        vo.setExpressNo(order.getExpressNo());
        vo.setCreateTime(TimeUtils.timestampToDate(order.getCreateTime()));
        vo.setPayTime(TimeUtils.timestampToDate(order.getPayTime()));
        vo.setExpressTime(TimeUtils.timestampToDate(order.getExpressTime()));
        vo.setContact(order.getAddressContact());
        vo.setMobile(order.getAddressMobile());
        vo.setAddress(order.getAddressContent());
        vo.setOrderGoodsNum(order.getGoodsNum());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setOrderStatusMsg(OrderEnum.getOrderStatusMsg(order.getOrderStatus()));
        vo.setConfirmTime(TimeUtils.timestampToDate(order.getConfirmTime()));

        if (order.getExpressId() < 0 || StringUtils.isEmpty(order.getExpressNo())) {
            throw new OperateException("订单无需物流");
        }

        try {
            String expressEngine = ConfigUtils.get("logistics", "engine", "");
            if (StringUtils.isEmpty(expressEngine)) {
                throw new OperateException("请联系管理员配置物流设置");
            }
            // 物流公司
            ExpressCompany expressCompany = expressCompanyMapper.selectById(order.getExpressId());
            vo.setExpressName(expressCompany.getName());

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
            List<KdTrackResultVo> track = (new DeliveryDriver()).queryTrack(kdQueryTrackParam);
            // 物流信息
            vo.setTrack(track);
        } catch (OperateException e) {
            // 查询中错误信息
            vo.setTrackError(e.getMsg());
        }
        return vo;
    }

    /**
     * 系统取消时间
     *
     * @return Long
     * @author mjf
     */
    @Override
    public Long getCancelUnpaidOrderTime(Long orderCreateTime) {
        // 系统自动取消订单时间
        float cancelTimeConfig = Float.parseFloat(ConfigUtils.get("trade", "cancelUnpaidOrderTime", "-1"));
        long cancelUnpaidOrderTime = 0;
        if (cancelTimeConfig != -1) {
            cancelUnpaidOrderTime = (long) (cancelTimeConfig * 60);
            long diff = (orderCreateTime + cancelUnpaidOrderTime) - TimeUtils.timestamp();
            if (diff <= 0) {
                cancelUnpaidOrderTime = 0;
            } else {
                cancelUnpaidOrderTime = diff;
            }
        }
        return cancelUnpaidOrderTime;
    }

    /**
     * 获取订单商品信息
     *
     * @param userId              Integer
     * @param orderSettleValidate OrderSettleValidate
     * @return List<OrderGoodsInfoVo>
     * @author mjf
     */
    public List<OrderGoodsInfoVo> getOrderGoodsInfo(Integer userId, OrderSettleValidate orderSettleValidate) {
        // 购买类型
        String buyType = orderSettleValidate.getBuyType();
        // 商品参数
        List<BuyGoodsValidate> buyGoodsValidate = orderSettleValidate.getBuyGoods();
        // 商品信息list
        List<OrderGoodsInfoVo> orderGoodsList = new ArrayList<>();
        // 购物车购买
        if (buyType.equals("cart")) {
            // 购物车Ids
            List<Integer> cartIds = buyGoodsValidate.stream().map(BuyGoodsValidate::getCartId).collect(Collectors.toList());
            if (cartIds.isEmpty()) {
                throw new OperateException("请选择商品");
            }

            // 商品信息
            orderGoodsList = goodsMapper.selectJoinList(OrderGoodsInfoVo.class, new MPJQueryWrapper<Goods>()
                    .eq("t.is_delete", 0)
                    .eq("c.is_delete", 0)
                    .eq("user_id", userId)
                    .in("c.id", cartIds)
                    .innerJoin("?_cart c ON c.goods_id = t.id".replace("?_", GlobalConfig.tablePrefix))
                    .innerJoin("?_goods_sku GS ON GS.id = c.goods_sku_id".replace("?_", GlobalConfig.tablePrefix))
                    .select("t.code as goods_code,t.express_type,t.express_template_id,t.image,t.name as goods_name,t.status," +
                            "GS.goods_id,GS.id as goods_sku_id,GS.image as sku_image,GS.sku_value_arr," +
                            "GS.stock,GS.price,GS.price as original_price,GS.weight,GS.volume,c.num,t.is_express,t.is_selffetch"));

            Assert.notEmpty(orderGoodsList, "商品信息不存在");

            for (OrderGoodsInfoVo goodsInfoVo : orderGoodsList) {
                goodsInfoVo.setGoodsType(OrderGoodsEnum.GOODS_TYPE_NORMAL.getCode());
                if (orderSettleValidate.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_EXPRESS.getCode())) { //快递
                    goodsInfoVo.setDeliveryAble(goodsInfoVo.getIsExpress().equals(1) ? true : false);
                } else if (orderSettleValidate.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_PICK.getCode())) {
                    goodsInfoVo.setDeliveryAble(goodsInfoVo.getIsSelffetch().equals(1) ? true : false);
                    goodsInfoVo.setExpressMoney(new BigDecimal(0));
                    goodsInfoVo.setExpressTemplateId(0);
                    goodsInfoVo.setExpressType(1);
                }
            }

        } else {
            // 立即购买只有一个商品
            BuyGoodsValidate buyNowGoods = buyGoodsValidate.get(0);
            // 商品信息
            OrderGoodsInfoVo oneOrderGoods = goodsMapper.selectJoinOne(OrderGoodsInfoVo.class, new MPJQueryWrapper<Goods>()
                    .eq("t.is_delete", 0)
                    .eq("GS.id", buyNowGoods.getSkuId())
                    .innerJoin("?_goods_sku GS ON GS.goods_id=t.id".replace("?_", GlobalConfig.tablePrefix))
                    .select("t.code as goods_code,t.express_type,t.express_template_id,t.image,t.name as goods_name,t.status," +
                            "GS.goods_id,GS.id as goods_sku_id,GS.image as sku_image,GS.sku_value_arr," +
                            "GS.stock,GS.price,GS.price as original_price,GS.weight,GS.volume,t.is_express,t.is_selffetch")
                    .last("limit 1"));

            if (oneOrderGoods == null) {
                throw new OperateException("商品信息不存在");
            }

            // 立即购买只有一个商品
            oneOrderGoods.setNum(buyGoodsValidate.get(0).getNum());
            oneOrderGoods.setGoodsType(OrderGoodsEnum.GOODS_TYPE_NORMAL.getCode());
            if (orderSettleValidate.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_EXPRESS.getCode())) { //快递
                oneOrderGoods.setDeliveryAble(oneOrderGoods.getIsExpress().equals(1) ? true : false);
            } else if (orderSettleValidate.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_PICK.getCode())) {
                oneOrderGoods.setDeliveryAble(oneOrderGoods.getIsSelffetch().equals(1) ? true : false);
                oneOrderGoods.setExpressMoney(new BigDecimal(0));
                oneOrderGoods.setExpressTemplateId(0);
                oneOrderGoods.setExpressType(1);
            }

            // 秒杀活动
            if (buyNowGoods.getSeckillId() != null && buyNowGoods.getSeckillId() > 0) {
                SeckillGoodsSku seckillGoodsSku = this.__getSeckillGoodsSku(buyNowGoods);
                if (seckillGoodsSku != null) {
                    oneOrderGoods.setPrice(seckillGoodsSku.getSeckillPrice());
                    oneOrderGoods.setGoodsType(OrderGoodsEnum.GOODS_TYPE_SECKILL.getCode());
                }
            }
            orderGoodsList.add(oneOrderGoods);
        }

        for (OrderGoodsInfoVo goodsInfoVo : orderGoodsList) {
//            if (goodsInfoVo.getStatus() == 0) {
//                throw new OperateException("商品:" + goodsInfoVo.getGoodsName() + " 已经下架，请选择商品下单");
//            }
//            if (goodsInfoVo.getNum() <= 0 || goodsInfoVo.getStock() < goodsInfoVo.getNum()) {
//                throw new OperateException("商品:" + goodsInfoVo.getGoodsName() + " 当前库存仅剩:" + goodsInfoVo.getStock());
//            }

            goodsInfoVo.setBuyAble(this.setBuyAbleStatus(goodsInfoVo));
            goodsInfoVo.setBuyAbleMsg(this.setBuyAbleStatusMsg(goodsInfoVo));
            BigDecimal itemGoodsAmount = goodsInfoVo.getPrice().multiply(BigDecimal.valueOf(goodsInfoVo.getNum()));
            goodsInfoVo.setGoodsAmount(itemGoodsAmount);
            goodsInfoVo.setImage(UrlUtils.toAbsoluteUrl(goodsInfoVo.getImage()));
            goodsInfoVo.setCouponMoney(BigDecimal.ZERO);
            goodsInfoVo.setExpressMoney(BigDecimal.ZERO);
        }

        return orderGoodsList;
    }

    /**
     * 运费计算
     *
     * @param orderGoodsList List<OrderGoodsInfoVo>
     * @return BigDecimal
     * @author mjf
     */
    public BigDecimal calculateFreight(List<OrderGoodsInfoVo> orderGoodsList) {
        BigDecimal totalFreight = BigDecimal.ZERO;
        for (OrderGoodsInfoVo goodsInfoVo : orderGoodsList) {
            if (goodsInfoVo.getDeliveryAble().equals(false)) { //配送失效的记录不参与计算
                continue;
            }
            if (goodsInfoVo.getBuyAble().equals(false)) { //不能购买的也不参与计算
                continue;
            }
            // 运费
            BigDecimal expressMoney = BigDecimal.ZERO;

            // 设置单个商品运费
            goodsInfoVo.setExpressMoney(expressMoney);

            // 1=包邮 2=运费模板
            if (1 == goodsInfoVo.getExpressType()) {
                continue;
            }

            // 运费模板
            ExpressTpl expressTpl = expressTplMapper.selectOne(new QueryWrapper<ExpressTpl>()
                    .eq("id", goodsInfoVo.getExpressTemplateId())
                    .eq("is_delete", 0)
                    .last("limit 1"));
            if (expressTpl == null) {
                continue;
            }

            // 商品比较单位
            Double unit = 0.0;
            switch (expressTpl.getType()) {
                //按件计费
                case 0:
                    unit = Double.valueOf(goodsInfoVo.getNum());
                    break;
                //体积计费
                case 1:
                    unit = goodsInfoVo.getVolume();
                    break;
                //重量计费
                case 2:
                    unit = goodsInfoVo.getWeight();
                    break;
            }

            // 首(重/件/立方)
            Integer firstNum = expressTpl.getFirstNum();
            if (unit <= firstNum) {
                // 小于首重，仅付首重金额
                expressMoney = expressTpl.getFirstPrice();
            } else {
                // 减去首重后的重量 / 续重
                int left = (int) Math.ceil((unit - firstNum) / (double) expressTpl.getContinueNum());
                // 续重需付金额
                BigDecimal continuePrice = expressTpl.getContinuePrice().multiply(BigDecimal.valueOf(left));
                // 首重金额 + 续重金额
                expressMoney = expressTpl.getFirstPrice().add(continuePrice);
            }

            // 设置单个商品运费
            goodsInfoVo.setExpressMoney(expressMoney);
            // 订单总运费
            totalFreight = totalFreight.add(expressMoney);
        }
        return totalFreight;
    }

    /**
     * 优惠券优惠金额
     *
     * @param orderGoodsList List<OrderGoodsInfoVo>
     * @param couponListId   Integer
     * @return BigDecimal
     * @author mjf
     */
    public BigDecimal calculateCoupon(List<OrderGoodsInfoVo> orderGoodsList, Integer couponListId, Integer userId) {
        // 总优惠金额
        BigDecimal totalDiscountMoney = BigDecimal.ZERO;
        if (couponListId == null || couponListId <= 0) {
            return totalDiscountMoney;
        }

        // 优惠券领取记录
        CouponList couponList = couponListMapper.selectOne(new QueryWrapper<CouponList>()
                .eq("id", couponListId)
                .eq("user_id", userId)
                .last("limit 1"));

        Assert.notNull(couponList, "优惠券不可用");
        if (TimeUtils.timestamp() > couponList.getInvalidTime()
                || couponList.getStatus() != CouponEnum.USE_STATUS_NOT.getCode()) {
            throw new OperateException("优惠券已失效");
        }

        // 优惠券
        Coupon coupon = couponMapper.selectById(couponList.getCouponId());
        Assert.notNull(coupon, "优惠券不可用");

        // 优惠券 商品id
        List<Integer> couponGoodsIds = new ArrayList<>();
        if (StringUtils.isNoneBlank(coupon.getUseGoodsIds())) {
            couponGoodsIds = ListUtils.stringToListAsInt(coupon.getUseGoodsIds(), ",");
        }

        // 可参与优惠券优惠的商品金额
        BigDecimal ableDiscountMoney = BigDecimal.ZERO;
        // 可参与优惠券的商品ID
        List<Integer> ableGoodsIds = new ArrayList<>();
        for (OrderGoodsInfoVo goodsInfoVo : orderGoodsList) {
            if (goodsInfoVo.getBuyAble().equals(false)) {
                continue;
            }
            // 指定商品可用
            if (coupon.getUseGoodsType() == CouponEnum.USE_GOODS_TYPE_ALLOW.getCode()
                    && !couponGoodsIds.contains(goodsInfoVo.getGoodsId())) {
                continue;
            }
            // 指定商品不可用
            if (coupon.getUseGoodsType() == CouponEnum.USE_GOODS_TYPE_BAN.getCode()
                    && couponGoodsIds.contains(goodsInfoVo.getGoodsId())) {
                continue;
            }
            ableGoodsIds.add(goodsInfoVo.getGoodsId());
            ableDiscountMoney = ableDiscountMoney.add(goodsInfoVo.getGoodsAmount());
        }

        if (coupon.getMoney().compareTo(BigDecimal.ZERO) <= 0 || ableDiscountMoney.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperateException("优惠券不可用");
        }

        // 满金额使用
        if (coupon.getConditionType() == CouponEnum.CONDITION_TYPE_FULL.getCode()
                && ableDiscountMoney.compareTo(coupon.getConditionMoney()) < 0) {
            throw new OperateException("当前商品不满足优惠券使用金额");
        }

        for (int i = 0; i < orderGoodsList.size(); i++) {
            if (orderGoodsList.get(i).getBuyAble().equals(false)) {    // 不购买条件，不参与
                continue;
            }
            // 非可用商品
            if (!ableGoodsIds.contains(orderGoodsList.get(i).getGoodsId())) {
                continue;
            }

            // 订单商品可获得优惠金额
            BigDecimal itemDiscountMoney = orderGoodsList.get(i).getGoodsAmount().divide(ableDiscountMoney, 2, RoundingMode.HALF_UP).multiply(coupon.getMoney());

            // 当前是否为最后一个商品
            if (i == orderGoodsList.size() - 1) {
                itemDiscountMoney = coupon.getMoney().subtract(totalDiscountMoney);
            }

            // 当前可获得的优惠大于订单商品时
            if (itemDiscountMoney.compareTo(orderGoodsList.get(i).getGoodsAmount()) > 0) {
                itemDiscountMoney = orderGoodsList.get(i).getGoodsAmount();
            }

            orderGoodsList.get(i).setCouponMoney(itemDiscountMoney);

            totalDiscountMoney = totalDiscountMoney.add(itemDiscountMoney);
        }
        return totalDiscountMoney;
    }

    /**
     * 提交订单
     *
     * @param settleResultVo OrderSettleResultVo
     * @param userId         Integer
     * @return SubmitOrderResultVo
     * @author mjf
     */
    @Transactional
    public Order addOrder(OrderSettleResultVo settleResultVo, Integer userId) {
        // 订单商品信息
        List<OrderGoodsInfoVo> orderGoodsList = settleResultVo.getGoods();
        // 用户地址
        UserAddressVo userAddress = settleResultVo.getAddress();
        Assert.notNull(userAddress, "请选择收货地址");

        // 订单编号
        String orderSn = orderMapper.randMakeOrderSn("order_sn");

        // 订单
        Order order = new Order();
        order.setOrderSn(orderSn);
        order.setUserId(userId);
        order.setOrderSource(LikeFrontThreadLocal.getTerminal());
        order.setOrderType(settleResultVo.getOrderType());
        order.setOrderStatus(OrderEnum.ORDER_STATUS_WAIT_PAY.getCode());
        order.setAddress(JSONArray.toJSONString(userAddress));
        order.setAddressContact(userAddress.getContact());
        order.setAddressMobile(userAddress.getMobile());
        order.setAddressContent(userAddress.getAddressDetail());
        order.setMoney(settleResultVo.getTotalAmount());
        order.setGoodsMoney(settleResultVo.getGoodsAmount());
        order.setCouponMoney(settleResultVo.getCouponAmount());
        order.setExpressMoney(settleResultVo.getFreightAmount());
        order.setNeedPayMoney(settleResultVo.getOrderAmount());
        order.setGoodsNum(settleResultVo.getTotalNum());
        order.setUserRemark(settleResultVo.getRemark() != null ? settleResultVo.getRemark() : "");
        order.setSeckillId(settleResultVo.getSeckillId());
        order.setUpdateTime(System.currentTimeMillis() / 1000);
        order.setCreateTime(System.currentTimeMillis() / 1000);
        order.setCouponListId(settleResultVo.getCouponListId() == null ? 0 : settleResultVo.getCouponListId());
        order.setDeliveryType(settleResultVo.getDeliveryType());

        if (order.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_PICK.getCode())) {
            order.setExpressIs(DeliverEnum.SHIPPED.getCode());
            order.setExpressTime(System.currentTimeMillis() / 1000);
            order.setVerificationStatus(OrderGoodsEnum.VERIFYSTATUS_FALSE.getCode());
            order.setVerificationTime(0L);
            order.setSelffetchShopId(settleResultVo.getSelffetchShopId());
            order.setPickupCode(buildPickUpCode("pickup_code"));
        }
        orderMapper.insert(order);

        for (OrderGoodsInfoVo orderGoodsListVo : orderGoodsList) {
            OrderGoods orderGoods = new OrderGoods();
            orderGoods.setUserId(userId);
            orderGoods.setOrderId(order.getId());
            orderGoods.setGoodsType(orderGoodsListVo.getGoodsType());
            orderGoods.setGoodsId(orderGoodsListVo.getGoodsId());
            orderGoods.setGoodsName(orderGoodsListVo.getGoodsName());
            orderGoods.setGoodsImage(UrlUtils.toRelativeUrl(orderGoodsListVo.getImage()));
            orderGoods.setGoodsCode(orderGoodsListVo.getGoodsCode());
            orderGoods.setGoodsSkuId(orderGoodsListVo.getGoodsSkuId());
            orderGoods.setGoodsSkuValue(orderGoodsListVo.getSkuValueArr());
            // 商品单价
            orderGoods.setGoodsOriginalPrice(orderGoodsListVo.getOriginalPrice());
            orderGoods.setGoodsPrice(orderGoodsListVo.getPrice());
            orderGoods.setGoodsNum(orderGoodsListVo.getNum());
            // 商品总额
            orderGoods.setGoodsMoney(orderGoodsListVo.getGoodsAmount());
            // 商品运费
            orderGoods.setExpressMoney(orderGoodsListVo.getExpressMoney());
            // 优惠金额
            orderGoods.setCouponMoney(orderGoodsListVo.getCouponMoney());
            // 订单总额 商品总额 + 运费
            orderGoods.setMoney(orderGoodsListVo.getGoodsAmount().add(orderGoodsListVo.getExpressMoney()));
            // 商品应付总额 商品总额 + 运费
            orderGoods.setNeedPayMoney(orderGoodsListVo.getGoodsAmount().add(orderGoodsListVo.getExpressMoney()).subtract(orderGoodsListVo.getCouponMoney()));
            orderGoods.setAfterSale(OrderGoodsEnum.AFTER_STATUS_NO.getCode());
            orderGoods.setCreateTime(System.currentTimeMillis() / 1000);
            orderGoods.setUpdateTime(System.currentTimeMillis() / 1000);
            orderGoodsMapper.insert(orderGoods);
        }

        // 订单日志
        logOrderMapper.add(order.getId(),
                OrderLogEnum.TYPE_USER.getCode(),
                OrderLogEnum.CHANNEL_ADD_ORDER.getCode(),
                userId, OrderLogEnum.CHANNEL_ADD_ORDER.getMsg());

        return order;
    }

    /**
     * 删除购物车
     *
     * @param buyType          String
     * @param buyGoodsValidate List<BuyGoodsValidate>
     * @author mjf
     */
    private void __deleteCart(String buyType, List<BuyGoodsValidate> buyGoodsValidate) {
        if (buyType.equals("cart")) {
            for (BuyGoodsValidate buyGoods : buyGoodsValidate) {
                Cart cart = new Cart();
                cart.setId(buyGoods.getCartId());
                cart.setIsDelete(1);
                cart.setUpdateTime(System.currentTimeMillis() / 1000);
                cartMapper.updateById(cart);
            }
        }
    }

    /**
     * 扣除库存
     *
     * @param orderGoodsList List<OrderGoodsInfoVo>
     * @author mjf
     */
    private void __updateGoodsStock(List<OrderGoodsInfoVo> orderGoodsList) {
        int inventoryOccupancy = Integer.parseInt(ConfigUtils.get("trade", "inventoryOccupancy", "1"));
        // 订单提交时占用库存
        if (inventoryOccupancy == 1) {
            for (OrderGoodsInfoVo orderGoodsVo : orderGoodsList) {
                // 扣除商品规格库存
                goodsSkuMapper.update(null, new UpdateWrapper<GoodsSku>()
                        .eq("id", orderGoodsVo.getGoodsSkuId())
                        .setSql("stock = IF(stock-" + orderGoodsVo.getNum() + "<0, 0, stock-" + orderGoodsVo.getNum() + ")"));
                // 扣除商品总库存
                goodsMapper.update(null, new UpdateWrapper<Goods>()
                        .eq("id", orderGoodsVo.getGoodsId())
                        .setSql("total_stock = IF(total_stock-" + orderGoodsVo.getNum() + "<0, 0, total_stock-" + orderGoodsVo.getNum() + ")"));
            }
        }
    }

    /**
     * 更新优惠券记录
     *
     * @param couponListId Integer
     * @param orderId      Integer
     * @param userId       Integer
     * @author mjf
     */
    private void __updateCouponList(Integer couponListId, Integer orderId, Integer userId) {
        if (couponListId != null && couponListId > 0) {
            CouponList couponList = couponListMapper.selectOne(new QueryWrapper<CouponList>()
                    .eq("id", couponListId)
                    .eq("user_id", userId)
                    .last("limit 1"));

            if (couponList != null) {
                couponList.setOrderId(orderId);
                couponList.setStatus(CouponEnum.USE_STATUS_OK.getCode());
                couponList.setUseTime(System.currentTimeMillis() / 1000);
                couponList.setUpdateTime(System.currentTimeMillis() / 1000);
                couponListMapper.updateById(couponList);
            }
        }
    }

    /**
     * 支付按钮
     *
     * @param order Order
     * @return Integer
     * @author mjf
     */
    private Integer __payBtn(Order order) {
        int btn = 0;
        if (order.getOrderStatus() == OrderEnum.ORDER_STATUS_WAIT_PAY.getCode()) {
            btn = 1;
        }
        return btn;
    }

    /**
     * 取消按钮
     *
     * @param order Order
     * @return Integer
     * @author mjf
     */
    private Integer __cancelBtn(Order order) {
        int btn = 0;
        // 未支付
        if (order.getOrderStatus() == OrderEnum.ORDER_STATUS_WAIT_PAY.getCode()
                || order.getOrderStatus() == OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode()) {
            btn = 1;
        }
        // 待发货
        if (OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode() == order.getOrderStatus()) {
            // 订单允许取消时间
            float cancelTimeConfig = Float.parseFloat(ConfigUtils.get("trade", "cancelUnshippedOrderTime", "-1"));
            if (cancelTimeConfig == -1) {
                btn = 0;
            } else {
                if (TimeUtils.timestamp() > (order.getPayTime() + (long) (cancelTimeConfig * 60))) {
                    btn = 0;
                }
            }
        }
        // 到店自取在取货前和有效时间内都可以取消
        if (order.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_PICK.getCode())) {
            btn = 0;
            float cancelTimeConfig = Float.parseFloat(ConfigUtils.get("trade", "cancelUnshippedOrderTime", "-1"));
            if (order.getOrderStatus().equals(OrderEnum.ORDER_STATUS_PICKUP_DELIVER.getCode())) { //待取货阶段
                if (cancelTimeConfig == -1) {
                    btn = 1;
                }
                if (TimeUtils.timestamp() < (order.getPayTime() + (long) (cancelTimeConfig * 60))) {
                    btn = 1;
                }
            }
        }
        return btn;
    }

    /**
     * 确定按钮
     *
     * @param order Order
     * @return Integer
     * @author mjf
     */
    private Integer __confirmBtn(Order order) {
        int btn = 0;
        if (OrderEnum.ORDER_STATUS_TAKE_DELIVER.getCode() == order.getOrderStatus()) {
            btn = 1;
        }
        return btn;
    }

    /**
     * 删除按钮
     *
     * @param order Order
     * @return Integer
     * @author mjf
     */
    private Integer __deleteBtn(Order order) {
        int btn = 0;
        if (OrderEnum.ORDER_STATUS_COMPLETED.getCode() == order.getOrderStatus() ||
                OrderEnum.ORDER_STATUS_CANCEL.getCode() == order.getOrderStatus()) {
            btn = 1;
        }
        return btn;
    }

    /**
     * 查看物流按钮
     *
     * @param order Order
     * @return Integer
     * @author mjf
     */
    private Integer __logisticsBtn(Order order) {
        int btn = 0;
        if (OrderEnum.ORDER_STATUS_TAKE_DELIVER.getCode() == order.getOrderStatus()
                && 1 == order.getExpressIs()) {
            btn = 1;
        }
        return btn;
    }

    /**
     * 申请退款按钮
     *
     * @param order Order
     * @return Integer
     * @author mjf
     */
    private Integer __refundBtn(Order order) {
        int btn = 0;
        if (OrderEnum.ORDER_STATUS_COMPLETED.getCode() == order.getOrderStatus()
                && order.getAfterDeadline() > TimeUtils.timestamp()) {
            // 查询是否有发生售后，没发生则 可显示申请退款按钮
            OrderAfter orderAfter = orderAfterMapper.selectOne(new QueryWrapper<OrderAfter>()
                    .in("after_status", Arrays.asList(OrderAfterEnum.AFTER_STATUS_ING.getCode(), OrderAfterEnum.AFTER_STATUS_SUCCESS.getCode()))
                    .eq("order_id", order.getId())
                    .last("limit 1"));

            if (orderAfter == null) {
                btn = 1;
            }
        }
        return btn;
    }

    /**
     * 评价按钮
     *
     * @param order Order
     * @return Integer
     * @author mjf
     */
    private Integer __commentBtn(Order order) {
        int btn = 0;
        if (OrderEnum.ORDER_STATUS_COMPLETED.getCode() == order.getOrderStatus() && 1 == order.getPayIs()) {
            btn = 1;
            // 查询订单商品
            List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>()
                    .select("id,is_comment")
                    .eq("order_id", order.getId()));
            int commentCount = 0;
            for (OrderGoods orderGoods : orderGoodsList) {
                if (orderGoods.getIsComment() == 1) {
                    commentCount += 1;
                }
            }
            if (commentCount == orderGoodsList.size()) {
                btn = 0;
            }
        }
        return btn;
    }

    /**
     * 订单商品售后按钮
     *
     * @param order        Order
     * @param orderGoodsId Integer
     * @return Integer
     * @author mjf
     */
    private Integer __afterApplyBtn(Order order, Integer orderGoodsId) {
        int btn = 0;
        // 待支付，已取消 不显示
        if (order.getOrderStatus() == OrderEnum.ORDER_STATUS_WAIT_PAY.getCode()
                || order.getOrderStatus() == OrderEnum.ORDER_STATUS_CANCEL.getCode()) {
            return btn;
        }

        // 订单是否可以取消,是否在取消时间内
        float cancelTimeConfig = Float.parseFloat(ConfigUtils.get("trade", "cancelUnshippedOrderTime", "-1"));
        if (cancelTimeConfig != -1 && order.getOrderStatus() < OrderEnum.ORDER_STATUS_COMPLETED.getCode()) {
            // 未过可取消时间
            if (TimeUtils.timestamp() < (order.getPayTime() + (long) (cancelTimeConfig * 60))) {
                return btn;
            }
        }

        // 当前是否在售后时间内(买家售后维权时效: -1=关闭,否则开启(天))
        float afterSalesDay = Float.parseFloat(ConfigUtils.get("trade", "afterSalesDay", "-1"));
        long ableAfterSalesTime = afterSalesDay != -1 ? (long) (afterSalesDay * 24 * 60 * 60) : 0;
        if (order.getConfirmTime() > 0 && TimeUtils.timestamp() > (order.getConfirmTime() + ableAfterSalesTime)) {
            return btn;
        }
        // 查询是否已申请售后售后
        OrderAfterGoods orderAfterGoods = orderAfterGoodsMapper.selectOne(new QueryWrapper<OrderAfterGoods>()
                .eq("order_goods_id", orderGoodsId)
                .last("limit 1"));
        if (orderAfterGoods == null) {
            btn = 1;
        } else {
            OrderAfter orderAfter = orderAfterMapper.selectById(orderAfterGoods.getOrderAfterId());
            if (orderAfter.getAfterStatus() == OrderAfterEnum.AFTER_STATUS_CLOSE.getCode()) {
                btn = 1;
            }
        }
        return btn;
    }

    /**
     * 订单商品售后描述
     *
     * @param order        Order
     * @param orderGoodsId Integer
     * @return String
     * @author mjf
     */
    private String __afterApplyMsg(Order order, Integer orderGoodsId) {
        String msg = "";
        // 查询是否已申请售后售后
        OrderAfterGoods orderAfterGoods = orderAfterGoodsMapper.selectOne(new QueryWrapper<OrderAfterGoods>()
                .eq("order_goods_id", orderGoodsId)
                .orderByDesc("id")
                .last("limit 1"));

        if (orderAfterGoods != null) {
            // 售后状态
            OrderAfter orderAfter = orderAfterMapper.selectOne(new QueryWrapper<OrderAfter>()
                    .eq("id", orderAfterGoods.getOrderAfterId())
                    .last("limit 1"));
            msg = OrderAfterEnum.getAfterStatusMsg(orderAfter.getAfterStatus());

            // 订单取消不显示售后描述
            if (order.getOrderStatus().equals(OrderEnum.ORDER_STATUS_CANCEL.getCode())) {
                msg = "";
            }
        }
        return msg;
    }

    /**
     * 是否依然在售后中
     * @param order        Order
     * @param orderGoodsId Integer
     * @return String
     * @author mjf
     */
    private Integer __afterApplyStatus(Order order, Integer orderGoodsId) {
        Integer ret = 0;
        // 查询是否已申请售后售后
        OrderAfterGoods orderAfterGoods = orderAfterGoodsMapper.selectOne(new QueryWrapper<OrderAfterGoods>()
                .eq("order_goods_id", orderGoodsId)
                .orderByDesc("id")
                .last("limit 1"));

        if (orderAfterGoods != null) {
            // 售后状态
            OrderAfter orderAfter = orderAfterMapper.selectOne(new QueryWrapper<OrderAfter>()
                    .eq("id", orderAfterGoods.getOrderAfterId())
                    .last("limit 1"));
            ret = orderAfter.getAfterStatus();
            // 订单取消不显示售后描述
            if (order.getOrderStatus().equals(OrderEnum.ORDER_STATUS_CANCEL.getCode())) {
                ret = 0;
            }
        }
        return ret;
    }

    /**
     * 售后时间
     *
     * @return Long
     * @author mjf
     */
    private Long __getAfterSaleDeadline() {
        float afterSalesDay = Float.parseFloat(ConfigUtils.get("trade", "afterSalesDay", "-1"));
        long afterSaleDeadline = TimeUtils.timestamp();
        if (afterSalesDay != -1) {
            afterSaleDeadline = (long) (afterSalesDay * 24 * 60 * 60) + TimeUtils.timestamp();
        }
        return afterSaleDeadline;
    }

    /**
     * 取消订单
     *
     * @param order Order
     * @author mjf
     */
    private void __handleCancelOrder(Order order) {
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
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
                    orderAfter.setRefundMoney(order.getPayMoney());
                    orderAfter.setRefundRemark("系统发起整单退款");
                    orderAfter.setAfterStatus(OrderAfterEnum.AFTER_STATUS_ING.getCode());
                    orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_ING_SELLER_REFUND_ING.getCode());
                    orderAfter.setCreateTime(System.currentTimeMillis() / 1000);
                    orderAfterMapper.insert(orderAfter);

                    // 写入售后商品
                    List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>().eq("order_id", order.getId()));
                    for (OrderGoods item : orderGoodsList) {
                        OrderAfterGoods afterGoods = new OrderAfterGoods();
                        afterGoods.setOrderId(item.getOrderId());
                        afterGoods.setOrderAfterId(orderAfter.getId());
                        afterGoods.setOrderGoodsId(item.getId());
                        afterGoods.setGoodsId(item.getGoodsId());
                        afterGoods.setGoodsSkuId(item.getGoodsSkuId());
                        afterGoods.setGoodsNum(item.getGoodsNum());
                        afterGoods.setGoodsPrice(item.getGoodsPrice());
                        afterGoods.setRefundMoney(item.getPayMoney());
                        afterGoods.setCreateTime(System.currentTimeMillis() / 1000);
                        afterGoods.setUpdateTime(System.currentTimeMillis() / 1000);
                        orderAfterGoodsMapper.insert(afterGoods);
                    }

                    // 生成售后日志
                    Integer roleSeller = LogOrderAfterEnum.TYPE_USER.getCode();
                    logOrderAfterMapper.add(roleSeller, order.getUserId(), orderAfter.getId(), LogOrderAfterEnum.BUYER_CANCEL_ORDER.getMsg());

                    // 生成退款记录
                    String refundSn = refundRecordMapper.randMakeOrderSn("sn");
                    refundRecord = new RefundRecord();
                    refundRecord.setSn(refundSn);
                    refundRecord.setOrderSn(order.getOrderSn());
                    refundRecord.setOrderId(order.getId());
                    refundRecord.setUserId(order.getUserId());
                    refundRecord.setRefundType(RefundEnum.TYPE_USER.getCode());
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
                    refundLog.setHandleId(0);
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

                            logMoneyMapper.add(
                                    user.getId(),
                                    LogMoneyEnum.BNW_INC_CANCEL_ORDER.getCode(),
                                    order.getPayMoney(),
                                    order.getId(),
                                    order.getOrderSn(),
                                    "售后退还余额!",
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

            // 订单状态-更新为已关闭
            order.setOrderStatus(OrderEnum.ORDER_STATUS_CANCEL.getCode());
            order.setCancelTime(System.currentTimeMillis() / 1000);
            order.setUpdateTime(System.currentTimeMillis() / 1000);
            orderMapper.updateById(order);

            // 订单日志
            Integer channel = OrderLogEnum.CHANNEL_USER_CANCEL_ORDER.getCode();
            logOrderMapper.add(
                    order.getId(),
                    OrderLogEnum.TYPE_USER.getCode(),
                    channel,
                    order.getUserId(),
                    OrderLogEnum.getValue(channel));

            // 退还库存
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

            // 没有支付的订单取消返回优惠券
            int returnCoupon = Integer.parseInt(ConfigUtils.get("trade", "returnCoupon", "0"));
            if (order.getPayIs() == 0 && order.getCouponListId() > 0 && returnCoupon == 1) {
                this.__returnCoupon(order.getCouponListId());
            }

            // 相关分销订单更新为失效
            DistributionOrder distributionOrder = new DistributionOrder();
            distributionOrder.setStatus(DistributionEnum.ORDER_EXPIRED.getCode());
            distributionOrder.setUpdateTime(System.currentTimeMillis() / 1000);
            distributionOrderMapper.update(distributionOrder, new QueryWrapper<DistributionOrder>()
                    .eq("order_id", order.getId()));

            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            transactionManager.rollback(transactionStatus);
            throw new OperateException(e.getMessage());
        }
    }

    /**
     * 退回优惠券
     *
     * @param couponListId Integer
     * @author mjf
     */
    private void __returnCoupon(Integer couponListId) {
        if (couponListId == null || couponListId < 0) {
            return;
        }

        CouponList couponList = couponListMapper.selectById(couponListId);
        if (couponList == null) {
            return;
        }

        Coupon coupon = couponMapper.selectById(couponList.getCouponId());
        if (coupon == null) {
            return;
        }

        // 优惠券活动关闭
        if (!coupon.getStatus().equals(CouponEnum.COUPON_STATUS_CONDUCT.getCode())) {
            return;
        }

        // 作废旧优惠券
        couponList.setStatus(CouponEnum.USE_STATUS_VOID.getCode());
        couponList.setUpdateTime(System.currentTimeMillis() / 1000);
        couponListMapper.updateById(couponList);

        String couponCode = couponListMapper.randMakeOrderSn("coupon_code");
        // 发放新优惠券
        CouponList couponListNew = new CouponList();
        couponListNew.setChannel(couponList.getChannel());
        couponListNew.setCouponCode(couponCode);
        couponListNew.setIssuerId(0);
        couponListNew.setCouponId(couponList.getCouponId());
        couponListNew.setOrderId(0);
        couponListNew.setUserId(couponList.getUserId());
        couponListNew.setStatus(CouponEnum.USE_STATUS_NOT.getCode());
        couponListNew.setInvalidTime(couponList.getInvalidTime());
        couponListNew.setCreateTime(System.currentTimeMillis() / 1000);
        couponListNew.setUpdateTime(System.currentTimeMillis() / 1000);
        couponListMapper.insert(couponListNew);
    }

    /**
     * 校验能否参与秒杀
     *
     * @param buyGoods BuyGoodsValidate
     * @return SeckillGoodsSku
     * @author mjf
     */
    private SeckillGoodsSku __getSeckillGoodsSku(BuyGoodsValidate buyGoods) {
        // 秒杀活动
        SeckillActivity seckillActivity = seckillActivityMapper.selectOne(new QueryWrapper<SeckillActivity>()
                .eq("id", buyGoods.getSeckillId())
                .eq("status", SeckillEnum.STATUS_CONDUCT.getCode())
                .le("start_time", TimeUtils.timestamp())
                .ge("end_time", TimeUtils.timestamp())
                .eq("is_delete", 0)
                .last("limit 1"));

        Assert.notNull(seckillActivity, "秒杀活动已结束");

        SeckillGoodsSku seckillGoodsSku = seckillGoodsSkuMapper.selectOne(new QueryWrapper<SeckillGoodsSku>()
                .eq("seckill_id", buyGoods.getSeckillId())
                .eq("sku_id", buyGoods.getSkuId())
                .last("limit 1"));

        Assert.notNull(seckillGoodsSku, "秒杀商品信息不存在");

        if (seckillActivity.getLimitStatus() > 0 && buyGoods.getNum() > seckillActivity.getMaxBuy()) {
            throw new OperateException("单笔订单数量不能大于" + seckillActivity.getMaxBuy() + "件");
        }

        return seckillGoodsSku;
    }

    // 设置自提码
    private String buildPickUpCode(String field) {
        // 字母
        List<String> letterAll = new LinkedList<>();
//        for (char ch = 'A'; ch <= 'Z'; ch++) {
//            String s = String.valueOf(ch);
//            if (Arrays.asList("I", "O").contains(s)) {
//                continue;
//            }
//            letterAll.add(s);
//        }

        // 数字
        for (char ch = '2'; ch <= '9'; ch++) {
            String s = String.valueOf(ch);
            letterAll.add(s);
        }
        StringBuilder randCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            randCode.append(letterAll.get(random.nextInt(letterAll.size())));
        }

        String code = randCode.toString();
        while (true) {
            Order order = orderMapper.selectOne(
                    new QueryWrapper<Order>()
                            .select("id")
                            .eq(field, code)
                            .eq("verification_status", OrderGoodsEnum.VERIFYSTATUS_FALSE.getCode())
                            .last("limit 1"));
            if (order == null) {
                break;
            } else {
                StringBuilder randCodeRand = new StringBuilder();
                Random random2 = new Random();
                for (int i = 0; i < 6; i++) {
                    randCodeRand.append(letterAll.get(random2.nextInt(letterAll.size())));
                }
                code = randCodeRand.toString();
            }
        }
        return code;
    }

    /**
     * 订单列表
     *
     * @param pageValidate PageValidate
     * @param status       Integer
     * @return PageResult<OrderListVo>
     * @author mjf
     */
    @Override
    public PageResult<OrderListVo> selffetchlist(PageValidate pageValidate, SelffetchOrderSearchValidate selffetchOrderSearchValidate) {
        // 分页
        //Integer userId = LikeFrontThreadLocal.getUserId();
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        // 订单状态
//        if (StringUtils.isNotNull(selffetchOrderSearchValidate.getOrderStatus())) {
//            orderQueryWrapper.eq("order_status", selffetchOrderSearchValidate.getOrderStatus());
//        }
        orderQueryWrapper.gt("order_status", OrderEnum.ORDER_STATUS_WAIT_PAY.getCode());
        orderQueryWrapper.ne("order_status", OrderEnum.ORDER_STATUS_CANCEL.getCode());
        if (StringUtils.isNotNull(selffetchOrderSearchValidate.getDeliveryType())) {
            orderQueryWrapper.eq("delivery_type", selffetchOrderSearchValidate.getDeliveryType());
        }
        if (StringUtils.isNotNull(selffetchOrderSearchValidate.getVerificationStatus())) {
            orderQueryWrapper.eq("verification_status", selffetchOrderSearchValidate.getVerificationStatus());
        }
        if (StringUtils.isNotNull(selffetchOrderSearchValidate.getCurrUserId())) { //根据用户查找
            List<Integer> shopIds = iSelffetchVerifierService.getShopIds(selffetchOrderSearchValidate.getCurrUserId());
            if (shopIds.size() > 0) {
                orderQueryWrapper.in("selffetch_shop_id", shopIds);
            } else {
                orderQueryWrapper.eq("id", -1);
            }
        }
        //判断在该
        orderQueryWrapper.eq("is_delete", 0)
                //.eq("user_id", userId)
                .orderByDesc("id");
        IPage<Order> iPage = orderMapper.selectPage(new Page<>(pageNo, pageSize), orderQueryWrapper);
        // 处理数据
        List<OrderListVo> orderList = new ArrayList<>();
        for (Order record : iPage.getRecords()) {
            OrderListVo orderListVo = new OrderListVo();
            BeanUtils.copyProperties(record, orderListVo);
            // 订单商品
            List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>().
                    eq("order_id", record.getId())
                    .select("id,goods_id,goods_name,goods_sku_id,goods_sku_value,goods_image,goods_num,goods_price," +
                            "goods_original_price,need_pay_money,goods_money,verification_status,verification_time"));
            List<OrderGoodsListVo> orderGoodsListVos = new ArrayList<>();
            for (OrderGoods orderGoods : orderGoodsList) {
                orderGoods.setGoodsImage(UrlUtils.toAbsoluteUrl(orderGoods.getGoodsImage()));
                OrderGoodsListVo orderGoodsListVo = new OrderGoodsListVo();
                BeanUtils.copyProperties(orderGoods, orderGoodsListVo);
                orderGoodsListVos.add(orderGoodsListVo);
            }
            // 订单商品信息
            orderListVo.setOrderGoodsList(orderGoodsListVos);
            // 订单信息
            orderListVo.setOrderStatus(record.getOrderStatus());
            orderListVo.setOrderStatusMsg(OrderEnum.getOrderStatusMsg(record.getOrderStatus()));
            orderListVo.setCancelTime(getCancelUnpaidOrderTime(record.getCreateTime()));
            // 订单按钮
            orderListVo.setPayBtn(__payBtn(record));
            orderListVo.setCancelBtn(__cancelBtn(record));
            orderListVo.setConfirmBtn(__confirmBtn(record));
            orderListVo.setCommentBtn(__commentBtn(record));
            orderListVo.setDeleteBtn(__deleteBtn(record));
            orderListVo.setRefundBtn(__refundBtn(record));
            orderListVo.setLogisticsBtn(__logisticsBtn(record));
            orderListVo.setVerificationStatusStr(OrderEnum.getVerificationStatusMsg(record.getVerificationStatus()));
            orderList.add(orderListVo);
        }
        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), orderList);
    }

    /**
     * 到店自提订单详情
     *
     * @param id Integer
     * @return OrderDetailVo
     * @author mjf
     */
    @Override
    public SelffetchOrderDetailVo selffetchDetail(Integer id) {
        Integer currUserId = LikeFrontThreadLocal.getUserId();
        // 订单
        QueryWrapper orderQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotNull(currUserId)) { //根据用户查找
            List<Integer> shopIds = iSelffetchVerifierService.getShopIds(currUserId);
            if (shopIds.size() > 0) {
                orderQueryWrapper.in("selffetch_shop_id", shopIds);
            } else {
                orderQueryWrapper.eq("id", -1);
            }
        }
        orderQueryWrapper.eq("id", id);

        orderQueryWrapper.eq("is_delete", 0);
        orderQueryWrapper.last("limit 1");
        Order order = orderMapper.selectOne(orderQueryWrapper);
        Assert.notNull(order, "订单信息不存在");

        // 订单商品
        List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>()
                .eq("order_id", order.getId()));

        // 订单商品处理
        List<OrderGoodsListVo> orderGoodsListVoList = new ArrayList<>();
        for (OrderGoods orderGoods : orderGoodsList) {
            OrderGoodsListVo orderGoodsListVo = new OrderGoodsListVo();
            BeanUtils.copyProperties(orderGoods, orderGoodsListVo);
            orderGoodsListVo.setGoodsImage(UrlUtils.toAbsoluteUrl(orderGoods.getGoodsImage()));
            orderGoodsListVo.setAfterSalesBtn(this.__afterApplyBtn(order, orderGoods.getId()));
            orderGoodsListVo.setAfterSalesMsg(this.__afterApplyMsg(order, orderGoods.getId()));
            orderGoodsListVo.setAfterSalesStatus(this.__afterApplyStatus(order, orderGoods.getId()));
            orderGoodsListVo.setVerificationStatus(orderGoods.getVerificationStatus());
            orderGoodsListVo.setVerificationStatusStr(OrderEnum.getVerificationStatusMsg(orderGoods.getVerificationStatus()));
            orderGoodsListVo.setVerificationTimeStr(orderGoods.getVerificationTime().equals(0L) ? "-" : TimeUtils.timestampToDate(orderGoods.getVerificationTime()));
            orderGoodsListVoList.add(orderGoodsListVo);
        }
        JSONObject addressJsonObject = JSONObject.parseObject(order.getAddress());

        // 订单信息处理
        SelffetchOrderDetailVo orderDetailVo = new SelffetchOrderDetailVo();
        BeanUtils.copyProperties(order, orderDetailVo);
        orderDetailVo.setAddressContact(addressJsonObject.getString("contact"));
        orderDetailVo.setAddressMobile(addressJsonObject.getLong("mobile"));
        orderDetailVo.setOrderTypeMsg(OrderEnum.getOrderTypeMsg(order.getOrderType()));
        orderDetailVo.setOrderGoodsList(orderGoodsListVoList);
        orderDetailVo.setCreateTime(TimeUtils.timestampToDate(order.getCreateTime()));

        String expressTime = order.getExpressTime() > 0 ? TimeUtils.timestampToDate(order.getExpressTime()) : "-";
        String payTime = order.getPayTime() > 0 ? TimeUtils.timestampToDate(order.getPayTime()) : "-";
        String confirmTime = order.getConfirmTime() > 0 ? TimeUtils.timestampToDate(order.getConfirmTime()) : "-";
        orderDetailVo.setExpressTime(expressTime);
        orderDetailVo.setPayTime(payTime);
        orderDetailVo.setConfirmTime(confirmTime);

        orderDetailVo.setCancelTime(getCancelUnpaidOrderTime(order.getCreateTime()));
        orderDetailVo.setOrderStatus(order.getOrderStatus());
        orderDetailVo.setOrderStatusMsg(OrderEnum.getOrderStatusMsg(order.getOrderStatus()));

        // 订单按钮
        orderDetailVo.setPayBtn(__payBtn(order));
        orderDetailVo.setCancelBtn(__cancelBtn(order));
        orderDetailVo.setConfirmBtn(__confirmBtn(order));
        orderDetailVo.setDeleteBtn(__deleteBtn(order));
        orderDetailVo.setCommentBtn(__commentBtn(order));
        orderDetailVo.setLogisticsBtn(__logisticsBtn(order));
        orderDetailVo.setRefundBtn(__refundBtn(order));
        orderDetailVo.setDeliveryTypeStr(DeliverEnum.getDeliverTypeMsg(order.getDeliveryType()));
        orderDetailVo.setPickupCode(order.getPickupCode());
        if (order.getSelffetchShopId().equals(0) == false) {
            SelffetchShopDetailVo selffetchShopDetailVo = iSelffetchShopService.detail(order.getSelffetchShopId());
            orderDetailVo.setSelffetchShop(selffetchShopDetailVo);
        }
        orderDetailVo.setVerificationStatus(order.getVerificationStatus());
        orderDetailVo.setVerificationStatusStr(OrderEnum.getVerificationStatusMsg(order.getVerificationStatus()));
        orderDetailVo.setVerificationTimeStr(order.getVerificationTime().equals(0L) ? "-" : TimeUtils.timestampToDate(order.getVerificationTime()));
        return orderDetailVo;
    }

    @Override
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

        com.baomidou.mybatisplus.core.toolkit.Assert.notNull(order, "订单不存在!");
        //判断单据是否属于自提类型
        if (order.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_PICK.getCode()) == false) {
            throw new OperateException("该订单不是自提订单，不能核销");
        }
        if (order.getOrderStatus().equals(OrderEnum.ORDER_STATUS_CANCEL.getCode())) {
            throw new OperateException("该订单已被取消，不能核销");
        }
        //判断当前用户是否可以核销
        if (iSelffetchVerifierService.verifyShopRights(order.getSelffetchShopId(), LikeFrontThreadLocal.getUserId()) == false) {
            throw new OperateException("当前账号没有核销该订单的权限");
        }

        //判断是否是否有可以自提
        for (Integer i = 0; i < items.size(); i++) {
            JSONObject itemJSON = JSONObject.parseObject(JSONObject.toJSONString(items.get(i)));
            if (StringUtils.isNull(itemJSON.getInteger("id"))) {
                throw new OperateException("核销商品格式错误，请重新提交");
            }
            OrderGoods orderGoods = iOrderGoodsService.detailByid(itemJSON.getInteger("id"));
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
            order.setVerificationBy(LikeFrontThreadLocal.getUserId());
            order.setVerificationByType(OrderEnum.VERIFY_BYTYPE_USER.getCode());

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
        verification.setHandleId(LikeFrontThreadLocal.getUserId());
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
    public SelffetchOrderPickuplVo pickCodeVerify(SelffetchOrderPickCodeVerifyValidate selffetchOrderPickCodeVerifyValidate) {
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        // 订单状态
        orderQueryWrapper.gt("order_status", OrderEnum.ORDER_STATUS_WAIT_PAY.getCode());
        orderQueryWrapper.eq("delivery_type", OrderEnum.DELIVERY_TYPE_PICK.getCode());
        orderQueryWrapper.eq("verification_status", OrderEnum.VERIFYSTATUS_FALSE.getCode());
        if (StringUtils.isNotNull(selffetchOrderPickCodeVerifyValidate.getCurrUserId())) { //根据用户查找
            List<Integer> shopIds = iSelffetchVerifierService.getShopIds(selffetchOrderPickCodeVerifyValidate.getCurrUserId());
            if (shopIds.size() > 0) {
                orderQueryWrapper.in("selffetch_shop_id", shopIds);
            } else {
                orderQueryWrapper.eq("id", -1);
            }
        }
        orderQueryWrapper.eq("pickup_code", selffetchOrderPickCodeVerifyValidate.getPickupCode());
        //判断在该
        orderQueryWrapper.eq("is_delete", 0)
                //.eq("user_id", userId)
                .orderByDesc("id");
        Order order = orderMapper.selectOne(orderQueryWrapper);
        if (StringUtils.isNull(order)) {
            throw new OperateException("找不到该核销信息");
        }
        SelffetchOrderPickuplVo selffetchOrderPickuplVo = new SelffetchOrderPickuplVo();
        selffetchOrderPickuplVo.setId(order.getId());
        selffetchOrderPickuplVo.setOrderSn(order.getOrderSn());
        selffetchOrderPickuplVo.setVerificationStatus(order.getVerificationStatus());
        selffetchOrderPickuplVo.setVerificationStatusStr(OrderEnum.getVerificationStatusMsg(order.getVerificationStatus()));
        selffetchOrderPickuplVo.setVerificationTimeStr(order.getVerificationTime().equals(0L) ? "-" : TimeUtils.timestampToDate(order.getVerificationTime()));

        return selffetchOrderPickuplVo;
    }

    /**
     * 检查是否所有单项都核销完毕
     */
    private Boolean checkPickupAll(Integer id) {
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

    /**
     * 判断是否可以购买
     */
    private Boolean setBuyAbleStatus(OrderGoodsInfoVo goodsInfoVo) {
        if (goodsInfoVo.getStatus() == 0) {
            return false;
            //throw new OperateException("商品:" + goodsInfoVo.getGoodsName() + " 已经下架，请选择商品下单");
        }
        if (goodsInfoVo.getNum() <= 0 || goodsInfoVo.getStock() < goodsInfoVo.getNum()) {
            return false;
            //throw new OperateException("商品:" + goodsInfoVo.getGoodsName() + " 当前库存仅剩:" + goodsInfoVo.getStock());
        }

        if (goodsInfoVo.getDeliveryAble().equals(false)) {
            return false;
        }
        return true;
    }

    /**
     * 不可以购买的理由
     */
    private JSONArray setBuyAbleStatusMsg(OrderGoodsInfoVo goodsInfoVo) {
        JSONArray ret = new JSONArray();
        if (goodsInfoVo.getStatus() == 0) {
            //ret.add("商品:" + goodsInfoVo.getGoodsName() + " 已经下架，请选择商品下单");
            ret.add("已经下架");
            //throw new OperateException("商品:" + goodsInfoVo.getGoodsName() + " 已经下架，请选择商品下单");
        }
        if (goodsInfoVo.getNum() <= 0 || goodsInfoVo.getStock() < goodsInfoVo.getNum()) {
            ret.add("库存仅剩:" + goodsInfoVo.getStock());
            //throw new OperateException("商品:" + goodsInfoVo.getGoodsName() + " 当前库存仅剩:" + goodsInfoVo.getStock());
        }

        if (goodsInfoVo.getDeliveryAble().equals(false)) {
            ret.add("不支持该配送方式");
            //return false;
        }
        return ret;
    }
}
