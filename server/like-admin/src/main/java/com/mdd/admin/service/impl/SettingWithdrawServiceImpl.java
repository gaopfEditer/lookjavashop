package com.mdd.admin.service.impl;

import com.mdd.admin.service.ISettingWithdrawService;
import com.mdd.admin.validate.setting.SettingWithdrawValidate;
import com.mdd.admin.vo.setting.SettingWithdrawVo;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.ListUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 提现设置服务实现类
 */
@Service
public class SettingWithdrawServiceImpl implements ISettingWithdrawService {

    /**
     * 提现设置详情
     *
     * @author mjf
     * @return SettingWithdrawVo
     */
    @Override
    public SettingWithdrawVo detail() {
        Map<String, String> config = ConfigUtils.get("withdraw");
        SettingWithdrawVo vo = new SettingWithdrawVo();
        vo.setWithdrawWay(ListUtils.stringToListAsInt(config.getOrDefault("withdrawWay", "1"), ","));
        vo.setTransferWay(Integer.parseInt(config.getOrDefault("transferWay", "1")));
        vo.setServiceCharge(Double.valueOf(config.getOrDefault("serviceCharge", "1")));
        vo.setMinMoney(new BigDecimal(config.getOrDefault("minMoney", "10")));
        vo.setMaxMoney(new BigDecimal(config.getOrDefault("maxMoney", "100")));
        return vo;
    }

    /**
     * 提现设置保存
     *
     * @author mjf
     * @param withdrawValidate SettingWithdrawValidate
     */
    @Override
    public void save(SettingWithdrawValidate withdrawValidate) {
        ConfigUtils.set("withdraw", "withdrawWay", withdrawValidate.getWithdrawWay());
        ConfigUtils.set("withdraw", "transferWay", String.valueOf(withdrawValidate.getTransferWay()));
        ConfigUtils.set("withdraw", "serviceCharge", String.valueOf(withdrawValidate.getServiceCharge()));
        ConfigUtils.set("withdraw", "minMoney", String.valueOf(withdrawValidate.getMinMoney()));
        ConfigUtils.set("withdraw", "maxMoney", String.valueOf(withdrawValidate.getMaxMoney()));
    }

}
