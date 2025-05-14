package com.mdd.admin.controller.setting;

import com.mdd.admin.aop.Log;
import com.mdd.admin.service.ISettingWithdrawService;
import com.mdd.admin.validate.setting.SettingWithdrawValidate;
import com.mdd.admin.vo.setting.SettingWithdrawVo;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/setting/withdraw")
@Api(tags = "配置提现参数")
public class SettingWithdrawController {

    @Resource
    ISettingWithdrawService iSettingWithdrawService;

    @GetMapping("/detail")
    @ApiOperation(value="提现设置详情")
    public AjaxResult<SettingWithdrawVo> detail() {
        SettingWithdrawVo vo = iSettingWithdrawService.detail();
        return AjaxResult.success(vo);
    }

    @Log(title = "提现设置编辑")
    @PostMapping("/save")
    @ApiOperation(value="提现设置编辑")
    public AjaxResult<Object> save(@Validated @RequestBody SettingWithdrawValidate withdrawValidate) {
        iSettingWithdrawService.save(withdrawValidate);
        return AjaxResult.success();
    }

}
