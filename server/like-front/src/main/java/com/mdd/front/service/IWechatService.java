package com.mdd.front.service;

import com.mdd.front.validate.wechat.WechatMnpCodeValidate;
import com.mdd.front.vo.wechat.WechatMnpCodeVo;

import java.util.Map;

/**
 * 微信服务接口类
 */
public interface IWechatService {

    /**
     * 公众号JsConfig
     *
     * @author fzr
     * @param url 链接
     * @return Map<String, Object>
     * @throws Exception 异常
     */
    Map<String, Object> jsConfig(String url) throws Exception;

    /**
     * 小程序码
     *
     * @author mjf
     * @param mnpCodeValidate WechatMnpCodeValidate
     * @return WechatMnpCodeVo
     */
    WechatMnpCodeVo mnpCode (WechatMnpCodeValidate mnpCodeValidate);

}
