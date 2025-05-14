package com.mdd.admin.service;

import com.mdd.admin.validate.setting.SettingWithdrawValidate;
import com.mdd.admin.vo.setting.SettingWithdrawVo;

/**
 * 提现设置服务接口类
 */
public interface ISettingWithdrawService {

    /**
     * 提现设置详情
     *
     * @author mjf
     * @return SettingUserVo
     */
    SettingWithdrawVo detail();

    /**
     * 提现设置保存
     *
     * @author mjf
     * @param withdrawValidate SettingWithdrawValidate
     */
    void save(SettingWithdrawValidate withdrawValidate);

}
