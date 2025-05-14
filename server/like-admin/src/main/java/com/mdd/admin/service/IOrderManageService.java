package com.mdd.admin.service;

import com.alibaba.fastjson2.JSONArray;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.order.OrderSearchValidate;
import com.mdd.admin.validate.order.OrderSendDeliveryValidate;
import com.mdd.admin.vo.order.OrderManageDetailVo;
import com.mdd.admin.vo.order.OrderManageExportVo;
import com.mdd.admin.vo.order.OrderManageListedVo;
import com.mdd.common.core.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 订单服务接口类
 */
public interface IOrderManageService {

    /**
     * 订单列表
     *
     * @author cjh
     * @param pageValidate 分页参数
     * @param orderSearchValidate 搜素参数
     * @return PageResult<OrderListVo>
     */
    PageResult<OrderManageListedVo> list(PageValidate pageValidate, OrderSearchValidate orderSearchValidate);

    /**
     * 订单详情
     *
     * @author fzr
     * @param id 订单ID
     * @return OrderDetailVo
     */
    OrderManageDetailVo detail(Integer id);

    /**
     * 订单取消
     *
     * @author fzr
     * @param id 订单ID
     * @param adminId 管理员ID
     */
    void cancel(Integer id, Integer adminId);

    /**
     * 订单备注
     *
     * @author fzr
     * @param orderId 订单ID
     * @param remarks 备注信息
     */
    void remarks(Integer orderId, String remarks);

    /**
     * 订单发货
     *
     * @author fzr
     * @param adminId 管理员ID
     * @param sendDeliveryValidate 发货参数
     */
    void sendDelivery(Integer adminId, OrderSendDeliveryValidate sendDeliveryValidate);

    /**
     * 确认收货
     *
     * @author fzr
     * @param orderId 订单ID
     * @param adminId 管理员ID
     */
    void takeDelivery(Integer orderId, Integer adminId);

    /**
     * 查看物流
     *
     * @author fzr
     * @param orderId 订单ID
     * @return Map<String, Object>
     */
    Map<String, Object> logistics(Integer orderId);

    /**
     * 获取当前售后时间
     *
     * @author mjf
     * @return Long
     */
    Long getAfterSaleDeadline();


    /**
     * 自提确认收货
     * @param id
     * @param items
     */
    void selffetchVerify(Integer id, JSONArray items);

    /**
     * 导出订单列表
     *
     * @author cjh
     * @param pageValidate 分页参数
     * @param orderSearchValidate 搜素参数
     * @return PageResult<OrderListVo>
     */
    List<OrderManageExportVo> exportExcel(OrderSearchValidate orderSearchValidate);

    /**
     * 判断是否已经全部自提
     */
    Boolean checkPickupAll(Integer id);

    /**
     * 根据订单ID
     * 获取商品名列表
     */
    List<String> getNamesByOid(Integer orderId);
}
