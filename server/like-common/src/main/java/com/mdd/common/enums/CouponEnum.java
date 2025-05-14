package com.mdd.common.enums;

public enum CouponEnum {

    // 使用条件
    CONDITION_TYPE_NOT(1, "无门槛"),
    CONDITION_TYPE_FULL(2, "需订单满足金额"),

    // 发放数量限制
    SEND_TOTAL_TYPE_NOT(1, "无限数量"),
    SEND_TOTAL_TYPE_FIXED(2, "固定数量"),

    // 用券时间
    USE_TIME_TYPE_FIXED(1, "固定时间"),
    USE_TIME_TYPE_TODAY(2, "领券当天起"),
    USE_TIME_TYPE_TOMORROW(3, "领券次日起"),

    // 领取方式
    GET_TYPE_USER(1, "用户领取"),
    GET_TYPE_STORE(2, "系统发放"),

    // 领取数量限制
    GET_NUM_TYPE_NOT(1, "不限制"),
    GET_NUM_TYPE_LIMIT(2, "限制张数"),
    GET_NUM_TYPE_DAY(3, "每天限制张数"),

    // 允许使用商品
    USE_GOODS_TYPE_NOT(1, "全场通用"),
    USE_GOODS_TYPE_ALLOW(2, "部分商品可用"),
    USE_GOODS_TYPE_BAN(3, "部分商品不可用"),

    // 优惠券状态
//    COUPON_STATUS_WAIT(1, "待发布"),
    COUPON_STATUS_NOT(1, "未开始"),
    COUPON_STATUS_CONDUCT(2, "进行中"),
    COUPON_STATUS_END(3, "已结束"),

    // 使用状态
    USE_STATUS_NOT(0, "未使用"),
    USE_STATUS_OK(1, "已使用"),
    USE_STATUS_EXPIRE(2, "已过期"),
    USE_STATUS_VOID(3, "已作废");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;
    CouponEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取状态码
     *
     * @author fzr
     * @return Long
     */
    public int getCode() {
        return this.code;
    }

    /**
     * 获取提示
     *
     * @author fzr
     * @return String
     */
    public String getMsg() {
        return this.msg;
    }

    /**
     * 优惠券状态描述
     *
     * @author fzr
     * @param code 类型
     * @return String
     */
    public static String getCouponStatusMsg(Integer code){
        switch (code) {
            case 1:
                return CouponEnum.COUPON_STATUS_NOT.getMsg();
            case 2:
                return CouponEnum.COUPON_STATUS_CONDUCT.getMsg();
            case 3:
                return CouponEnum.COUPON_STATUS_END.getMsg();
        }
        return "未知";
    }

    /**
     * 优惠券使用状态描述
     *
     * @author fzr
     * @param code 类型
     * @return String
     */
    public static String getUseCouponMsg(Integer code){
        switch (code) {
            case 1:
                return CouponEnum.USE_STATUS_NOT.getMsg();
            case 2:
                return CouponEnum.USE_STATUS_OK.getMsg();
            case 3:
                return CouponEnum.USE_STATUS_EXPIRE.getMsg();
            case 4:
                return CouponEnum.USE_STATUS_VOID.getMsg();
        }
        return "未知";
    }

    /**
     * 发放方式描述
     *
     * @author fzr
     * @param code 类型
     * @return String
     */
    public static String getGetTypeMsg(Integer code) {
        switch (code) {
            case 1:
                return CouponEnum.GET_TYPE_USER.getMsg();
            case 2:
                return CouponEnum.GET_TYPE_STORE.getMsg();
        }
        return "未知";
    }

    /**
     * 适用商品类型描述
     *
     * @author fzr
     * @param code 类型
     * @return String
     */
    public static String getUseGoodsTypeMsg(Integer code) {
        switch (code) {
            case 1:
                return CouponEnum.USE_GOODS_TYPE_NOT.getMsg();
            case 2:
                return CouponEnum.USE_GOODS_TYPE_ALLOW.getMsg();
            case 3:
                return CouponEnum.USE_GOODS_TYPE_BAN.getMsg();
        }
        return "未知";
    }

}
