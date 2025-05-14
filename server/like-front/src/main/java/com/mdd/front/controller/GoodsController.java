package com.mdd.front.controller;

import com.alibaba.fastjson2.JSONArray;
import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.front.validate.goods.GoodsLimitSearchValidate;
import com.mdd.front.validate.goods.GoodsListSearchValidate;
import com.mdd.front.vo.goods.GoodsImageResourceVo;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.front.service.IGoodsService;
import com.mdd.front.validate.common.IdValidate;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.goods.GoodsDetailVo;
import com.mdd.front.vo.goods.GoodsListsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goods")
@Api(tags = "商品管理")
public class GoodsController {

    @Resource
    IGoodsService iGoodsService;

    @NotLogin
    @GetMapping("/searchRecord")
    @ApiOperation(value = "搜索记录")
    public AjaxResult<Map<String, Object>> searchRecord() {
        Map<String, Object> list = iGoodsService.searchRecord();
        return AjaxResult.success(list);
    }

    @NotLogin
    @GetMapping("/list")
    @ApiOperation(value = "商品列表")
    public AjaxResult<PageResult<GoodsListsVo>> list(@Validated PageValidate pageValidate,
                                                     @Validated GoodsListSearchValidate searchValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        PageResult<GoodsListsVo> list = iGoodsService.list(userId, pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @NotLogin
    @GetMapping("/limit")
    @ApiOperation(value = "商品列表(限定数量)")
    public AjaxResult<List<GoodsListsVo>> limitList(@Validated GoodsLimitSearchValidate searchValidate) {
        if (searchValidate.getCategoryId() == null) {
            return AjaxResult.success(iGoodsService.limitList(searchValidate.getType(), searchValidate.getLimit()));
        } else {
            return AjaxResult.success(iGoodsService.limitList(searchValidate.getType(), searchValidate.getLimit(), searchValidate.getCategoryId()));
        }
    }

    @GetMapping("/collectList")
    @ApiOperation(value = "收藏列表")
    public AjaxResult<PageResult<GoodsListsVo>> collectList(@Validated PageValidate pageValidate) {
        PageResult<GoodsListsVo> list = iGoodsService.collectList(pageValidate);
        return AjaxResult.success(list);
    }

    @NotLogin
    @GetMapping("/detail")
    @ApiOperation(value = "商品详情")
    public AjaxResult<Object> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        GoodsDetailVo detail = iGoodsService.detail(userId, id);
        return AjaxResult.success(detail);
    }

    @PostMapping("/collect")
    @ApiOperation(value = "收藏商品")
    public AjaxResult<Object> collect(@Validated @RequestBody IdValidate idValidate) {
        iGoodsService.collect(idValidate.getId());
        return AjaxResult.success();
    }

    @NotLogin
    @GetMapping("/category")
    @ApiOperation(value = "商品分类")
    public AjaxResult<JSONArray> category() {
        JSONArray list = iGoodsService.category();
        return AjaxResult.success(list);
    }

    @GetMapping("/imageResource")
    @ApiOperation(value = "商品分享-图片资源(base64)")
    public AjaxResult<GoodsImageResourceVo> imageResource(@Validated @IDMust() @RequestParam("id") Integer id) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        GoodsImageResourceVo vo = iGoodsService.imageResource(userId, id);
        return AjaxResult.success(vo);
    }


}
