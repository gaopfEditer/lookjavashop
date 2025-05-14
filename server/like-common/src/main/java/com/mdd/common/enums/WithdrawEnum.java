package com.mdd.common.enums;

/**
 * 提现枚举类
 */
public enum WithdrawEnum {

    // 提现状态
    STATUS_WAIT(1, "待提现"),
    STATUS_ING(2, "提现中"),
    STATUS_SUCCESS(3, "提现成功"),
    STATUS_FAIL(4, "提现失败"),


    // 提现类型
    TYPE_BALANCE(1, "账户余额"),
    TYPE_WECHAT_CHANGE(2, "微信零钱"),
    TYPE_BANK(3, "银行卡"),
    TYPE_WECHAT_CODE(4, "微信收款码"),
    TYPE_ALI_CODE(5, "支付宝收款码");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;

    WithdrawEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取状态码
     *
     * @return Long
     * @author mjf
     */
    public int getCode() {
        return this.code;
    }

    /**
     * 获取提示
     *
     * @return String
     * @author mjf
     */
    public String getMsg() {
        return this.msg;
    }


    /**
     * 获取提现类型描述
     *
     * @author mjf
     * @param code Integer
     * @return String
     */
    public static String getTypeMsg(Integer code){
        switch (code) {
            case 1:
                return WithdrawEnum.TYPE_BALANCE.getMsg();
            case 2:
                return WithdrawEnum.TYPE_WECHAT_CHANGE.getMsg();
            case 3:
                return WithdrawEnum.TYPE_BANK.getMsg();
            case 4:
                return WithdrawEnum.TYPE_WECHAT_CODE.getMsg();
            case 5:
                return WithdrawEnum.TYPE_ALI_CODE.getMsg();
        }
        return "未知";
    }

    /**
     * 状态描述
     *
     * @author mjf
     * @param code Integer
     * @return String
     */
    public static String getStatusMsg(Integer code){
        switch (code) {
            case 1:
                return WithdrawEnum.STATUS_WAIT.getMsg();
            case 2:
                return WithdrawEnum.STATUS_ING.getMsg();
            case 3:
                return WithdrawEnum.STATUS_SUCCESS.getMsg();
            case 4:
                return WithdrawEnum.STATUS_FAIL.getMsg();
        }
        return "未知";
    }
}
