package com.mdd.front.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaQrcodeService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import com.mdd.front.cache.MnpCodeCache;
import com.mdd.front.validate.wechat.WechatMnpCodeValidate;
import com.mdd.front.vo.wechat.WechatMnpCodeVo;
import com.mdd.common.exception.OperateException;
import com.mdd.common.plugin.wechat.WxMnpDriver;
import com.mdd.common.util.*;
import com.mdd.front.service.IWechatService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 微信服务实现类
 */
@Service
public class WechatServiceImpl implements IWechatService {

    /**
     * 公众号JsConfig
     * 微信公众号拉起支付时需要用到
     *
     * @author fzr
     * @param url 链接
     * @return Map<String, Object>
     * @throws Exception 异常
     */
    @Override
    public Map<String, Object> jsConfig(String url) throws Exception {
        String appId = ConfigUtils.get("oa_channel", "appId");
        String accessToken = WxMnpDriver.mnp().getAccessToken();
        String jsapiTicket = WxMnpDriver.getJsSdkGetTicket(accessToken);
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String nonceStr = UUID.randomUUID().toString();
        String signature = WxMnpDriver.buildJSSDKSignature(jsapiTicket,timestamp,nonceStr,url);

        List<String> array = Arrays.asList(
                "onMenuShareTimeline",
                "onMenuShareAppMessage",
                "onMenuShareQQ",
                "onMenuShareWeibo",
                "onMenuShareQZone",
                "openLocation",
                "getLocation",
                "chooseWXPay",
                "updateAppMessageShareData",
                "updateTimelineShareData",
                "openAddress",
                "scanQRCode");

        Map<String,Object> map = new HashMap<>();
        map.put("url", url);
        map.put("jsapi_ticket", jsapiTicket);
        map.put("nonceStr", nonceStr);
        map.put("timestamp", timestamp);
        map.put("signature", signature);
        map.put("appId", appId);
        map.put("jsApiList", array);
        return map;
    }


    /**
     * 小程序码
     *
     * @author mjf
     * @param mnpCodeValidate WechatMnpCodeValidate
     * @return WechatMnpCodeVo
     */
    @Override
    public WechatMnpCodeVo mnpCode (WechatMnpCodeValidate mnpCodeValidate) {
        try {
            String scene = mnpCodeValidate.getScene();
            String page = mnpCodeValidate.getPage();
            Integer width = 430;
            if (StringUtils.isNotNull(mnpCodeValidate.getWidth())) {
                width = mnpCodeValidate.getWidth();
            }

            // 优先查缓存
            String CacheKey = scene + page + width;
            String CacheData = MnpCodeCache.get(CacheKey);
            if (StringUtils.isNotEmpty(CacheData)) {
                WechatMnpCodeVo vo = new WechatMnpCodeVo();
                vo.setImage(CacheData);
                return vo;
            }
            
            WxMaService wxMaService = WxMnpDriver.mnp();
            WxMaQrcodeService qrcodeService = wxMaService.getQrcodeService();

            // 设置小程序二维码线条颜色为黑色
            WxMaCodeLineColor lineColor = new WxMaCodeLineColor();
            // 生成二维码图片字节流
            byte[] qrcodeFile = qrcodeService.createWxaCodeUnlimitBytes(scene, page, false, "release", width, false, lineColor, false);
            String imageData = "data:image/jpeg;base64," + Base64Util.encode(qrcodeFile);

            // 设置缓存
            MnpCodeCache.set(CacheKey, imageData);

            WechatMnpCodeVo vo = new WechatMnpCodeVo();
            vo.setImage(imageData);
            return vo;

        } catch (WxErrorException e) {
            throw new OperateException(e.getError().getErrorCode() + ": " + e.getError().getErrorMsg());
        } catch (Exception e) {
            throw new OperateException(e.getMessage());
        }
    }


}
