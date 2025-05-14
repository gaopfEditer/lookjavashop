package com.mdd.admin.controller.goods;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSONArray;
import com.mdd.admin.service.IGoodsNewCategoryService;
import com.mdd.common.core.AjaxResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/goodsNew/category")
@Api(tags = "商品分类管理new")
public class GoodsNewCategoryController {
  
  @Resource
  IGoodsNewCategoryService iGoodsNewCategoryService;

  @GetMapping("/list")
  @ApiOperation("分类列表")
  public AjaxResult<JSONArray> list() {
    JSONArray list = iGoodsNewCategoryService.list();
    return AjaxResult.success(list);
  }
}
