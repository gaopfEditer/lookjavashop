package com.mdd.common.enums;

/**
 * 购物车枚举类
 */
public enum CartEnum {
    // 订单状态
    CART_GOODS_NORMAL(0,"商品正常"),
    CART_GOODS_DOWN(1,"商品下架"),
    CART_GOODS_DELETED(2,"商品删除"),
    CART_ORDER_SKU_DELETED(3,"规格删除"),
    CART_ORDER_SKU_STOCK_LACK(4,"库存不足");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;

    CartEnum(int code, String msg) {
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
     * 获取提示
     *
     * @author mjf
     * @return String
     */
    public String getMsg() {
        return this.msg;
    }

}
