package com.mdd.admin.controller.finance;

import com.mdd.admin.service.IFinanceEarningsService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.finance.FinanceEarningsSearchValidate;
import com.mdd.admin.vo.finance.FinanceEarningsListVo;
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
@RequestMapping("api/finance/earnings")
@Api("佣金明细管理")
public class FinanceEarningsController {

    @Resource
    IFinanceEarningsService iFinanceEarningsService;

    @GetMapping("/list")
    @ApiOperation("记录列表")
    public AjaxResult<Object> list(@Validated PageValidate pageValidate,
                                   @Validated FinanceEarningsSearchValidate searchValidate) {
        PageResult<FinanceEarningsListVo> list = iFinanceEarningsService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

}
