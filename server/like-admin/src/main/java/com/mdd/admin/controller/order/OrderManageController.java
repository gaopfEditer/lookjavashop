package com.mdd.admin.controller.order;

import com.mdd.admin.LikeAdminThreadLocal;
import com.mdd.admin.service.IOrderManageService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.order.OrderRemarkValidate;
import com.mdd.admin.validate.order.OrderSearchValidate;
import com.mdd.admin.validate.order.OrderSendDeliveryValidate;
import com.mdd.admin.vo.order.OrderManageDetailVo;
import com.mdd.admin.vo.order.OrderManageListedVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("api/order/order")
@Api("订单管理")
public class OrderManageController {

    @Resource
    IOrderManageService iOrderManageService;

    @GetMapping("/list")
    @ApiOperation("订单列表")
    public AjaxResult<PageResult<OrderManageListedVo>> list(@Validated PageValidate pageValidate,
                                                            @Validated OrderSearchValidate orderSearchValidate) {
        PageResult<OrderManageListedVo> lists = iOrderManageService.list(pageValidate,orderSearchValidate);
        return AjaxResult.success(lists);
    }

    @GetMapping("/detail")
    @ApiOperation("订单详情")
    public AjaxResult<OrderManageDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        OrderManageDetailVo detail = iOrderManageService.detail(id);
        return AjaxResult.success(detail);
    }

    @PostMapping("/cancel")
    @ApiOperation("订单取消")
    public AjaxResult<Object> cancel(@Validated @RequestBody IdValidate idValidate) {
        Integer adminId = LikeAdminThreadLocal.getAdminId();

        iOrderManageService.cancel(idValidate.getId(), adminId);
        return AjaxResult.success();
    }

    @PostMapping("/remarks")
    @ApiOperation("订单备注")
    public AjaxResult<Object> remarks(@Validated @RequestBody OrderRemarkValidate remarkValidate) {
        Integer orderId = remarkValidate.getId();
        String remarks = remarkValidate.getRemarks();
        iOrderManageService.remarks(orderId, remarks);
        return AjaxResult.success();
    }

    @PostMapping("/sendDelivery")
    @ApiOperation("订单发货")
    public AjaxResult<Object> sendDelivery(@Validated @RequestBody OrderSendDeliveryValidate sendDeliveryValidate) {
        Integer adminId = LikeAdminThreadLocal.getAdminId();

        iOrderManageService.sendDelivery(adminId, sendDeliveryValidate);
        return AjaxResult.success();
    }

    @PostMapping("/takeDelivery")
    @ApiOperation("确认收货")
    public AjaxResult<Object> takeDelivery(@Validated @RequestBody IdValidate idValidate) {
        Integer adminId = LikeAdminThreadLocal.getAdminId();

        iOrderManageService.takeDelivery(idValidate.getId(), adminId);
        return AjaxResult.success();
    }

    @GetMapping("/logistics")
    @ApiModelProperty("查看物流")
    public AjaxResult<Object> logistics(@Validated @IDMust() @RequestParam("id") Integer id) {
        Map<String, Object> map = iOrderManageService.logistics(id);
        return AjaxResult.success(map);
    }

}
