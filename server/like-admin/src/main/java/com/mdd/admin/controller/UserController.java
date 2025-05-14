package com.mdd.admin.controller;

import com.mdd.admin.LikeAdminThreadLocal;
import com.mdd.admin.aop.Log;
import com.mdd.admin.service.IUserService;
import com.mdd.admin.validate.user.*;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.user.InviterVo;
import com.mdd.admin.vo.user.UserVo;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.aop.NotPower;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/user")
@Api(tags = "用户数据管理")
public class UserController {

    @Resource
    IUserService iUserService;

    @GetMapping("/list")
    @ApiOperation(value="用户列表")
    @NotPower
    public AjaxResult<PageResult<UserVo>> list(@Validated PageValidate pageValidate,
                                               @Validated UserSearchValidate searchValidate) {
        PageResult<UserVo> list = iUserService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation(value="用户详情")
    public AjaxResult<UserVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        UserVo vo = iUserService.detail(id);
        return AjaxResult.success(vo);
    }

    @Log(title = "用户编辑")
    @PostMapping("/edit")
    @ApiOperation(value="用户编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody UserUpdateValidate updateValidate) {
        iUserService.edit(updateValidate);
        return AjaxResult.success();
    }

    @Log(title = "余额调整")
    @PostMapping("/adjustWallet")
    @ApiOperation(value="余额调整")
    public AjaxResult<Object> adjustWallet(@Validated @RequestBody UserWalletValidate walletValidate) {
        iUserService.adjustWallet(walletValidate);
        return AjaxResult.success();
    }
    
    @Log(title = "佣金调整")
    @PostMapping("/adjustEarnings")
    @ApiOperation(value="佣金调整")
    public AjaxResult<Object> adjustEarnings(@Validated @RequestBody UserEarningsValidate earningsValidate) {
        iUserService.adjustEarnings(earningsValidate);
        return AjaxResult.success();
    }

    @Log(title = "发放优惠券")
    @PostMapping("/sendCoupon")
    @ApiModelProperty(value = "发放优惠券")
    public AjaxResult<Object> sendCoupon(@Validated @RequestBody SendCouponValidate sendCouponValidate) {
        Integer adminId = LikeAdminThreadLocal.getAdminId();

        List<Integer> couponIds = sendCouponValidate.getCouponIds();
        List<Integer> userIds = sendCouponValidate.getUserIds();
        Integer number = sendCouponValidate.getNumber();
        iUserService.sendCoupon(couponIds, userIds, number, adminId);
        return AjaxResult.success();
    }

    @GetMapping("/inviterList")
    @ApiOperation(value="邀请列表")
    public AjaxResult<PageResult<InviterVo>> inviterList (@Validated PageValidate pageValidate,
                                           @Validated InviterSearchValidate searchValidate) {
        PageResult<InviterVo> list = iUserService.inviterList(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

}
