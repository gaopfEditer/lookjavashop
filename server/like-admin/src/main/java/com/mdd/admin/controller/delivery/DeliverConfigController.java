package com.mdd.admin.controller.delivery;

import com.mdd.admin.service.IDeliveryConfigService;
import com.mdd.admin.validate.delivery.DeliverConfigValidate;
import com.mdd.admin.validate.delivery.PickupConfigValidate;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("api/deliver/config")
@Api("物流配置管理")
public class DeliverConfigController {

    @Resource
    IDeliveryConfigService iDeliveryConfigService;

    @GetMapping("/getDeliverConfig")
    @ApiOperation("获取配送方式")
    public AjaxResult<Map<String, Object>> getDeliverConfig() {
        Map<String, Object> config = iDeliveryConfigService.getDeliverConfig();
        return AjaxResult.success(config);
    }

    @PostMapping("/setDeliverConfig")
    @ApiOperation("设置配送方式")
    public AjaxResult<Object> setDeliverConfig(@Validated @RequestBody DeliverConfigValidate deliverConfigValidate) {
        iDeliveryConfigService.setDeliverConfig(deliverConfigValidate);
        return AjaxResult.success();
    }

    @PostMapping("/setPickupConfig")
    @ApiOperation("设置到店自提方式")
    public AjaxResult<Object> setPickupConfig(@Validated @RequestBody PickupConfigValidate pickupConfigValidate) {
        iDeliveryConfigService.setPickupConfig(pickupConfigValidate);
        return AjaxResult.success();
    }


    @GetMapping("/getLogisticsConfig")
    @ApiOperation("获取物流接口")
    public AjaxResult<Map<String,Object>> getLogisticsConfig() {
        Map<String, Object> config = iDeliveryConfigService.getLogisticsConfig();
        return AjaxResult.success(config);
    }

    @PostMapping("/setLogisticsConfig")
    @ApiOperation("设置物流接口")
    public AjaxResult<Object> setLogisticsConfig(@RequestBody Map<String, Object> params){
        iDeliveryConfigService.setDeliverConfig(params);
        return AjaxResult.success();
    }

}
