package com.mdd.front.controller;

import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.front.service.IWithdrawService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.withdraw.WithdrawApplyValidate;
import com.mdd.front.vo.withdraw.WithdrawConfigVo;
import com.mdd.front.vo.withdraw.WithdrawDetailVo;
import com.mdd.front.vo.withdraw.WithdrawListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/withdraw")
@Api(tags = "提现管理")
public class WithdrawController {

    @Resource
    IWithdrawService iWithdrawService;

    @GetMapping("/config")
    @ApiOperation("提现配置")
    public AjaxResult<WithdrawConfigVo> config () {
        Integer userId = LikeFrontThreadLocal.getUserId();
        Integer terminal = LikeFrontThreadLocal.getTerminal();
        WithdrawConfigVo vo = iWithdrawService.config(userId, terminal);
        return AjaxResult.success(vo);
    }

    @PostMapping("/apply")
    @ApiOperation("提现申请")
    public AjaxResult<Object> apply (@Validated @RequestBody WithdrawApplyValidate applyValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        iWithdrawService.apply(userId, applyValidate);
        return AjaxResult.success();
    }

    @GetMapping("/detail")
    @ApiOperation("提现详情")
    public AjaxResult<WithdrawDetailVo> detail(@Validated @IDMust @RequestParam("id") Integer id) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        WithdrawDetailVo detail = iWithdrawService.detail(userId, id);
        return AjaxResult.success(detail);
    }

    @GetMapping("/list")
    @ApiOperation("提现列表")
    public AjaxResult<PageResult<WithdrawListVo>> list(@Validated PageValidate pageValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        PageResult<WithdrawListVo> list = iWithdrawService.list(pageValidate, userId);
        return AjaxResult.success(list);
    }

}
