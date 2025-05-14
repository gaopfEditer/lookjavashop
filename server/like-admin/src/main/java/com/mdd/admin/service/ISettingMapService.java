package com.mdd.admin.service;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.validate.setting.RegionSearchValidate;
import com.mdd.admin.validate.setting.SettingMapValidate;
import com.mdd.admin.validate.setting.SettingWebsiteValidate;
import com.mdd.admin.vo.setting.SettingMapVo;
import com.mdd.admin.vo.setting.SettingWebsiteVo;

/**
 * 网站信息服务接口类
 */
public interface ISettingMapService {

    /**
     * 获取网站信息
     *
     * @author fzr
     * @return SettingWebsiteVo
     */
    SettingMapVo detail();

    /**
     * 保存网站信息
     *
     * @author fzr
     * @param websiteValidate 参数
     */
    void save(SettingMapValidate websiteValidate);

}
