package com.mdd.admin.crontab;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mdd.common.entity.coupon.Coupon;
import com.mdd.common.entity.coupon.CouponList;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.entity.goods.GoodsSku;
import com.mdd.common.entity.order.Order;
import com.mdd.common.entity.order.OrderGoods;
import com.mdd.common.enums.CouponEnum;
import com.mdd.common.enums.OrderEnum;
import com.mdd.common.enums.OrderLogEnum;
import com.mdd.common.enums.PaymentEnum;
import com.mdd.common.mapper.coupon.CouponListMapper;
import com.mdd.common.mapper.coupon.CouponMapper;
import com.mdd.common.mapper.goods.GoodsMapper;
import com.mdd.common.mapper.goods.GoodsSkuMapper;
import com.mdd.common.mapper.log.LogOrderMapper;
import com.mdd.common.mapper.order.OrderGoodsMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.TimeUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component("orderCloseJob")
public class OrderCloseJob {

    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderGoodsMapper orderGoodsMapper;

    @Resource
    GoodsMapper goodsMapper;

    @Resource
    GoodsSkuMapper goodsSkuMapper;

    @Resource
    CouponListMapper couponListMapper;

    @Resource
    CouponMapper couponMapper;

    @Resource
    LogOrderMapper logOrderMapper;

    public void handle() {
        try {
            // 系统自动取消时间
            float cancelTimeConfig = Float.parseFloat(ConfigUtils.get("trade", "cancelUnpaidOrderTime", "-1"));
            if (cancelTimeConfig == -1) {
                return;
            }

            float confirmTime = cancelTimeConfig * 60;
            List<Order> list = orderMapper.selectList(new QueryWrapper<Order>()
                    .eq("order_status", OrderEnum.ORDER_STATUS_WAIT_PAY.getCode())
                    .eq("pay_is", PaymentEnum.UN_PAID.getCode())
                    .lt("create_time + " + confirmTime, TimeUtils.timestamp()));

            if (list.isEmpty()) {
                return;
            }

            for (Order order : list) {
                // 退回订单商品库存
                returnInventory(order);
                // 如使用优惠券返回优惠券
                returnCoupon(order.getCouponListId());
                // 更新订单信息
                updateOrderStatus(order);
            }
        } catch (Exception ignored) {}
    }

    // 更新订单信息
    private void updateOrderStatus(Order order) {
        // 订单状态
        order.setOrderStatus(OrderEnum.ORDER_STATUS_CANCEL.getCode());
        order.setUpdateTime(System.currentTimeMillis() / 1000);
        order.setCancelTime(System.currentTimeMillis() / 1000);
        orderMapper.updateById(order);
        // 更新订单日志
        Integer channel = OrderLogEnum.CHANNEL_SYSTEM_CANCEL_ORDER.getCode();
        logOrderMapper.add(
                order.getId(),
                OrderLogEnum.TYPE_SYSTEM.getCode(),
                channel,
                0,
                OrderLogEnum.getValue(channel));
    }

    // 退回库存
    private void returnInventory(Order order) {
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
    }

    // 退回优惠券
    private void returnCoupon(Integer couponListId) {
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

}
