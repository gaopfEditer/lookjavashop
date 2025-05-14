package com.mdd.admin.controller.marketing;

import com.mdd.admin.service.IMarketingCouponService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.IdsValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.marketing.*;
import com.mdd.admin.vo.marketing.MarketingCouponDetailVo;
import com.mdd.admin.vo.marketing.MarketingCouponListedVo;
import com.mdd.admin.vo.marketing.MarketingCouponRecordVo;
import com.mdd.admin.vo.marketing.MarketingCouponSelectVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/marketing/coupon")
@Api("营销优惠券管理")
public class MarketingCouponController {

    @Resource
    IMarketingCouponService iMarketingCouponService;

    @GetMapping("/select")
    @ApiOperation(value = "优惠券选择")
    public AjaxResult<PageResult<MarketingCouponSelectVo>> select(@Validated PageValidate pageValidate,
                                                                  @Validated MarketingCouponSearchValidate searchValidate) {
        PageResult<MarketingCouponSelectVo> list = iMarketingCouponService.select(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/list")
    @ApiOperation(value = "优惠券列表")
    public AjaxResult<PageResult<MarketingCouponListedVo>> list(@Validated PageValidate pageValidate,
                                                                @Validated MarketingCouponSearchValidate searchValidate) {
        PageResult<MarketingCouponListedVo> list = iMarketingCouponService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/info")
    @ApiOperation(value = "优惠券信息")
    public AjaxResult<MarketingCouponListedVo> info(@Validated @IDMust() @RequestParam("id") Integer id) {
        MarketingCouponListedVo vo = iMarketingCouponService.info(id);
        return AjaxResult.success(vo);
    }

    @GetMapping("/detail")
    @ApiOperation(value = "优惠券详情")
    public AjaxResult<MarketingCouponDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        MarketingCouponDetailVo vo = iMarketingCouponService.detail(id);
        return AjaxResult.success(vo);
    }

    @PostMapping("/add")
    @ApiOperation(value = "优惠券新增")
    public AjaxResult<Object> add(@Validated @RequestBody MarketingCouponCreateValidate createValidate) {
        iMarketingCouponService.add(createValidate);
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    @ApiOperation(value = "优惠券编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody MarketingCouponUpdateValidate updateValidate) {
        iMarketingCouponService.edit(updateValidate);
        return AjaxResult.success();
    }

    @PostMapping("/del")
    @ApiOperation(value = "优惠券删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iMarketingCouponService.del(idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("/rename")
    @ApiOperation(value = "优惠券命名")
    public AjaxResult<Object> rename(@Validated @RequestBody MarketingCouponRenameValidate renameValidate) {
        iMarketingCouponService.rename(renameValidate.getId(), renameValidate.getName(), renameValidate.getSendTotal());
        return AjaxResult.success();
    }

//    @PostMapping("release")
//    @ApiOperation(value = "优惠券发布")
//    public AjaxResult<Object> release(@Validated @RequestBody IdValidate idValidate) {
//        iMarketingCouponService.release(idValidate.getId());
//        return AjaxResult.success();
//    }

    @PostMapping("/start")
    @ApiOperation(value = "优惠券开始")
    public AjaxResult<Object> start(@Validated @RequestBody IdValidate idValidate) {
        iMarketingCouponService.start(idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("/end")
    @ApiOperation(value = "优惠券结束")
    public AjaxResult<Object> end(@Validated @RequestBody IdValidate idValidate) {
        iMarketingCouponService.end(idValidate.getId());
        return AjaxResult.success();
    }

    @GetMapping("/record")
    @ApiOperation(value = "优惠券领取记录")
    public AjaxResult<PageResult<MarketingCouponRecordVo>> record(@Validated PageValidate pageValidate,
                                                                  @Validated MarketingCouponRecordValidate searchValidate) {
        PageResult<MarketingCouponRecordVo> list = iMarketingCouponService.record(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @PostMapping("/repeal")
    @ApiOperation(value = "优惠券作废")
    public AjaxResult<Object> repeal(@Validated @RequestBody IdsValidate idsValidate) {
        iMarketingCouponService.repeal(idsValidate.getIds());
        return AjaxResult.success();
    }

}
