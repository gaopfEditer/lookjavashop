package com.mdd.common.enums;

/**
 * 订单枚举类
 */
public enum OrderEnum {
    // 订单状态
    ORDER_STATUS_WAIT_PAY(1,"待支付"),
    ORDER_STATUS_WAIT_DELIVER(2,"待发货"),
    ORDER_STATUS_TAKE_DELIVER(3,"待收货"),
    ORDER_STATUS_PICKUP_DELIVER(3,"待取货"),
    ORDER_STATUS_COMPLETED(4,"已完成"),
    ORDER_STATUS_CANCEL(5,"已取消"),

    // 订单类型
    ORDER_SOURCE_NORMAL(1, "普通订单"),
    ORDER_SOURCE_SECKILL(2, "秒杀订单"),

    // 配送类型
    DELIVERY_TYPE_EXPRESS(1, "快递"),
    DELIVERY_TYPE_PICK(2, "到店自提"),
    DELIVERY_VIRTUAL(4, "虚拟发货"),

    VERIFYSTATUS_TRUE(1, "已核销"),
    VERIFYSTATUS_FALSE(0, "待核销"),

    VERIFY_BYTYPE_ADMIN(1, "管理员"),
    VERIFY_BYTYPE_USER(2, "员工");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;

    OrderEnum(int code, String msg) {
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
     * 订单状态描述
     *
     * @author mjf
     * @param code Integer
     * @return String
     */
    public static String getOrderStatusMsg(Integer code) {
        switch (code) {
            case 1:
                return OrderEnum.ORDER_STATUS_WAIT_PAY.getMsg();
            case 2:
                return OrderEnum.ORDER_STATUS_WAIT_DELIVER.getMsg();
            case 3:
                return OrderEnum.ORDER_STATUS_TAKE_DELIVER.getMsg();
            case 4:
                return OrderEnum.ORDER_STATUS_COMPLETED.getMsg();
            case 5:
                return OrderEnum.ORDER_STATUS_CANCEL.getMsg();
        }
        return "未知";
    }

    /**
     * 订单状态描述
     *
     * @author mjf
     * @param code Integer
     * @return String
     */
    public static String getOrderStatusMsg(Integer code, Integer deliveryType) {
        switch (code) {
            case 1:
                return OrderEnum.ORDER_STATUS_WAIT_PAY.getMsg();
            case 2:
                return OrderEnum.ORDER_STATUS_WAIT_DELIVER.getMsg();
            case 3:
                if (deliveryType.equals(OrderEnum.DELIVERY_TYPE_PICK.getCode())) {
                    return OrderEnum.ORDER_STATUS_PICKUP_DELIVER.getMsg();
                } else {
                    return OrderEnum.ORDER_STATUS_TAKE_DELIVER.getMsg();
                }
            case 4:
                return OrderEnum.ORDER_STATUS_COMPLETED.getMsg();
            case 5:
                return OrderEnum.ORDER_STATUS_CANCEL.getMsg();
        }
        return "未知";
    }

    /**
     * 订单类型描述
     *
     * @author mjf
     * @param code Integer
     * @return String
     */
    public static String getOrderTypeMsg(Integer code) {
        switch (code) {
            case 1:
                return OrderEnum.ORDER_SOURCE_NORMAL.getMsg();
            case 2:
                return OrderEnum.ORDER_SOURCE_SECKILL.getMsg();
        }
        return "未知";
    }

    /**
     * 订单状态描述
     *
     * @author mjf
     * @param code Integer
     * @return String
     */
    public static String getVerificationStatusMsg(Integer code) {
        switch (code) {
            case 1:
                return OrderEnum.VERIFYSTATUS_TRUE.getMsg();
            case 0:
                return OrderEnum.VERIFYSTATUS_FALSE.getMsg();
        }
        return "未知";
    }
}
