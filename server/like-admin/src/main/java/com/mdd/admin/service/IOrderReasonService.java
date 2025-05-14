package com.mdd.admin.service;

import com.mdd.admin.validate.order.OrderReasonCreateValidate;
import com.mdd.admin.validate.order.OrderReasonSearchValidate;
import com.mdd.admin.validate.order.OrderReasonUpdateValidate;
import com.mdd.common.entity.order.OrderAfterReason;

import java.util.List;

/**
 * 订单售后原因服务接口类
 */
public interface IOrderReasonService {

    /**
     * 原因列表
     *
     * @author fzr
     * @param searchValidate 搜索参数
     * @return List<OrderAfterReason>
     */
    List<OrderAfterReason> list(OrderReasonSearchValidate searchValidate);

    /**
     * 原因详情
     *
     * @author fzr
     * @param id 主键
     * @return OrderAfterReason
     */
    OrderAfterReason detail(Integer id);

    /**
     * 原因新增
     *
     * @author fzr
     * @param createValidate 创建参数
     */
    void add(OrderReasonCreateValidate createValidate);

    /**
     * 原因编辑
     *
     * @author fzr
     * @param updateValidate 更新参数
     */
    void edit(OrderReasonUpdateValidate updateValidate);

    /**
     * 原因删除
     *
     * @author fzr
     * @param id 主键
     */
    void del(Integer id);

    /**
     * 原因状态
     *
     * @author fzr
     * @param id 主键
     */
    void change(Integer id);

}
