package com.mdd.admin.controller.distribution;

import com.mdd.admin.service.IDistributionApplyService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.DistributionApplyAuditValidate;
import com.mdd.admin.validate.distribution.DistributionApplySearchValidate;
import com.mdd.admin.vo.distribution.DistributionApplyDetailVo;
import com.mdd.admin.vo.distribution.DistributionApplyListedVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/distribution/apply")
@Api(tags = "分销申请管理")
public class DistributionApplyController {

    @Resource
    IDistributionApplyService iDistributionApplyService;

    @GetMapping("/list")
    @ApiOperation("分销申请列表")
    public AjaxResult<PageResult<DistributionApplyListedVo>> list(@Validated PageValidate pageValidate,
                                                                  @Validated DistributionApplySearchValidate searchValidate) {
        PageResult<DistributionApplyListedVo> list = iDistributionApplyService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation("分销申请详情")
    public AjaxResult<DistributionApplyDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        DistributionApplyDetailVo vo = iDistributionApplyService.detail(id);
        return AjaxResult.success(vo);
    }

    @PostMapping("/audit")
    @ApiOperation("分销申请审核")
    public AjaxResult<Object> audit(@Validated @RequestBody DistributionApplyAuditValidate auditValidate) {
        iDistributionApplyService.audit(auditValidate);
        return AjaxResult.success();
    }

}
