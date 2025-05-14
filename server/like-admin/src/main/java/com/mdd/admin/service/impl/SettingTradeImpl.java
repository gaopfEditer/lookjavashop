package com.mdd.admin.service.impl;

import com.mdd.admin.service.ISettingTradeService;
import com.mdd.admin.validate.setting.SettingTradeValidate;
import com.mdd.admin.vo.setting.SettingTradeVo;
import com.mdd.common.util.ConfigUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 交易设置服务实现类
 */
@Service
public class SettingTradeImpl implements ISettingTradeService {

    /**
     * 交易设置详情
     *
     * @author fzr
     * @return SettingTradeVo
     */
    @Override
    public SettingTradeVo detail() {
        Map<String, String> config = ConfigUtils.get("trade");

        SettingTradeVo vo = new SettingTradeVo();
        vo.setCancelUnpaidOrderTime(Float.parseFloat(config.getOrDefault("cancelUnpaidOrderTime", "-1")));
        vo.setCancelUnshippedOrderTime(Float.parseFloat(config.getOrDefault("cancelUnshippedOrderTime", "-1")));
        vo.setAutoConfirmReceiptDay(Float.parseFloat(config.getOrDefault("autoConfirmReceiptDay", "-1")));
        vo.setAfterSalesDay(Float.parseFloat(config.getOrDefault("afterSalesDay", "-1")));
        vo.setInventoryOccupancy(Integer.parseInt(config.getOrDefault("inventoryOccupancy", "1")));
        vo.setReturnInventory(Integer.parseInt(config.getOrDefault("returnInventory", "0")));
        vo.setReturnCoupon(Integer.parseInt(config.getOrDefault("returnCoupon", "0")));
        vo.setBeforeExpressRefund(Integer.parseInt(config.getOrDefault("beforeExpressRefund", "0")));
        return vo;
    }

    /**
     * 交易设置保存
     *
     * @author fzr
     * @param tradeValidate 参数
     */
    @Override
    public void save(SettingTradeValidate tradeValidate) {
        ConfigUtils.set("trade", "cancelUnpaidOrderTime", tradeValidate.getCancelUnpaidOrderTime().toString());
        ConfigUtils.set("trade", "cancelUnshippedOrderTime", tradeValidate.getCancelUnshippedOrderTime().toString());
        ConfigUtils.set("trade", "autoConfirmReceiptDay", tradeValidate.getAutoConfirmReceiptDay().toString());
        ConfigUtils.set("trade", "afterSalesDay", tradeValidate.getAfterSalesDay().toString());
        ConfigUtils.set("trade", "inventoryOccupancy", tradeValidate.getInventoryOccupancy().toString());
        ConfigUtils.set("trade", "returnInventory", tradeValidate.getReturnInventory().toString());
        ConfigUtils.set("trade", "returnCoupon", tradeValidate.getReturnCoupon().toString());
        ConfigUtils.set("trade", "beforeExpressRefund", tradeValidate.getBeforeExpressRefund().toString());

    }

}
