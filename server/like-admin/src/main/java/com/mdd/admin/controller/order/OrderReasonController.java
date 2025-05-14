package com.mdd.admin.controller.order;

import com.mdd.admin.service.IOrderReasonService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.order.OrderReasonCreateValidate;
import com.mdd.admin.validate.order.OrderReasonSearchValidate;
import com.mdd.admin.validate.order.OrderReasonUpdateValidate;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.entity.order.OrderAfterReason;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/order/reason")
@Api("订单售后原因管理")
public class OrderReasonController {

    @Resource
    IOrderReasonService iOrderReasonService;

    @GetMapping("/list")
    @ApiOperation(value = "售后原因列表")
    public AjaxResult<List<OrderAfterReason>> list(@Validated OrderReasonSearchValidate searchValidate) {
        List<OrderAfterReason> list = iOrderReasonService.list(searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation(value = "售后原因详情")
    public AjaxResult<OrderAfterReason> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        OrderAfterReason detail = iOrderReasonService.detail(id);
        return AjaxResult.success(detail);
    }

    @PostMapping("/add")
    @ApiOperation(value = "售后原因新增")
    public AjaxResult<Object> add(@Validated @RequestBody OrderReasonCreateValidate createValidate) {
        iOrderReasonService.add(createValidate);
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    @ApiOperation(value = "售后原因编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody OrderReasonUpdateValidate updateValidate) {
        iOrderReasonService.edit(updateValidate);
        return AjaxResult.success();
    }

    @PostMapping("/del")
    @ApiOperation(value = "售后原因删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iOrderReasonService.del(idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("/change")
    @ApiOperation(value = "售后原因状态")
    public AjaxResult<Object> change(@Validated @RequestBody IdValidate idValidate) {
        iOrderReasonService.change(idValidate.getId());
        return AjaxResult.success();
    }

}
