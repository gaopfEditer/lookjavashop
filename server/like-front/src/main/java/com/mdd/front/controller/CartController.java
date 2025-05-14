package com.mdd.front.controller;

import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.front.validate.cart.CartAddValidate;
import com.mdd.front.validate.cart.CartChangeSkuValidate;
import com.mdd.front.validate.cart.CartSelectedValidate;
import com.mdd.front.validate.common.IdsValidate;
import com.mdd.front.vo.cart.CartCountVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.front.service.ICartService;
import com.mdd.front.validate.cart.CartChangeNumValidate;
import com.mdd.front.vo.cart.CartListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/cart")
@Api(tags = "购物车管理")
public class CartController {

    @Resource
    ICartService icartService;

    @GetMapping("/list")
    @ApiOperation(value = "购物车")
    public AjaxResult<CartListVo> list() {
        Integer userId = LikeFrontThreadLocal.getUserId();
        CartListVo list = icartService.list(userId);
        return AjaxResult.success(list);
    }

    @PostMapping("/add")
    @ApiOperation(value = "加入购物车")
    public AjaxResult<Object> add(@Validated @RequestBody CartAddValidate cartAddValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        icartService.add(userId, cartAddValidate);
        return AjaxResult.success();
    }

    @PostMapping("/select")
    @ApiOperation(value = "购物车选中")
    public AjaxResult<Object> select(@Validated @RequestBody CartSelectedValidate cartSelectedValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        icartService.select(userId, cartSelectedValidate);
        return AjaxResult.success();
    }

    @PostMapping("/changeNum")
    @ApiOperation(value = "购物车变动数量")
    public AjaxResult<Object> changeNum(@Validated @RequestBody CartChangeNumValidate changeNumValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        icartService.changeNum(userId, changeNumValidate);
        return AjaxResult.success();
    }

    @PostMapping("/del")
    @ApiOperation(value = "删除购物车")
    public AjaxResult<Object> del(@Validated @RequestBody IdsValidate idsValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        icartService.del(userId, idsValidate.getIds());
        return AjaxResult.success();
    }

    @PostMapping("/changeSku")
    @ApiOperation(value = "编辑规格")
    public AjaxResult<Object> changeSku(@Validated @RequestBody CartChangeSkuValidate changeSkuValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        icartService.changeSku(userId, changeSkuValidate);
        return AjaxResult.success();
    }


    @GetMapping("/count")
    @ApiOperation(value = "购物车数量")
    public AjaxResult<CartCountVo> cartCount() {
        Integer userId = LikeFrontThreadLocal.getUserId();
        CartCountVo count = icartService.cartCount(userId);
        return AjaxResult.success(count);
    }

}
