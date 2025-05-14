package com.mdd.admin.controller.distribution;

import com.mdd.admin.service.IDistributionOrderService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.DistributionOrderSearchValidate;
import com.mdd.admin.vo.distribution.DistributionOrderListedVo;
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
@RequestMapping("api/distribution/order")
@Api(tags = "分销订单")
public class DistributionOrderController {

    @Resource
    IDistributionOrderService iDistributionOrderService;

    @GetMapping("/list")
    @ApiOperation("分销订单列表")
    public AjaxResult<PageResult<DistributionOrderListedVo>> list(@Validated PageValidate pageValidate,
                                                                  @Validated DistributionOrderSearchValidate searchValidate) {
        PageResult<DistributionOrderListedVo> list = iDistributionOrderService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }


}
