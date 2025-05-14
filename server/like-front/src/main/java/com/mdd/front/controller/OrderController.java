package com.mdd.front.controller;

import com.alibaba.fastjson2.JSONArray;
import com.mdd.common.enums.OrderEnum;
import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.front.validate.common.IdValidate;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.order.OrderSettleValidate;
import com.mdd.front.validate.selffetchorder.SelffetchOrderPickCodeVerifyValidate;
import com.mdd.front.validate.selffetchorder.SelffetchOrderSearchValidate;
import com.mdd.front.validate.selffetchorder.SelffetchOrderVerifyValidate;
import com.mdd.front.vo.order.*;
import com.mdd.common.core.AjaxResult;
import com.mdd.front.service.IOrderService;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.front.vo.selffetchOrder.SelffetchOrderDetailVo;
import com.mdd.front.vo.selffetchOrder.SelffetchOrderPickuplVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/order")
@Api(tags = "订单管理")
public class OrderController {

    @Resource
    IOrderService iOrderService;

    @PostMapping("/settlement")
    @ApiOperation(value = "订单结算")
    public AjaxResult<OrderSettleResultVo> settlementOrder(@Validated @RequestBody OrderSettleValidate orderSettleValidate) {
        OrderSettleResultVo settlementOrderResultVo = iOrderService.settlementOrder(orderSettleValidate);
        return AjaxResult.success(settlementOrderResultVo);
    }

    @PostMapping("/submit")
    @ApiOperation(value = "提交订单")
    public AjaxResult<OrderSubmitResultVo> submitOrder(@Validated @RequestBody OrderSettleValidate orderSettleValidate) {
        OrderSubmitResultVo submitOrderResultVo = iOrderService.submitOrder(orderSettleValidate);
        return AjaxResult.success(submitOrderResultVo);
    }

    @GetMapping("/list")
    @ApiModelProperty(value = "订单列表")
    public AjaxResult<PageResult<OrderListVo>> list(@Validated PageValidate pageValidate,
                                                    @RequestParam(value = "status", defaultValue = "0") Integer status) {
        PageResult<OrderListVo> pageResult = iOrderService.list(pageValidate, status);
        return AjaxResult.success(pageResult);
    }

    @GetMapping("/detail")
    @ApiOperation("订单详情")
    public AjaxResult<OrderDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        OrderDetailVo orderDetailVo = iOrderService.detail(id);
        return AjaxResult.success(orderDetailVo);
    }

    @PostMapping("/confirm")
    @ApiOperation("确认收货")
    public AjaxResult<Object> confirm(@Validated() @RequestBody IdValidate idValidate) {
        iOrderService.confirm(idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("/del")
    @ApiOperation("删除订单")
    public AjaxResult<Object> del(@Validated() @RequestBody IdValidate idValidate) {
        iOrderService.del(idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("/cancel")
    @ApiOperation("取消订单")
    public AjaxResult<Object> cancel(@Validated() @RequestBody IdValidate idValidate) {
        iOrderService.cancel(idValidate.getId());
        return AjaxResult.success();
    }

    @GetMapping("/logistics")
    @ApiOperation("查看物流")
    public AjaxResult<OrderLogisticsVo> logistics(@Validated() @IDMust() @RequestParam("id") Integer id) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        OrderLogisticsVo result = iOrderService.logistics(userId, id);
        return AjaxResult.success(result);
    }
    @GetMapping("/selffetchList")
    @ApiModelProperty(value = "到店自提订单列表")
    public AjaxResult<PageResult<OrderListVo>> selffetchlist(@Validated PageValidate pageValidate, SelffetchOrderSearchValidate selffetchOrderSearchValidate) {
        selffetchOrderSearchValidate.setCurrUserId(LikeFrontThreadLocal.getUserId());
        selffetchOrderSearchValidate.setDeliveryType(OrderEnum.DELIVERY_TYPE_PICK.getCode());
        PageResult<OrderListVo> pageResult = iOrderService.selffetchlist(pageValidate, selffetchOrderSearchValidate);
        return AjaxResult.success(pageResult);
    }

    @GetMapping("/selffetchDetail")
    @ApiOperation("自提单订单详情")
    public AjaxResult<SelffetchOrderDetailVo> selffetchDetail(@Validated @IDMust() @RequestParam("id") Integer id) {
        SelffetchOrderDetailVo orderDetailVo = iOrderService.selffetchDetail(id);
        return AjaxResult.success(orderDetailVo);
    }
    @PostMapping("/selffetchVerify")
    @ApiOperation("确认自提")
    public AjaxResult<Object> selffetchVerify(@Validated @RequestBody SelffetchOrderVerifyValidate selffetchOrderVerifyValidate) {
        //System.out.println(orderSelffetchValidate);
        Integer orderId = selffetchOrderVerifyValidate.getId();
        JSONArray items = selffetchOrderVerifyValidate.getItems();
        iOrderService.selffetchVerify(orderId, items);
        return AjaxResult.success();
    }

    @PostMapping("/pickCodeVerify")
    @ApiOperation("核销码检查")
    public AjaxResult<SelffetchOrderPickuplVo> pickCodeVerify(@Validated @RequestBody SelffetchOrderPickCodeVerifyValidate selffetchOrderPickCodeVerifyValidate) {
        selffetchOrderPickCodeVerifyValidate.setCurrUserId(LikeFrontThreadLocal.getUserId());
        SelffetchOrderPickuplVo selffetchOrderPickuplVo = iOrderService.pickCodeVerify(selffetchOrderPickCodeVerifyValidate);
        return AjaxResult.success(selffetchOrderPickuplVo);
    }
}