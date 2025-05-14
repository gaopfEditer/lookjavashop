package com.mdd.admin.controller.distribution;

import com.mdd.admin.service.IDistributionConfigService;
import com.mdd.admin.validate.distribution.DistributionConfigValidate;
import com.mdd.admin.vo.distribution.DistributionConfigVo;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/distribution/config")
@Api(tags = "分销设置管理")
public class DistributionConfigController {

    @Resource
    IDistributionConfigService iDistributionConfigService;

    @GetMapping("/detail")
    @ApiOperation("分销配置详情")
    public AjaxResult<DistributionConfigVo> detail() {
        DistributionConfigVo vo = iDistributionConfigService.detail();
        return AjaxResult.success(vo);
    }

    @PostMapping("/save")
    @ApiOperation("分销配置保存")
    public AjaxResult<Object> save(@Validated @RequestBody DistributionConfigValidate configValidate) {
        iDistributionConfigService.save(configValidate);
        return AjaxResult.success();
    }

}
