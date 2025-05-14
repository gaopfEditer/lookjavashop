package com.mdd.common.enums;

/**
 * 订单售后日志枚举类
 */
public enum LogOrderAfterEnum {

    // 操作人角色
    TYPE_SYSTEM(1,"系统"),
    TYPE_ADMIN(2,"商家"),// 平台
    TYPE_USER(3,"买家"),

    // 整单退款场景
    BUYER_CANCEL_ORDER(1,"买家取消订单"),
    SELLER_CANCEL_ORDER(2,"卖家取消订单"),
    ORDER_CLOSE(3,"支付回调时订单已关闭"),;


    /**
     * 构造方法
     */
    private final int code;
    private final String msg;

    LogOrderAfterEnum(int code, String msg) {
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

}
