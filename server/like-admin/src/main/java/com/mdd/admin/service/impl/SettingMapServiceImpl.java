package com.mdd.admin.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.service.IIndexService;
import com.mdd.admin.service.ISettingMapService;
import com.mdd.admin.service.ISettingWebsiteService;
import com.mdd.admin.validate.setting.RegionSearchValidate;
import com.mdd.admin.validate.setting.SettingMapValidate;
import com.mdd.admin.validate.setting.SettingWebsiteValidate;
import com.mdd.admin.vo.setting.SettingMapVo;
import com.mdd.admin.vo.setting.SettingWebsiteVo;
import com.mdd.common.exception.OperateException;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.HttpUtils;
import com.mdd.common.util.MapUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 网站信息配置服务实现类
 */
@Service
public class SettingMapServiceImpl implements ISettingMapService {

    @Resource
    IIndexService iIndexService;

    /**
     * 获取腾讯地图配置信息
     *
     * @author fzr
     * @return Map<String, String>
     */
    @Override
    public SettingMapVo detail() {
        Map<String, String> config = ConfigUtils.get("map");
        SettingMapVo vo = new SettingMapVo();
        vo.setTencentMapKey(config.getOrDefault("tencentMapKey", ""));
        return vo;
    }

    /**
     * 保存腾讯地图配置信息
     *
     * @author fzr
     * @param settingMapValidate 参数
     */
    @Override
    public void save(SettingMapValidate settingMapValidate) {
        ConfigUtils.set("map", "tencentMapKey", settingMapValidate.getTencentMapKey());
    }

}
