package com.mdd.front.service;

import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.coupon.CouponOrderSettleValidate;
import com.mdd.front.vo.coupon.CouponGoodsListVo;
import com.mdd.front.vo.coupon.CouponListVo;
import com.mdd.front.vo.coupon.CouponMyListVo;
import com.mdd.front.vo.coupon.CouponOrderResultVo;
import com.mdd.common.core.PageResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券服务类
 */
public interface ICouponService {

    /**
     * 优惠券列表(领券中心)
     *
     * @author mjf
     * @param userId Integer
     * @param pageValidate PageValidate
     * @return List<CouponListVo>
     */
    PageResult<CouponListVo> list(Integer userId, PageValidate pageValidate);

    /**
     * 领取优惠券
     *
     * @author mjf
     * @param userId Integer
     * @param id Integer
     */
    void receive(Integer userId, Integer id);

    /**
     * 我的优惠券
     *
     * @author mjf
     * @param userId Integer
     * @param pageValidate PageValidate
     * @param status Integer
     * @return PageResult<CouponMyListVo>
     */
    PageResult<CouponMyListVo> myCoupon(Integer userId, PageValidate pageValidate, Integer status);

    /**
     * 商品详情页优惠券
     *
     * @author mjf
     * @param userId Integer
     * @param goodsId Integer
     * @return List<CouponGoodsListVo>
     */
    List<CouponGoodsListVo> goodsCoupon(Integer userId, Integer goodsId);

    /**
     * 订单结算优惠券
     *
     * @author mjf
     * @param userId Integer
     * @param couponSettleValidate CouponOrderSettleValidate
     * @return CouponOrderResultVo
     */
    CouponOrderResultVo orderCoupon(Integer userId, CouponOrderSettleValidate couponSettleValidate);

    /**
     * 优惠券条件描述
     *
     * @author mjf
     * @param conditionType Integer
     * @param conditionMoney BigDecimal
     * @return String
     */
    String getConditionMsg(Integer conditionType, BigDecimal conditionMoney);
}
