package com.mdd.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.mdd.admin.service.IDeliveryConfigService;
import com.mdd.admin.validate.delivery.DeliverConfigValidate;
import com.mdd.admin.validate.delivery.PickupConfigValidate;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 物流配置实现服务类
 */
@Service
public class DeliveryConfigServiceImpl implements IDeliveryConfigService {

    /**
     * 获取配送方式
     *
     * @author cjh
     * @return Map<String, String>
     */
    @Override
    public Map<String, Object> getDeliverConfig() {
        Map<String, String> method = ConfigUtils.getMap("express", "method");
        Map<String, String> pickUp = ConfigUtils.getMap("pickup", "method");
        if (StringUtils.isNull(method)) {
            method = Collections.emptyMap();
        }
        if (StringUtils.isNull(pickUp)) {
            pickUp = Collections.emptyMap();
        }

        Map<String, Object> config =  new LinkedHashMap<>();
        config.put("expressName", method.getOrDefault("expressName", "快递发货"));
        config.put("expressAlias", method.getOrDefault("expressAlias", "快递发货"));
        config.put("expressIs", Integer.parseInt(method.getOrDefault("expressIs", "0")));

        config.put("pickupName", pickUp.getOrDefault("pickupName", "到店自提"));
        config.put("pickupAlias", pickUp.getOrDefault("pickupAlias", "到店自提"));
        config.put("pickupIs", Integer.parseInt(pickUp.getOrDefault("pickupIs", "0")));

        return config;
    }

    /**
     * 设置配送方式
     *
     * @author cjh
     * @param deliverConfigValidate 参数
     */
    @Override
    public void setDeliverConfig(DeliverConfigValidate deliverConfigValidate) {
        ConfigUtils.set("express", "method", JSON.toJSONString(deliverConfigValidate));
    }

    @Override
    public void setPickupConfig(PickupConfigValidate pickupConfigValidate) {
        ConfigUtils.set("pickup", "method", JSON.toJSONString(pickupConfigValidate));
    }

    /**
     * 获取物流接口
     *
     * @author cjh
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> getLogisticsConfig() {
        String engine = ConfigUtils.get("logistics", "engine", "kd100");
        Map<String, String> kd100 = ConfigUtils.getMap("logistics", "kd100");
        Map<String, String> kdniao = ConfigUtils.getMap("logistics", "kdniao");

        Map<String, String> kd100Vo = new LinkedHashMap<>();
        kd100Vo.put("customer", kd100.getOrDefault("customer", ""));
        kd100Vo.put("key", kd100.getOrDefault("key", ""));

        Map<String, String> kdniaoVo = new LinkedHashMap<>();
        kdniaoVo.put("requestType", kdniao.getOrDefault("requestType", "free"));
        kdniaoVo.put("customer", kdniao.getOrDefault("customer", ""));
        kdniaoVo.put("key", kdniao.getOrDefault("key", ""));

        Map<String, Object> config =  new LinkedHashMap<>();
        config.put("engine", engine);
        config.put("kd100", kd100Vo);
        config.put("kdniao", kdniaoVo);
        return config;
    }

    /**
     * 设置物流接口
     *
     * @author cjh
     * @param params 参数
     */
    @Override
    public void setDeliverConfig(Map<String, Object> params) {
        ConfigUtils.set("logistics", "engine", params.get("engine").toString());
        ConfigUtils.set("logistics", "kd100", JSON.toJSONString(params.get("kd100")));
        ConfigUtils.set("logistics", "kdniao", JSON.toJSONString(params.get("kdniao")));
    }

}
