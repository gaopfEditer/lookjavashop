package com.mdd.admin.service;

import com.github.binarywang.wxpay.exception.WxPayException;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.order.OrderAfterSearchValidate;
import com.mdd.admin.vo.order.OrderAfterDetailVo;
import com.mdd.admin.vo.order.OrderAfterListedVo;
import com.mdd.common.core.PageResult;

/**
 * 订单售后服务接口类
 */
public interface IOrderAfterService {

    /**
     * 售后列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<OrderAfterListedVo>
     */
    PageResult<OrderAfterListedVo> list(PageValidate pageValidate, OrderAfterSearchValidate searchValidate);

    /**
     * 售后详情
     *
     * @author fzr
     * @param id 售后ID
     * @return OrderAfterDetailVo
     */
    OrderAfterDetailVo detail(Integer id);

    /**
     * 卖家同意售后
     *
     * @author fzr
     * @param adminId 管理员ID
     * @param id 售后ID
     * @param remarks 备注
     */
    void agree(Integer adminId, Integer id, String remarks);

    /**
     * 卖家拒绝售后
     *
     * @author fzr
     * @param adminId 管理员ID
     * @param id 售后ID
     * @param remarks 备注
     */
    void refuse(Integer adminId, Integer id, String remarks);

    /**
     * 卖家拒绝收货
     *
     * @author fzr
     * @param adminId 管理员ID
     * @param id 售后ID
     */
    void refuseGoods(Integer adminId, Integer id);

    /**
     * 卖家确认收货
     *
     * @author fzr
     * @param adminId 管理员ID
     * @param id 售后ID
     */
    void confirmGoods(Integer adminId, Integer id);

    /**
     * 卖家确认退款
     *
     * @author fzr
     * @param adminId 管理员ID
     * @param id 售后ID
     */
    void confirmRefund(Integer adminId, Integer id);

}
