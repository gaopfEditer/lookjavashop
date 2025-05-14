package com.mdd.front.controller;

import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.front.service.ILogsService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.distribution.DistributionEarningsRecordVo;
import com.mdd.front.vo.recharge.RechargeRecordVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/logs")
@Api(tags = "日志管理")
public class LogsController {

    @Resource
    ILogsService iLogsService;

    @GetMapping("/userMoney")
    @ApiOperation("余额变动日志")
    public AjaxResult<Object> userMoney(@Validated PageValidate pageValidate,
                                        @RequestParam(defaultValue = "0") Integer type) {

        Integer userId = LikeFrontThreadLocal.getUserId();

        PageResult<RechargeRecordVo> list = iLogsService.userMoney(pageValidate, userId, type);
        return AjaxResult.success(list);
    }

    @GetMapping("/userEarnings")
    @ApiOperation("佣金变动日志")
    public AjaxResult<Object> userEarnings(@Validated PageValidate pageValidate,
                                        @RequestParam(defaultValue = "0") Integer type) {

        Integer userId = LikeFrontThreadLocal.getUserId();

        PageResult<DistributionEarningsRecordVo> list = iLogsService.userEarnings(pageValidate, userId, type);
        return AjaxResult.success(list);
    }

}
