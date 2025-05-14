package com.mdd.admin.controller.setting;

import com.mdd.admin.service.ISettingTradeService;
import com.mdd.admin.validate.setting.SettingProtocolValidate;
import com.mdd.admin.validate.setting.SettingTradeValidate;
import com.mdd.admin.vo.setting.SettingTradeVo;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/setting/trade")
@Api(tags = "配置交易参数")
public class SettingTradeController {

    @Resource
    ISettingTradeService iSettingTradeService;

    @GetMapping("/detail")
    @ApiOperation("交易配置详情")
    public AjaxResult<Object> detail() {
        SettingTradeVo vo = iSettingTradeService.detail();
        return AjaxResult.success(vo);
    }

    @PostMapping("/save")
    @ApiOperation("交易配置保存")
    public AjaxResult<Object> save(@Validated @RequestBody SettingTradeValidate tradeValidate) {
        iSettingTradeService.save(tradeValidate);
        return AjaxResult.success();
    }

}
