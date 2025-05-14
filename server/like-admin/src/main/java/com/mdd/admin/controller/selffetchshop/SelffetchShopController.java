package com.mdd.admin.controller.selffetchshop;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.aop.Log;
import com.mdd.admin.service.ISelffetchShopService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.selffetchshop.SelffetchShopCreateValidate;
import com.mdd.admin.validate.selffetchshop.SelffetchShopStatusValidate;
import com.mdd.admin.validate.selffetchshop.SelffetchShopUpdateValidate;
import com.mdd.admin.validate.selffetchshop.SelffetchShopSearchValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.setting.RegionSearchValidate;
import com.mdd.admin.vo.selffetchshop.SelffetchShopListedVo;
import com.mdd.admin.vo.selffetchshop.SelffetchShopDetailVo;
import com.mdd.common.aop.NotPower;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/selffetchshop")
@Api(tags = "自提门店管理")
public class SelffetchShopController {

    @Resource
    ISelffetchShopService iSelffetchShopService;

    @GetMapping("/list")
    @ApiOperation(value="自提门店列表")
    public AjaxResult<PageResult<SelffetchShopListedVo>> list(@Validated PageValidate pageValidate,
                                                     @Validated SelffetchShopSearchValidate searchValidate) {
        PageResult<SelffetchShopListedVo> list = iSelffetchShopService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation(value="自提门店详情")
    @NotPower
    public AjaxResult<SelffetchShopDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        SelffetchShopDetailVo detail = iSelffetchShopService.detail(id);
        return AjaxResult.success(detail);
    }

    @Log(title = "自提门店新增")
    @PostMapping("/add")
    @ApiOperation(value="自提门店新增")
    public AjaxResult<Object> add(@Validated @RequestBody SelffetchShopCreateValidate createValidate) {
        iSelffetchShopService.add(createValidate);
        return AjaxResult.success();
    }

    @Log(title = "自提门店编辑")
    @PostMapping("/edit")
    @ApiOperation(value="自提门店编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody SelffetchShopUpdateValidate updateValidate) {
        iSelffetchShopService.edit(updateValidate);
        return AjaxResult.success();
    }

    @Log(title = "自提门店删除")
    @PostMapping("/del")
    @ApiOperation(value="自提门店删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iSelffetchShopService.del(idValidate.getId());
        return AjaxResult.success();
    }

    @GetMapping("/regionSearch")
    @ApiOperation(value="腾讯地图区域搜索")
    public AjaxResult<JSONObject> regionSearch(@Validated RegionSearchValidate regionSearchValidate) {
        JSONObject ret = iSelffetchShopService.regionSearch(regionSearchValidate);
        return AjaxResult.success(ret);
    }

    @PostMapping("/status")
    @ApiOperation(value="自提门店状态修改")
    public AjaxResult<Object> status(@Validated @RequestBody SelffetchShopStatusValidate selffetchShopStatusValidate) {
        iSelffetchShopService.status(selffetchShopStatusValidate);
        return AjaxResult.success();
    }

}
