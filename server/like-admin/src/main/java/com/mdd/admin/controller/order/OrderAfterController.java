package com.mdd.admin.controller.order;

import com.mdd.admin.LikeAdminThreadLocal;
import com.mdd.admin.service.IOrderAfterService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.order.OrderAfterSearchValidate;
import com.mdd.admin.validate.order.OrderRemarkValidate;
import com.mdd.admin.vo.order.OrderAfterDetailVo;
import com.mdd.admin.vo.order.OrderAfterListedVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/order/after")
@Api("售后管理")
public class OrderAfterController {

    @Resource
    IOrderAfterService iOrderAfterService;

    @GetMapping("/list")
    @ApiOperation("售后列表")
    public AjaxResult<PageResult<OrderAfterListedVo>> list(@Validated PageValidate pageValidate,
                                                           @Validated OrderAfterSearchValidate searchValidate) {
        PageResult<OrderAfterListedVo> list = iOrderAfterService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation("售后详情")
    public AjaxResult<Object> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        OrderAfterDetailVo vo = iOrderAfterService.detail(id);
        return AjaxResult.success(vo);
    }

    @PostMapping("agree")
    @ApiOperation("卖家同意售后")
    public AjaxResult<Object> agree(@Validated @RequestBody OrderRemarkValidate remarkValidate) {
        Integer adminId = LikeAdminThreadLocal.getAdminId();

        iOrderAfterService.agree(adminId, remarkValidate.getId(), remarkValidate.getRemarks());
        return AjaxResult.success();
    }

    @PostMapping("refuse")
    @ApiOperation("卖家拒绝售后")
    public AjaxResult<Object> refuse(@Validated @RequestBody OrderRemarkValidate remarkValidate) {
        Integer adminId = LikeAdminThreadLocal.getAdminId();

        iOrderAfterService.refuse(adminId, remarkValidate.getId(), remarkValidate.getRemarks());
        return AjaxResult.success();
    }

    @PostMapping("refuseGoods")
    @ApiOperation("卖家拒绝收货")
    public AjaxResult<Object> refuseGoods(@Validated @RequestBody IdValidate idValidate) {
        Integer adminId = LikeAdminThreadLocal.getAdminId();

        iOrderAfterService.refuseGoods(adminId, idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("confirmGoods")
    @ApiOperation("卖家确认收货")
    public AjaxResult<Object> confirmGoods(@Validated @RequestBody IdValidate idValidate) {
        Integer adminId = LikeAdminThreadLocal.getAdminId();

        iOrderAfterService.confirmGoods(adminId, idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("confirmRefund")
    @ApiOperation("卖家确认退款")
    public AjaxResult<Object> confirmRefund(@Validated @RequestBody IdValidate idValidate) {
        Integer adminId = LikeAdminThreadLocal.getAdminId();

        iOrderAfterService.confirmRefund(adminId, idValidate.getId());
        return AjaxResult.success();
    }

}
