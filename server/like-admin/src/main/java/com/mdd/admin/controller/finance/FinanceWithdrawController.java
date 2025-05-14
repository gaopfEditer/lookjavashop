package com.mdd.admin.controller.finance;

import com.mdd.admin.service.IFinanceWithdrawService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.finance.FinanceWithdrawAuditValidate;
import com.mdd.admin.validate.finance.FinanceWithdrawSearchValidate;
import com.mdd.admin.validate.finance.FinanceWithdrawTransferValidate;
import com.mdd.admin.vo.finance.FinanceWithdrawDetailVo;
import com.mdd.admin.vo.finance.FinanceWithdrawListVo;
import com.mdd.admin.vo.finance.FinanceWithdrawQueryVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/finance/withdraw")
@Api("提现管理")
public class FinanceWithdrawController {

    @Resource
    IFinanceWithdrawService iFinanceWithdrawService;

    @GetMapping("/list")
    @ApiOperation("提现列表")
    public AjaxResult<PageResult<FinanceWithdrawListVo>> list(@Validated PageValidate pageValidate,
                                                              @Validated FinanceWithdrawSearchValidate searchValidate) {
        PageResult<FinanceWithdrawListVo> list = iFinanceWithdrawService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation("提现详情")
    public AjaxResult<FinanceWithdrawDetailVo> detail(@Validated @IDMust @RequestParam("id") Integer id) {
        FinanceWithdrawDetailVo vo = iFinanceWithdrawService.detail(id);
        return AjaxResult.success(vo);
    }

    @PostMapping("/audit")
    @ApiOperation("提现审核")
    public AjaxResult<Object> audit(@Validated @RequestBody FinanceWithdrawAuditValidate auditValidate) {
        iFinanceWithdrawService.audit(auditValidate);
        return AjaxResult.success();
    }

    @PostMapping("/transfer")
    @ApiOperation("提现转账")
    public AjaxResult<Object> transfer(@Validated @RequestBody FinanceWithdrawTransferValidate transferValidate) {
        iFinanceWithdrawService.transfer(transferValidate);
        return AjaxResult.success();
    }

    @GetMapping("/query")
    @ApiOperation("提现结果查询")
    public AjaxResult<FinanceWithdrawQueryVo> query(@Validated @IDMust @RequestParam("id") Integer id) {
        FinanceWithdrawQueryVo result = iFinanceWithdrawService.getWithdrawResult(id);
        return AjaxResult.success(result);
    }

}
