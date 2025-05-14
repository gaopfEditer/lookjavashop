package com.mdd.common.enums;

/**
 * 订单日志枚举类
 */
public enum OrderLogEnum {

    TYPE_SYSTEM(1,"系统"),
    TYPE_ADMIN(2,"商家"),
    TYPE_USER(3,"买家"),

    CHANNEL_ADD_ORDER(100,"用户下单"),
    CHANNEL_PAY_ORDER(101,"用户支付订单"),
    CHANNEL_USER_CANCEL_ORDER(102,"用户取消订单"),
    CHANNEL_ADMIN_CANCEL_ORDER(103,"管理员取消订单"),
    CHANNEL_SYSTEM_CANCEL_ORDER(104,"系统取消订单"),
    CHANNEL_USER_DEL_ORDER(105,"用户删除订单"),
    CHANNEL_USER_CONFIRM_ORDER(106,"用户确认收货"),
    CHANNEL_SHOP_DELIVERY_ORDER(107,"商家确认发货"),
    CHANNEL_SHOP_CONFIRM_ORDER(108,"商家确认收货"),
    CHANNEL_SYSTEM_CONFIRM_ORDER(109,"系统确认收货");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;

    OrderLogEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取状态码
     *
     * @author mjf
     * @return int
     */
    public int getCode() {
        return this.code;
    }

    /**
     * 获取提示信息
     *
     * @author mjf
     * @return String
     */
    public String getMsg() {
        return this.msg;
    }

    /**
     * 获取变动类型
     *
     * @author mjf
     * @param code Integer
     * @return String
     */
    public static String getValue(Integer code) {
        for (OrderLogEnum value : OrderLogEnum.values()) {
            if (value.getCode() == code) {
                return value.getMsg();
            }
        }
        return "-";
    }
}
