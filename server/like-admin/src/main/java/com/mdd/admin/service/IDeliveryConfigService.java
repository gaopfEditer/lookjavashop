package com.mdd.admin.service;

import com.mdd.admin.validate.delivery.DeliverConfigValidate;
import com.mdd.admin.validate.delivery.PickupConfigValidate;

import java.util.Map;

/**
 * 物流配置接口服务类
 */
public interface IDeliveryConfigService {

    /**
     * 获取配送方式
     *
     * @author cjh
     * @return Map<String, String>
     */
    Map<String, Object> getDeliverConfig();

    /**
     * 设置配送方式
     *
     * @author cjh
     * @param deliverConfigValidate 参数
     */
    void setDeliverConfig(DeliverConfigValidate deliverConfigValidate);

    /**
     * 设置配送方式
     *
     * @author cjh
     * @param deliverConfigValidate 参数
     */
    void setPickupConfig(PickupConfigValidate pickupConfigValidate);

    /**
     * 获取物流接口
     *
     * @author cjh
     * @return Map<String, Object>
     */
    Map<String, Object> getLogisticsConfig();

    /**
     * 设置物流接口
     *
     * @author cjh
     * @param params 参数
     */
    void setDeliverConfig(Map<String, Object> params);

}
