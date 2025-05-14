package com.mdd.common.enums;

import java.util.*;

public enum LogMoneyEnum {

    /**
     * 用户余额类型
     */
    UM_INC_ADMIN(1001, "平台增加余额"),
    UM_DEC_ADMIN(1002, "平台减少余额"),
    UM_INC_RECHARGE(1003, "充值余额增加"),
    UM_DEC_RECHARGE(1004, "充值余额退回"),
    UM_DEC_PAY_ORDER(1005, "下单扣减余额"),

    BNW_INC_CANCEL_ORDER(1201, "取消订单退还余额"),
    BNW_INC_AFTER_SALE(1202, "售后退还余额"),
    BNW_INC_WITHDRAW(1203, "提现至余额");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;
    LogMoneyEnum(int code, String msg) {
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
     * 根据编码获取Msg
     *
     * @author fzr
     * @param code 类型
     * @return String
     */
    public static String getMsgByCode(Integer code){
        for(LogMoneyEnum enumItem : LogMoneyEnum.values()) {
            if (enumItem.getCode() == code) {
                return enumItem.getMsg();
            }
        }
        return null;
    }

    /**
     * 获取所有类型
     *
     * @author fzr
     * @return String
     */
    public static Map<Integer, String> getTypeList(){
        Map<Integer, String> map = new LinkedHashMap<>();
        for(LogMoneyEnum enumItem : LogMoneyEnum.values()) {
            map.put(enumItem.getCode(), enumItem.getMsg());
        }
        return map;
    }

    /**
     * 用户所有使用余额的变动方式
     *
     * @author mjf
     * @return List<Integer>
     */
    public static List<Integer> getChangeTypeUserDec() {
        List<Integer> list = new ArrayList<>();
        list.add(UM_DEC_PAY_ORDER.getCode());
        return list;
    }

}
