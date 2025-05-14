package com.mdd.admin.controller;

import com.mdd.admin.aop.Log;
import com.mdd.admin.service.IVerificationService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.VerificationCreateValidate;
import com.mdd.admin.validate.VerificationUpdateValidate;
import com.mdd.admin.validate.VerificationSearchValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.VerificationListedVo;
import com.mdd.admin.vo.VerificationDetailVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/verification")
@Api(tags = "核销管理")
public class VerificationController {

    @Resource
    IVerificationService iVerificationService;

    @GetMapping("/list")
    @ApiOperation(value="核销列表")
    public AjaxResult<PageResult<VerificationListedVo>> list(@Validated PageValidate pageValidate,
                                                     @Validated VerificationSearchValidate searchValidate) {
        PageResult<VerificationListedVo> list = iVerificationService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation(value="核销详情")
    public AjaxResult<VerificationDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        VerificationDetailVo detail = iVerificationService.detail(id);
        return AjaxResult.success(detail);
    }

    @Log(title = "核销新增")
    @PostMapping("/add")
    @ApiOperation(value="核销新增")
    public AjaxResult<Object> add(@Validated @RequestBody VerificationCreateValidate createValidate) {
        iVerificationService.add(createValidate);
        return AjaxResult.success();
    }

    @Log(title = "核销编辑")
    @PostMapping("/edit")
    @ApiOperation(value="核销编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody VerificationUpdateValidate updateValidate) {
        iVerificationService.edit(updateValidate);
        return AjaxResult.success();
    }

    @Log(title = "核销删除")
    @PostMapping("/del")
    @ApiOperation(value="核销删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iVerificationService.del(idValidate.getId());
        return AjaxResult.success();
    }

}
