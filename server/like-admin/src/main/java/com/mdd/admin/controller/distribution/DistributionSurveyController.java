package com.mdd.admin.controller.distribution;

import com.mdd.admin.service.IDistributionSurveyService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.distribution.DistributionSurveyDataVo;
import com.mdd.admin.vo.distribution.DistributionSurveyEarningsVo;
import com.mdd.admin.vo.distribution.DistributionSurveyFansVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/distribution/survey")
@Api(tags = "分销概括管理")
public class DistributionSurveyController {

    @Resource
    IDistributionSurveyService iDistributionSurveyService;

    @GetMapping("/data")
    @ApiOperation("数据概括")
    public AjaxResult<DistributionSurveyDataVo> data() {
        DistributionSurveyDataVo vo = iDistributionSurveyService.data();
        return AjaxResult.success(vo);
    }

    @GetMapping("/topEarnings")
    @ApiOperation("分销商收入排行")
    public AjaxResult<PageResult<DistributionSurveyEarningsVo>> topEarnings(@Validated PageValidate pageValidate) {
        PageResult<DistributionSurveyEarningsVo> list = iDistributionSurveyService.topEarnings(pageValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/topFans")
    @ApiOperation("分销商下级人数排行")
    public AjaxResult<PageResult<DistributionSurveyFansVo>> topFans(@Validated PageValidate pageValidate) {
        PageResult<DistributionSurveyFansVo> list = iDistributionSurveyService.topFans(pageValidate);
        return AjaxResult.success(list);
    }

}
