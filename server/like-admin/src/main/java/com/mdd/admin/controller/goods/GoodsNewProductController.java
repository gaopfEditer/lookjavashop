package com.mdd.admin.controller.goods;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.mdd.admin.service.IGoodsNewService;
import com.mdd.admin.service.IGoodsService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.IdsValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.goods.GoodsCreateValidate;
import com.mdd.admin.validate.goods.GoodsSearchValidate;
import com.mdd.admin.validate.goods.GoodsUpdateValidate;
import com.mdd.admin.vo.goods.GoodsCommonVo;
import com.mdd.admin.vo.goods.GoodsDetailVo;
import com.mdd.admin.vo.goods.GoodsNewListVo;
import com.mdd.common.aop.NotPower;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/goodsNew/product")
@Api("商品产品管理")
public class GoodsNewProductController {

    @Resource
    IGoodsService iGoodsService;
    @Resource
    IGoodsNewService IGoodsNewService;

    @NotPower
    @GetMapping("/common")
    @ApiOperation("公共商品")
    public AjaxResult<PageResult<GoodsCommonVo>> common(@Validated PageValidate pageValidate,
                                                        @Validated GoodsSearchValidate goodsSearchValidate) {
        PageResult<GoodsCommonVo> list = iGoodsService.common(pageValidate, goodsSearchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/list")
    @ApiOperation("商品列表")
    public AjaxResult<PageResult<GoodsNewListVo>> list(@Validated PageValidate pageValidate,
                                                      @Validated GoodsSearchValidate goodsSearchValidate) {
        PageResult<GoodsNewListVo> list = IGoodsNewService.list(pageValidate, goodsSearchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation("商品详情")
    public AjaxResult<Object> detail(@Validated @IDMust() @RequestParam("id") Integer id){
        GoodsDetailVo detail = iGoodsService.detail(id);
        return AjaxResult.success(detail);
    }
    
    @PostMapping("/add")
    @ApiOperation("商品新增")
    public AjaxResult<Object> add(@Validated @RequestBody GoodsCreateValidate goodsCreateValidate) {
        iGoodsService.add(goodsCreateValidate);
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    @ApiOperation("商品编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody GoodsUpdateValidate goodsUpdateValidate){
        boolean result = iGoodsService.edit(goodsUpdateValidate);
        if (result){
            return AjaxResult.success();
        }else {
            return AjaxResult.success("商品信息修改成功,该商品属于分销商品，请重新设置分销信息！");
        }
    }

    @PostMapping("/del")
    @ApiOperation("商品删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iGoodsService.del(idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("/change")
    @ApiOperation("商品状态")
    public AjaxResult<Object> change(@Validated @RequestBody IdValidate idValidate){
        iGoodsService.change(idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("/sort")
    @ApiOperation("商品排序")
    public AjaxResult<Object> sort(@RequestBody Map<String, Integer> params) {
        Assert.notNull(params.get("id"), "id参数缺失");
        Assert.notNull(params.get("sort"), "sort参数缺失");

        iGoodsService.sort(params.get("id"), params.get("sort"));
        return AjaxResult.success();
    }

    @PostMapping("/batchDelete")
    @ApiOperation("批量删除")
    public AjaxResult<Object> batchDelete(@Validated @RequestBody IdsValidate idsValidate) {
        iGoodsService.batchDelete(idsValidate.getIds());
        return AjaxResult.success();
    }

    @PostMapping("/batchUpper")
    @ApiOperation("批量上架")
    public AjaxResult<Object> batchUpper(@Validated @RequestBody IdsValidate idsValidate) {
        iGoodsService.batchUpper(idsValidate.getIds());
        return AjaxResult.success();
    }

    @PostMapping("/batchLower")
    @ApiOperation("批量下架")
    public AjaxResult<Object> batchLower(@Validated @RequestBody IdsValidate idsValidate) {
        iGoodsService.batchLower(idsValidate.getIds());
        return AjaxResult.success();
    }

}
