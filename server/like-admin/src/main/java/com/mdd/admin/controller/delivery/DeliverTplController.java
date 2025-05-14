package com.mdd.admin.controller.delivery;

import com.mdd.admin.service.IExpressTplService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.delivery.DeliverTplCreateValidate;
import com.mdd.admin.validate.delivery.DeliverTplUpdateValidate;
import com.mdd.admin.vo.delivery.DeliverTplListedVo;
import com.mdd.admin.vo.delivery.DeliverTplDetailVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/deliver/tpl")
@ApiModel("运费模板管理")
public class DeliverTplController {

    @Resource
    IExpressTplService iExpressTplService;

    @GetMapping("/list")
    @ApiOperation("运费模板列表")
    public AjaxResult<List<DeliverTplListedVo>> list(){
        List<DeliverTplListedVo> list = iExpressTplService.list();
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation("运费模板详情")
    public AjaxResult<DeliverTplDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        DeliverTplDetailVo detail = iExpressTplService.detail(id);
        return AjaxResult.success(detail);
    }

    @PostMapping("/add")
    @ApiOperation("运费模板新增")
    public AjaxResult<Object> add(@Validated @RequestBody DeliverTplCreateValidate deliverTplCreateValidate){
        iExpressTplService.add(deliverTplCreateValidate);
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    @ApiOperation("运费模板编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody DeliverTplUpdateValidate deliverTplUpdateValidate){
        iExpressTplService.edit(deliverTplUpdateValidate);
        return AjaxResult.success();
    }

    @PostMapping("/del")
    @ApiOperation("运费模板删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iExpressTplService.del(idValidate.getId());
        return AjaxResult.success();
    }

}
