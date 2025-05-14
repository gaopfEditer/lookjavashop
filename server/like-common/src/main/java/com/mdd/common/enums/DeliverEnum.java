package com.mdd.common.enums;

public enum DeliverEnum {

    // 配送状态
    NOT_SHIPPED(0,"未发货"),
    SHIPPED(1,"已发货"),

    // 发货方式
    EXPRESS(1,"快递配送"),
    PICK_UP(2, "到店自提"),
    SAME_CITY(3, "同城配送"),
    VIRTUAL_EXPRESS(4, "虚拟发货"),
    NO_EXPRESS(0,"无需快递");
    //NO_EXPRESS(2,"无需快递");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;

    DeliverEnum(int code, String msg) {
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
     * 发货方式描述
     *
     * @author fzr
     * @param code 类型
     * @return String
     */
    public static String getDeliverIsMsg(Integer code){
        switch (code) {
            case 0:
                return "未发货";
            case 1:
                return "已发货";
        }
        return "未知";
    }

    /**
     * 配送类型描述
     *
     * @author fzr
     * @param code 类型
     * @return String
     */
    public static String getDeliverTypeMsg(Integer code){
        switch (code) {
            case 1:
                return "快递配送";
            case 2:
                return "到店自提";
            case 3:
                return "同城配送";
            case 4:
                return "虚拟发货";
            case 0:
                return "无需快递";
            default:
        }
        return "未知";
    }
}
