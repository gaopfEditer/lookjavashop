package com.mdd.admin.controller.delivery;

import com.mdd.admin.service.IExpressCompanyService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.delivery.DeliverCompanyCreateValidate;
import com.mdd.admin.validate.delivery.DeliverCompanyUpdateValidate;
import com.mdd.admin.vo.delivery.DeliverCompanyVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/deliver/express")
@Api("快递公司管理")
public class DeliverCompanyController {
    @Resource
    IExpressCompanyService iExpressCompanyService;

    @GetMapping("/list")
    @ApiOperation("快递列表")
    public AjaxResult<List<DeliverCompanyVo>> list(@Validated PageValidate pageValidate,
                                                   @RequestParam(value = "name", defaultValue = "") String name) {
        List<DeliverCompanyVo> expressCompany = iExpressCompanyService.list(pageValidate, name);
        return AjaxResult.success(expressCompany);
    }

    @GetMapping("/detail")
    @ApiOperation("快递详情")
    public AjaxResult<DeliverCompanyVo> detail(@Validated @IDMust() @RequestParam("id") Integer id){
        DeliverCompanyVo detail = iExpressCompanyService.detail(id);
        return AjaxResult.success(detail);
    }

    @PostMapping("/add")
    @ApiOperation("快递新增")
    public AjaxResult<Object> add(@Validated @RequestBody DeliverCompanyCreateValidate deliverCompanyCreateValidate) {
        iExpressCompanyService.add(deliverCompanyCreateValidate);
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    @ApiOperation("快递编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody DeliverCompanyUpdateValidate deliverCompanyUpdateValidate){
        iExpressCompanyService.edit(deliverCompanyUpdateValidate);
        return AjaxResult.success();
    }

    @PostMapping("/del")
    @ApiOperation("快递删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate){
        iExpressCompanyService.del(idValidate.getId());
        return AjaxResult.success();
    }

}
