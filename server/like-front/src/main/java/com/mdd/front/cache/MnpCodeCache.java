package com.mdd.front.cache;

import com.mdd.common.util.RedisUtils;
import com.mdd.common.util.StringUtils;

/**
 * 小程序码缓存
 */
public class MnpCodeCache {

    private static final String KEY = "wechat:mnp:code:";

    public static String get(String keyName) {
        Object o = RedisUtils.get(KEY + keyName);
        if (StringUtils.isNull(o)) {
            return "";
        }
        return o.toString();
    }

    public static void set(String keyName, String data) {
        RedisUtils.set(KEY + keyName, data, 1800);
    }

}
