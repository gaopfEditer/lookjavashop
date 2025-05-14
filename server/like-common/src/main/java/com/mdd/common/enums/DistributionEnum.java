package com.mdd.common.enums;

/**
 * 分销枚举类
 */
public enum DistributionEnum {

    // 分销订单状态
    ORDER_UN_RETURNED(1, "待结算"),
    ORDER_RETURNED(2, "已结算"),
    ORDER_EXPIRED(3, "已失效"),

    // 分销订单层级
    ORDER_LEVEL_SELF(1, "自购佣金"),
    ORDER_LEVEL_FIRST(2, "一级分佣"),
    ORDER_LEVEL_SECOND(3, "二级分佣"),

    // 申请状态
    APPLY_STATUS_ING(1,"待审核"),
    APPLY_STATUS_SUCCESS(2,"审核通过"),
    APPLY_STATUS_FAIL(3,"审核不通过"),

    // 分销等级升级类型 1-OR关系 2-AND关系
    LEVEL_UPDATE_TYPE_OR(1, "or"),
    LEVEL_UPDATE_TYPE_AND(2, "and"),

    // 分销等级升级条件
    LEVEL_UPDATE_SINGLE_AMOUNT(1, "单笔消费金额"),
    LEVEL_UPDATE_CUMULATIVE_AMOUNT(2, "累计消费金额"),
    LEVEL_UPDATE_CUMULATIVE_TIME(3, "累计消费次数"),
    LEVEL_UPDATE_SETTLE_AMOUNT(4, "已结算佣金收入"),

    // 分销商品佣金规则
    GOODS_RULE_LEVEL(1, "按分销等级比例分佣"),
    GOODS_RULE_SINGLE(2, "单独设置分佣比例");


    /**
     * 构造方法
     */
    private final int code;
    private final String msg;

    DistributionEnum(int code, String msg) {
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


    /**
     * 申请状态描述
     *
     * @author mjf
     * @param code Integer
     * @return String
     */
    public static String getApplyStatusMsg(Integer code) {
        switch (code) {
            case 1:
                return DistributionEnum.APPLY_STATUS_ING.getMsg();
            case 2:
                return DistributionEnum.APPLY_STATUS_SUCCESS.getMsg();
            case 3:
                return DistributionEnum.APPLY_STATUS_FAIL.getMsg();
        }
        return "未知";
    }

    /**
     * 分销订单状态
     *
     * @author mjf
     * @param code Integer
     * @return String
     */
    public static String getOrderStatusMsg(Integer code) {
        switch (code) {
            case 1:
                return DistributionEnum.ORDER_UN_RETURNED.getMsg();
            case 2:
                return DistributionEnum.ORDER_RETURNED.getMsg();
            case 3:
                return DistributionEnum.ORDER_EXPIRED.getMsg();
        }
        return "未知";
    }

    /**
     * 分销订单级别
     *
     * @author mjf
     * @param code Integer
     * @return String
     */
    public static String getOrderLevelMsg(Integer code) {
        switch (code) {
            case 1:
                return DistributionEnum.ORDER_LEVEL_SELF.getMsg();
            case 2:
                return DistributionEnum.ORDER_LEVEL_FIRST.getMsg();
            case 3:
                return DistributionEnum.ORDER_LEVEL_SECOND.getMsg();
        }
        return "未知";
    }
}
