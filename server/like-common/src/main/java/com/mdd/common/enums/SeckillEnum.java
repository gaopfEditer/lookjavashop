package com.mdd.common.enums;

/**
 * 秒杀枚举类
 */
public enum SeckillEnum {

    // 配送状态
    STATUS_WAIT(1, "待发布"),
    STATUS_NOT(2, "未开始"),
    STATUS_CONDUCT(3, "进行中"),
    STATUS_END(4, "已结束");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;

    SeckillEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取状态码
     *
     * @return Long
     * @author fzr
     */
    public int getCode() {
        return this.code;
    }

    /**
     * 获取提示
     *
     * @return String
     * @author fzr
     */
    public String getMsg() {
        return this.msg;
    }

}
