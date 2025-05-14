package com.mdd.common.enums;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 订单售后枚举类
 */
public enum OrderAfterEnum {

    // 售后状态
    AFTER_STATUS_ING(1, "售后中"),
    AFTER_STATUS_SUCCESS(2, "售后成功"),
    AFTER_STATUS_CLOSE(3, "售后关闭"),

    // 售后类型
    AFTER_TYPE_ORDER(1, "整单退款"),
    AFTER_TYPE_GOODS(2, "商品售后"),

    // 售后类型
    METHOD_ONLY_REFUND (1, "仅退款"),
    METHOD_REFUND_GOODS(2, "退货退款"),

    // 售后子状态
    SUB_STATUS_ING_WAIT_SELLER_AGREE(11, "买家申请售后,待商家同意"),

    SUB_STATUS_ING_WAIT_BUYER_RETURN(12, "商家同意售后申请,待买家退货"),
    SUB_STATUS_ING_WAIT_SELLER_HANDLE(13, "买家已退货,待商家处理"),
    SUB_STATUS_ING_SELLER_RECEIPT_WAIT_REFUND(14, "商家确认收货,等待退款"),
    SUB_STATUS_ING_SELLER_AGREE_WAIT_REFUND(15, "商家同意售后申请,等待退款"),
    SUB_STATUS_ING_SELLER_REFUND_ING(16, "商家已确认退款,售后退款中"),

    SUB_STATUS_SUCCESS(21, "商家已确认退款,售后成功"),

    SUB_STATUS_CLOSE_BUYER_CANCEL(31, "买家撤销售后申请"),
    SUB_STATUS_CLOSE_SELLER_REFUSE_APPLY(32, "商家已拒绝售后申请,待买家处理"),
    SUB_STATUS_CLOSE_SELLER_REFUSE_RECEIPT(33, "商家拒绝收货,待买家处理");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;

    OrderAfterEnum(int code, String msg) {
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
     * 售后类型描述
     *
     * @author fzr
     * @param code 编码
     * @return String
     */
    public static String getAfterTypeMsg(Integer code){
        switch (code) {
            case 1:
                return OrderAfterEnum.AFTER_TYPE_ORDER.getMsg();
            case 2:
                return OrderAfterEnum.AFTER_TYPE_GOODS.getMsg();
        }
        return "未知";
    }

    /**
     * 退款类型描述
     *
     * @author fzr
     * @param code 编码
     * @return String
     */
    public static String getRefundTypeMsg(Integer code){
        switch (code) {
            case 1:
                return OrderAfterEnum.METHOD_ONLY_REFUND.getMsg();
            case 2:
                return OrderAfterEnum.METHOD_REFUND_GOODS.getMsg();
        }
        return "未知";
    }

    /**
     * 订单售后描述
     *
     * @author fzr
     * @param code 编码
     * @return String
     */
    public static String afterSaleMsg(Integer code) {
        switch (code) {
            case 1:
                return "无售后";
            case 2:
                return "售后中";
            case 3:
                return "售后完成";
            case 4:
                return "售后关闭";
        }

        return "未知";
    }

    /**
     * 售后状态描述
     *
     * @author fzr
     * @param code 编码
     * @return String
     */
    public static String getAfterStatusMsg(Integer code){
        switch (code) {
            case 1:
                return OrderAfterEnum.AFTER_STATUS_ING.getMsg();
            case 2:
                return OrderAfterEnum.AFTER_STATUS_SUCCESS.getMsg();
            case 3:
                return OrderAfterEnum.AFTER_STATUS_CLOSE.getMsg();
        }
        return "未知";
    }

    /**
     * 售后子状态描述
     *
     * @author fzr
     * @param code 编码
     * @return String
     */
    public static String geSubStatusMsg(Integer code){
        switch (code) {
            case 11:
                return OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_AGREE.getMsg();
            case 12:
                return OrderAfterEnum.SUB_STATUS_ING_WAIT_BUYER_RETURN.getMsg();
            case 13:
                return OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_HANDLE.getMsg();
            case 14:
                return OrderAfterEnum.SUB_STATUS_ING_SELLER_RECEIPT_WAIT_REFUND.getMsg();
            case 15:
                return OrderAfterEnum.SUB_STATUS_ING_SELLER_AGREE_WAIT_REFUND.getMsg();
            case 16:
                return OrderAfterEnum.SUB_STATUS_ING_SELLER_REFUND_ING.getMsg();
            case 21:
                return OrderAfterEnum.SUB_STATUS_SUCCESS.getMsg();
            case 31:
                return OrderAfterEnum.SUB_STATUS_CLOSE_BUYER_CANCEL.getMsg();
            case 32:
                return OrderAfterEnum.SUB_STATUS_CLOSE_SELLER_REFUSE_APPLY.getMsg();
            case 33:
                return OrderAfterEnum.SUB_STATUS_CLOSE_SELLER_REFUSE_RECEIPT.getMsg();
        }
        return "未知";
    }

    /**
     * 售后子状态列表
     *
     * @author fzr
     * @return Map<Integer, String>
     */
    public static Map<Integer, String> getAfterSubStatusList() {
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_AGREE.getCode(), OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_AGREE.getMsg());
        map.put(OrderAfterEnum.SUB_STATUS_ING_WAIT_BUYER_RETURN.getCode(), OrderAfterEnum.SUB_STATUS_ING_WAIT_BUYER_RETURN.getMsg());
        map.put(OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_HANDLE.getCode(), OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_HANDLE.getMsg());
        map.put(OrderAfterEnum.SUB_STATUS_ING_SELLER_RECEIPT_WAIT_REFUND.getCode(), OrderAfterEnum.SUB_STATUS_ING_SELLER_RECEIPT_WAIT_REFUND.getMsg());
        map.put(OrderAfterEnum.SUB_STATUS_ING_SELLER_AGREE_WAIT_REFUND.getCode(), OrderAfterEnum.SUB_STATUS_ING_SELLER_AGREE_WAIT_REFUND.getMsg());
        map.put(OrderAfterEnum.SUB_STATUS_ING_SELLER_REFUND_ING.getCode(), OrderAfterEnum.SUB_STATUS_ING_SELLER_REFUND_ING.getMsg());
        map.put(OrderAfterEnum.SUB_STATUS_SUCCESS.getCode(), OrderAfterEnum.SUB_STATUS_SUCCESS.getMsg());
        map.put(OrderAfterEnum.SUB_STATUS_CLOSE_BUYER_CANCEL.getCode(), OrderAfterEnum.SUB_STATUS_CLOSE_BUYER_CANCEL.getMsg());
        map.put(OrderAfterEnum.SUB_STATUS_CLOSE_SELLER_REFUSE_APPLY.getCode(), OrderAfterEnum.SUB_STATUS_CLOSE_SELLER_REFUSE_APPLY.getMsg());
        map.put(OrderAfterEnum.SUB_STATUS_CLOSE_SELLER_REFUSE_RECEIPT.getCode(), OrderAfterEnum.SUB_STATUS_CLOSE_SELLER_REFUSE_RECEIPT.getMsg());
        return map;
    }

}
