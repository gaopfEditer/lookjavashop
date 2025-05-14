package com.mdd.front.controller;

import com.mdd.front.service.IOrderGoodsService;
import com.mdd.front.vo.order.*;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/order/goods")
@Api(tags = "订单商品管理")
public class OrderGoodsController {

    @Resource
    IOrderGoodsService iOrderGoodsService;

    @GetMapping("/detail")
    @ApiOperation("订单商品详情")
    public AjaxResult<OrderGoodsDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        OrderGoodsDetailVo orderDetailVo = iOrderGoodsService.detail(id);
        return AjaxResult.success(orderDetailVo);
    }


}
