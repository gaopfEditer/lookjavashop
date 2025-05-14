package com.mdd.admin.controller.distribution;

import com.mdd.admin.service.IDistributionStoreService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.*;
import com.mdd.admin.vo.distribution.*;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/distribution/store")
@Api(tags = "分销商管理")
public class DistributionStoreController {

    @Resource
    IDistributionStoreService iDistributionStoreService;

    @GetMapping("/list")
    @ApiOperation("分销商列表")
    public AjaxResult<PageResult<DistributionStoreListedVo>> list(@Validated PageValidate pageValidate,
                                                                  @Validated DistributionStoreSearchValidate searchValidate) {
        PageResult<DistributionStoreListedVo> list = iDistributionStoreService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @PostMapping("/open")
    @ApiOperation("分销商开通")
    public AjaxResult<Object> open(@Validated @RequestBody DistributionStoreOpenValidate openValidate) {
        iDistributionStoreService.open(openValidate);
        return AjaxResult.success();
    }

    @PostMapping("/freeze")
    @ApiOperation("冻结分销商")
    public AjaxResult<Object> freeze(@Validated @RequestBody IdValidate idValidate) {
        iDistributionStoreService.freeze(idValidate.getId());
        return AjaxResult.success();
    }

    @GetMapping("/adjustLevelInfo")
    @ApiOperation("等级调整详情")
    public AjaxResult<DistributionAdjustLevelVo> adjustLevelInfo(@Validated @IDMust() @RequestParam("id") Integer id) {
        DistributionAdjustLevelVo vo = iDistributionStoreService.adjustLevelInfo(id);
        return AjaxResult.success(vo);
    }

    @PostMapping("/adjustLevel")
    @ApiOperation("等级调整")
    public AjaxResult<Object> adjustLevel(@Validated @RequestBody DistributionAdjustLevelValidate adjustValidate) {
        iDistributionStoreService.adjustLevel(adjustValidate);
        return AjaxResult.success();
    }

    @GetMapping("/detail")
    @ApiOperation("分销商详情")
    public AjaxResult<DistributionStoreDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        DistributionStoreDetailVo vo = iDistributionStoreService.detail(id);
        return AjaxResult.success(vo);
    }

    @GetMapping("/fansInfo")
    @ApiOperation("下级详情")
    public AjaxResult<DistributionFansInfoVo> fansInfo(@Validated @IDMust() @RequestParam("id") Integer id) {
        DistributionFansInfoVo vo = iDistributionStoreService.fansInfo(id);
        return AjaxResult.success(vo);
    }

    @GetMapping("/fansList")
    @ApiOperation("下级列表")
    public AjaxResult<PageResult<DistributionFansListedVo>> fansList(@Validated PageValidate pageValidate,
                                                                     @Validated DistributionFansSearchValidate searchValidate) {
        PageResult<DistributionFansListedVo> list = iDistributionStoreService.fansList(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @PostMapping("/adjustLeader")
    @ApiOperation("调整上级")
    public AjaxResult<Object> adjustLeader(@Validated @RequestBody DistributionAdjustLeaderValidate adjustValidate) {
        iDistributionStoreService.adjustLeader(adjustValidate);
        return AjaxResult.success();
    }

    @NotLogin
    @GetMapping("/update")
    @ApiOperation("分销初始化数据")
    public AjaxResult<Object> updateData() {
        iDistributionStoreService.updateData();
        return AjaxResult.success();
    }

}
