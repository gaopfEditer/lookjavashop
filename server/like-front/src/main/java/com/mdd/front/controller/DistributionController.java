package com.mdd.front.controller;

import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.front.service.IDistributionService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.distribution.DistributionApplyValidate;
import com.mdd.front.validate.distribution.DistributionBindValidate;
import com.mdd.front.validate.distribution.DistributionFansSearchValidate;
import com.mdd.front.validate.distribution.DistributionOrderSearchValidate;
import com.mdd.front.vo.distribution.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/api/distribution")
@Api(tags = "分销管理")
public class DistributionController {

    @Resource
    IDistributionService iDistributionService;

    @PostMapping("/bind")
    @ApiOperation(value = "绑定上下级")
    public AjaxResult<Object> bind(@Validated @RequestBody DistributionBindValidate bindValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        iDistributionService.bind(userId, bindValidate.getCode());
        return AjaxResult.success();
    }

    @PostMapping("/apply")
    @ApiOperation(value = "申请分销")
    public AjaxResult<Object> apply(@Validated @RequestBody DistributionApplyValidate applyValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        iDistributionService.apply(userId, applyValidate);
        return AjaxResult.success();
    }

    @GetMapping("/applyDetail")
    @ApiOperation(value = "申请详情")
    public AjaxResult<DistributionApplyDetailVo> applyDetail() {
        Integer userId = LikeFrontThreadLocal.getUserId();
        DistributionApplyDetailVo vo = iDistributionService.applyDetail(userId);
        return AjaxResult.success(vo);
    }

    @GetMapping("/fans")
    @ApiOperation(value = "我的粉丝")
    public AjaxResult<PageResult<DistributionFansVo>> fans(@Validated PageValidate pageValidate,
                                     @Validated DistributionFansSearchValidate searchValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        PageResult<DistributionFansVo> list = iDistributionService.fans(pageValidate, searchValidate, userId);
        return AjaxResult.success(list);
    }

    @GetMapping("/order")
    @ApiOperation(value = "分销订单")
    public AjaxResult<PageResult<DistributionOrderVo>> order(@Validated PageValidate pageValidate,
                                    @Validated DistributionOrderSearchValidate searchValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        PageResult<DistributionOrderVo> list = iDistributionService.order(pageValidate, searchValidate, userId);
        return AjaxResult.success(list);
    }

    @GetMapping("/index")
    @ApiOperation(value = "分销主页")
    public AjaxResult<DistributionIndexVo> index () {
        Integer userId = LikeFrontThreadLocal.getUserId();
        DistributionIndexVo vo = iDistributionService.index(userId);
        return AjaxResult.success(vo);
    }

    @GetMapping("/level")
    @ApiOperation(value = "分销等级")
    public AjaxResult<List<DistributionLevelVo>> level() {
        Integer userId = LikeFrontThreadLocal.getUserId();
        List<DistributionLevelVo> list = iDistributionService.level(userId);
        return AjaxResult.success(list);
    }

    @NotLogin
    @GetMapping("/config")
    @ApiOperation(value = "分销配置")
    public AjaxResult<DistributionConfigVo> config() {
        DistributionConfigVo config = iDistributionService.config();
        return AjaxResult.success(config);
    }


}
