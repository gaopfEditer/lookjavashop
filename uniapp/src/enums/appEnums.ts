//菜单主题类型
export enum ThemeEnum {
    LIGHT = 'light',
    DARK = 'dark'
}

// 客户端
export enum ClientEnum {
    MP_WEIXIN = 1, // 微信-小程序
    OA_WEIXIN = 2, // 微信-公众号
    H5 = 3, // H5
    IOS = 5, //苹果
    ANDROID = 6 //安卓
}

export enum SMSEnum {
    LOGIN = 101,
    BIND_MOBILE = 102,
    CHANGE_MOBILE = 103,
    FIND_PASSWORD = 104
}

export enum SearchTypeEnum {
    HISTORY = 'history'
}

// 用户资料
export enum FieldType {
    NONE = '',
    AVATAR = 'avatar',
    USERNAME = 'username',
    NICKNAME = 'nickname',
    SEX = 'sex'
}


// 页面状态
export enum PageStatusEnum {
    LOADING = 'loading',		// 加载中
    NORMAL = 'normal',		    // 正常
    ABNORMAL = 'abnormal',	    // 不正常
}

// 支付来源类型
export enum PayFromType {
    ORDER = 'order',
    USERRECHARGE = 'recharge'
}

// 支付方式类型
export enum PayWayType {
    WALLET = 1,
    WECHAT = 2,
    ALIPAY = 3
}

// 支付结果
export enum PayStatusEnum {
    SUCCESS = 'success',
    FAIL = 'fail',
    PENDING = 'pending'
}