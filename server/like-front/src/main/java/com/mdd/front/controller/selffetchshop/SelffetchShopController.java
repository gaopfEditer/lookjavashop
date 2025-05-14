package com.mdd.front.controller.selffetchshop;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.front.service.ISelffetchShopService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.selffetchshop.SelffetchShopSearchValidate;
import com.mdd.front.vo.selffetchshop.SelffetchShopDetailVo;
import com.mdd.front.vo.selffetchshop.SelffetchShopListedVo;
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
        searchValidate.setStatus(1);
        PageResult<SelffetchShopListedVo> list = iSelffetchShopService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation(value="自提门店详情")
    public AjaxResult<SelffetchShopDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        SelffetchShopDetailVo detail = iSelffetchShopService.detail(id);
        return AjaxResult.success(detail);
    }
}
