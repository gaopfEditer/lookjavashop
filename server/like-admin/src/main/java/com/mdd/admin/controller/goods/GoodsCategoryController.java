package com.mdd.admin.controller.goods;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSONArray;
import com.mdd.admin.service.IGoodsCategoryService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.goods.GoodsCategoryCreateValidate;
import com.mdd.admin.validate.goods.GoodsCategoryUpdateValidate;
import com.mdd.admin.vo.goods.GoodsCategoryVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.validator.annotation.IDMust;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/goods/goodsCategory")
@Api(tags = "商品分类管理")
public class GoodsCategoryController {

    @Resource
    IGoodsCategoryService iGoodsCategoryService;

    @GetMapping("/list")
    @ApiOperation(value="分类列表")
    public AjaxResult<JSONArray> goodsCategoryList() {
        JSONArray list = iGoodsCategoryService.list();
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation(value="分类详情")
    public AjaxResult<GoodsCategoryVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        GoodsCategoryVo detail = iGoodsCategoryService.detail(id);
        return AjaxResult.success(detail);
    }

    @PostMapping("/add")
    @ApiOperation(value="分类新增")
    public AjaxResult<Object> add(@Validated @RequestBody GoodsCategoryCreateValidate goodsCategoryCreateValidate) {
        iGoodsCategoryService.add(goodsCategoryCreateValidate);
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    @ApiOperation(value="分类编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody GoodsCategoryUpdateValidate goodsCategoryUpdateValidate) {
        iGoodsCategoryService.edit(goodsCategoryUpdateValidate);
        return AjaxResult.success();
    }

    @PostMapping("/del")
    @ApiOperation(value="分类删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iGoodsCategoryService.del(idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("/change")
    @ApiOperation(value="分类状态")
    public  AjaxResult<Object> change(@Validated @RequestBody IdValidate idValidate) {
        iGoodsCategoryService.change(idValidate.getId());
        return AjaxResult.success();
    }

}
