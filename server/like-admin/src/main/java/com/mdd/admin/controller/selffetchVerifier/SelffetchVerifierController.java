package com.mdd.admin.controller.selffetchVerifier;

import com.mdd.admin.aop.Log;
import com.mdd.admin.service.ISelffetchVerifierService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.selffetchVerifier.SelffetchVerifierCreateValidate;
import com.mdd.admin.validate.selffetchVerifier.SelffetchVerifierStatusValidate;
import com.mdd.admin.validate.selffetchVerifier.SelffetchVerifierUpdateValidate;
import com.mdd.admin.validate.selffetchVerifier.SelffetchVerifierSearchValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.selffetchshop.SelffetchShopStatusValidate;
import com.mdd.admin.vo.selffetchVerifier.SelffetchVerifierListedVo;
import com.mdd.admin.vo.selffetchVerifier.SelffetchVerifierDetailVo;
import com.mdd.common.aop.NotPower;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/selffetchVerifier")
@Api(tags = "核销员管理")
public class SelffetchVerifierController {

    @Resource
    ISelffetchVerifierService iSelffetchVerifierService;

    @GetMapping("/list")
    @ApiOperation(value="核销员列表")
    public AjaxResult<PageResult<SelffetchVerifierListedVo>> list(@Validated PageValidate pageValidate,
                                                     @Validated SelffetchVerifierSearchValidate searchValidate) {
        PageResult<SelffetchVerifierListedVo> list = iSelffetchVerifierService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation(value="核销员详情")
    @NotPower
    public AjaxResult<SelffetchVerifierDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        SelffetchVerifierDetailVo detail = iSelffetchVerifierService.detail(id);
        return AjaxResult.success(detail);
    }

    @Log(title = "核销员新增")
    @PostMapping("/add")
    @ApiOperation(value="核销员新增")
    public AjaxResult<Object> add(@Validated @RequestBody SelffetchVerifierCreateValidate createValidate) {
        iSelffetchVerifierService.add(createValidate);
        return AjaxResult.success();
    }

    @Log(title = "核销员编辑")
    @PostMapping("/edit")
    @ApiOperation(value="核销员编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody SelffetchVerifierUpdateValidate updateValidate) {
        iSelffetchVerifierService.edit(updateValidate);
        return AjaxResult.success();
    }

    @Log(title = "核销员删除")
    @PostMapping("/del")
    @ApiOperation(value="核销员删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iSelffetchVerifierService.del(idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("/status")
    @ApiOperation(value="自提门店状态修改")
    public AjaxResult<Object> status(@Validated @RequestBody SelffetchVerifierStatusValidate selffetchVerifierStatusValidate) {
        iSelffetchVerifierService.status(selffetchVerifierStatusValidate);
        return AjaxResult.success();
    }

}
