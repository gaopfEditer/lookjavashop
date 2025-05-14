package com.mdd.admin.service.impl;

import com.mdd.admin.service.IIndexService;
import com.mdd.admin.service.ISettingWebsiteService;
import com.mdd.admin.validate.setting.SettingWebsiteValidate;
import com.mdd.admin.vo.setting.SettingWebsiteVo;
import com.mdd.common.exception.OperateException;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 网站信息配置服务实现类
 */
@Service
public class SettingWebsiteServiceImpl implements ISettingWebsiteService {

    @Resource
    IIndexService iIndexService;

    /**
     * 获取网站信息
     *
     * @author fzr
     * @return Map<String, String>
     */
    @Override
    public SettingWebsiteVo detail() {
        Map<String, String> config = ConfigUtils.get("website");

        SettingWebsiteVo vo = new SettingWebsiteVo();
        vo.setName(config.getOrDefault("name", ""));
        vo.setLogo(UrlUtils.toAbsoluteUrl(config.getOrDefault("logo", "")));
        vo.setFavicon(UrlUtils.toAbsoluteUrl(config.getOrDefault("favicon", "")));
        vo.setBackdrop(UrlUtils.toAbsoluteUrl(config.getOrDefault("backdrop", "")));
        vo.setIsShowDoc(Integer.parseInt(config.getOrDefault("isShowDoc", "1")));

        vo.setShopName(config.getOrDefault("shopName", ""));
        vo.setShopLogo(UrlUtils.toAbsoluteUrl(config.getOrDefault("shopLogo", "")));
        vo.setShopStatus(Integer.parseInt(config.getOrDefault("shopStatus", "1")));

        vo.setPcLogo(UrlUtils.toAbsoluteUrl(config.getOrDefault("pcLogo", "")));
        vo.setPcIco(UrlUtils.toAbsoluteUrl(config.getOrDefault("pcIco", "")));
        vo.setPcTitle(config.getOrDefault("pcTitle", ""));
        vo.setPcDesc(config.getOrDefault("pcDesc", ""));
        vo.setPcKeywords(config.getOrDefault("pcKeywords", ""));

        Map<String, String> contactConfig = ConfigUtils.get("contact");
        vo.setContactNickname(contactConfig.getOrDefault("nickname", ""));
        vo.setContactMobile(contactConfig.getOrDefault("mobile", ""));

        Map<String, String> retreatConfig = ConfigUtils.get("retreat");
        vo.setRetreatConsignee(retreatConfig.getOrDefault("consignee", ""));
        vo.setRetreatMobile(retreatConfig.getOrDefault("mobile", ""));
        vo.setRetreatProvinceId(retreatConfig.getOrDefault("province_id", ""));
        vo.setRetreatCityId(retreatConfig.getOrDefault("city_id", ""));
        vo.setRetreatDistrictId(retreatConfig.getOrDefault("district_id", ""));
        vo.setRetreatAddress(retreatConfig.getOrDefault("address", ""));
        return vo;
    }

    /**
     * 保存网站信息
     *
     * @author fzr
     * @param websiteValidate 参数
     */
    @Override
    public void save(SettingWebsiteValidate websiteValidate) {

        if (websiteValidate.getIsShowDoc().equals(0)) {
            Map<String, Object> result = iIndexService.checkAuth();
            if (result.get("result").equals(false)) {
                throw new OperateException("产品未授权，【文档信息】不可隐藏，需前往官网授权后才能操作");
            }
        }

        ConfigUtils.set("website", "name", websiteValidate.getName());
        ConfigUtils.set("website", "logo", UrlUtils.toRelativeUrl(websiteValidate.getLogo()));
        ConfigUtils.set("website", "favicon", UrlUtils.toRelativeUrl(websiteValidate.getFavicon()));
        ConfigUtils.set("website", "backdrop", UrlUtils.toRelativeUrl(websiteValidate.getBackdrop()));
        ConfigUtils.set("website", "isShowDoc",String.valueOf(websiteValidate.getIsShowDoc()));

        ConfigUtils.set("website", "shopName", websiteValidate.getShopName());
        ConfigUtils.set("website", "shopLogo", UrlUtils.toRelativeUrl(websiteValidate.getShopLogo()));
        ConfigUtils.set("website", "shopStatus",String.valueOf(websiteValidate.getShopStatus()));

        ConfigUtils.set("website", "pcLogo", UrlUtils.toRelativeUrl(websiteValidate.getPcLogo()));
        ConfigUtils.set("website", "pcIco", UrlUtils.toRelativeUrl(websiteValidate.getPcIco()));
        ConfigUtils.set("website", "pcTitle", websiteValidate.getPcTitle());
        ConfigUtils.set("website", "pcDesc", websiteValidate.getPcDesc());
        ConfigUtils.set("website", "pcKeywords", websiteValidate.getPcKeywords());

        ConfigUtils.set("contact", "nickname", websiteValidate.getContactNickname());
        ConfigUtils.set("contact", "mobile", websiteValidate.getContactMobile());

        ConfigUtils.set("retreat", "consignee", websiteValidate.getRetreatConsignee());
        ConfigUtils.set("retreat", "mobile", websiteValidate.getRetreatMobile());
        ConfigUtils.set("retreat", "province_id", websiteValidate.getRetreatProvinceId());
        ConfigUtils.set("retreat", "city_id", websiteValidate.getRetreatCityId());
        ConfigUtils.set("retreat", "district_id", websiteValidate.getRetreatDistrictId());
        ConfigUtils.set("retreat", "address", websiteValidate.getRetreatAddress());
    }

}
