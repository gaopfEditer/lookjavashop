package com.mdd.front.controller;

import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.front.service.IOrderAfterService;
import com.mdd.front.validate.common.IdValidate;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.order.OrderAfterCreateValidate;
import com.mdd.front.validate.order.OrderAfterDeliveryValidate;
import com.mdd.front.vo.order.OrderAfterCreateVo;
import com.mdd.front.vo.order.OrderAfterDetailVo;
import com.mdd.front.vo.order.OrderAfterInfoVo;
import com.mdd.front.vo.order.OrderAfterListVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/api/order/after")
@Api(value = "售后管理")
public class OrderAfterController {

    @Resource
    IOrderAfterService iOrderAfterService;

    @GetMapping("/list")
    @ApiOperation(value = "售后列表")
    public AjaxResult<PageResult<OrderAfterListVo>> list(@Validated PageValidate pageValidate,
                                                         @RequestParam(value = "type", defaultValue = "apply") String type) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        PageResult<OrderAfterListVo> list = iOrderAfterService.list(pageValidate, userId, type);
        return AjaxResult.success(list);
    }

    @PostMapping("/delivery")
    @ApiOperation(value = "买家退货")
    public AjaxResult<Object> delivery(@Validated @RequestBody OrderAfterDeliveryValidate deliveryValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        iOrderAfterService.delivery(userId, deliveryValidate);
        return AjaxResult.success();
    }

    @PostMapping("/add")
    @ApiOperation(value = "申请售后")
    public AjaxResult<OrderAfterCreateVo> add(@Validated @RequestBody OrderAfterCreateValidate orderAfterCreateValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        OrderAfterCreateVo orderAfterCreateVo = iOrderAfterService.add(userId, orderAfterCreateValidate);
        return AjaxResult.success(orderAfterCreateVo);
    }

    @GetMapping("/detail")
    @ApiOperation(value = "售后详情")
    public AjaxResult<OrderAfterDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        OrderAfterDetailVo detailVo = iOrderAfterService.detail(userId, id);
        return AjaxResult.success(detailVo);
    }

    @PostMapping("/cancel")
    @ApiOperation(value = "撤销售后")
    public AjaxResult<Object> cancel(@Validated @RequestBody IdValidate idValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        iOrderAfterService.cancel(userId, idValidate.getId());
        return AjaxResult.success();
    }

    @GetMapping("/orderInfo")
    @ApiOperation(value = "订单信息")
    public AjaxResult<OrderAfterInfoVo> orderInfo(@Validated @IDMust() @RequestParam("orderGoodsId") Integer orderGoodsId) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        OrderAfterInfoVo infoVo = iOrderAfterService.orderInfo(userId, orderGoodsId);
        return AjaxResult.success(infoVo);
    }


}
