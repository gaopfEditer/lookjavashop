package com.mdd.admin.controller.setting;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.aop.Log;
import com.mdd.admin.service.ISettingCopyrightService;
import com.mdd.admin.service.ISettingMapService;
import com.mdd.admin.validate.setting.RegionSearchValidate;
import com.mdd.admin.validate.setting.SettingCopyrightValidate;
import com.mdd.admin.validate.setting.SettingMapValidate;
import com.mdd.admin.vo.setting.SettingCopyrightVo;
import com.mdd.admin.vo.setting.SettingMapVo;
import com.mdd.common.aop.NotPower;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = {"api/setting/map"})
@Api(tags = "配置网站地图Key")
public class SettingMapConfigController {

    @Resource
    ISettingMapService iSettingMapService;

    @GetMapping("/detail")
    @ApiOperation(value="网站地图Key信息")
    @NotPower
    public AjaxResult<SettingMapVo> detail() {
        SettingMapVo data = iSettingMapService.detail();
        return AjaxResult.success(data);
    }

    @Log(title = "网站地图Key信息编辑")
    @PostMapping("/save")
    @ApiOperation(value="网站地图Key信息编辑")
    public AjaxResult<Object> save(@Validated @RequestBody SettingMapValidate settingMapValidate) {
        iSettingMapService.save(settingMapValidate);
        return AjaxResult.success();
    }
}
