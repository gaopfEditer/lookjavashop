package com.mdd.front.controller;

import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.front.service.ICouponService;
import com.mdd.front.validate.common.IdValidate;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.coupon.CouponOrderSettleValidate;
import com.mdd.front.vo.coupon.CouponGoodsListVo;
import com.mdd.front.vo.coupon.CouponListVo;
import com.mdd.front.vo.coupon.CouponMyListVo;
import com.mdd.front.vo.coupon.CouponOrderResultVo;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/api/coupon")
@Api(tags = "优惠券管理")
public class CouponController {

    @Resource
    ICouponService iCouponService;

    @NotLogin
    @GetMapping("/list")
    @ApiOperation(value = "优惠券列表")
    public AjaxResult<PageResult<CouponListVo>> list(@Validated PageValidate pageValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        PageResult<CouponListVo> list = iCouponService.list(userId, pageValidate);
        return AjaxResult.success(list);
    }

    @PostMapping("/receive")
    @ApiOperation(value = "领取优惠券")
    public AjaxResult<Object> receive(@Validated @RequestBody IdValidate idValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        iCouponService.receive(userId, idValidate.getId());
        return AjaxResult.success();
    }

    @GetMapping("/myCoupon")
    @ApiOperation(value = "我的优惠券")
    public AjaxResult<PageResult<CouponMyListVo>> myCoupon(@Validated PageValidate pageValidate,
                                                           @RequestParam(value = "status", defaultValue = "0") Integer status) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        PageResult<CouponMyListVo> list = iCouponService.myCoupon(userId, pageValidate, status);
        return AjaxResult.success(list);
    }

    @NotLogin
    @GetMapping("/goodsCoupon")
    @ApiOperation(value = "商品页优惠券")
    public AjaxResult<List<CouponGoodsListVo>> goodsCoupon(@RequestParam(value = "goodsId", defaultValue = "0") Integer goodsId) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        List<CouponGoodsListVo> list = iCouponService.goodsCoupon(userId, goodsId);
        return AjaxResult.success(list);
    }

    @PostMapping("/orderCoupon")
    @ApiOperation(value = "订单结算优惠券")
    public AjaxResult<Object> orderCoupon(@Validated @RequestBody CouponOrderSettleValidate couponSettleValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        CouponOrderResultVo vo = iCouponService.orderCoupon(userId, couponSettleValidate);
        return AjaxResult.success(vo);
    }

}
