package com.mdd.common.enums;

/**
 * 订单商品枚举类
 */
public enum OrderGoodsEnum {

    // 商品售后状态
    AFTER_STATUS_NO(1, "无售后"),
    AFTER_STATUS_ING(2, "售后中"),
    AFTER_STATUS_FINISH(3, "售后完成"),
    AFTER_STATUS_REJECT(4, "售后拒绝"),

    //自提信息
    VERIFYSTATUS_TRUE(1, "已自提"),
    VERIFYSTATUS_FALSE(0, "未自提"),
    //自提信息
    VERIFICATION_SCENE_SYSTEM(0, "系统"),
    VERIFICATION_SCENE_ADMIN(1, "管理员"),
    VERIFICATION_SCENE_CUST(2, "会员"),

    // 商品类型
    GOODS_TYPE_NORMAL(1, "普通商品"),
    GOODS_TYPE_SECKILL(2, "秒杀商品");



    /**
     * 构造方法
     */
    private final int code;
    private final String msg;

    OrderGoodsEnum(int code, String msg) {
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
