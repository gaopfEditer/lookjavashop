package com.mdd.admin.service;

import com.mdd.admin.validate.setting.SettingTradeValidate;
import com.mdd.admin.vo.setting.SettingTradeVo;

/**
 * 交易设置服务接口类
 */
public interface ISettingTradeService {

    SettingTradeVo detail();

    void save(SettingTradeValidate tradeValidate);

}
