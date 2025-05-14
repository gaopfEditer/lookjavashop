package com.mdd.admin.crontab;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.admin.service.IOrderManageService;
import com.mdd.common.entity.order.Order;
import com.mdd.common.enums.OrderEnum;
import com.mdd.common.enums.OrderLogEnum;
import com.mdd.common.enums.PaymentEnum;
import com.mdd.common.mapper.log.LogOrderMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.TimeUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component("orderConfirmJob")
public class OrderConfirmJob {

    @Resource
    OrderMapper orderMapper;

    @Resource
    LogOrderMapper logOrderMapper;

    @Resource
    IOrderManageService iOrderManageService;

    public void handle() {
        try {
            // 订单自动完成时长: -1=关闭,否则开启(天)
            float autoConfirmReceiptDay = Float.parseFloat(ConfigUtils.get("trade", "autoConfirmReceiptDay", "-1"));
            if (autoConfirmReceiptDay == -1) {
                return;
            }
            float confirmTime = autoConfirmReceiptDay * 24 * 60 * 60;

            List<Order> list = orderMapper.selectList(new QueryWrapper<Order>()
                    .eq("order_status", OrderEnum.ORDER_STATUS_TAKE_DELIVER.getCode())
                    .eq("pay_is", PaymentEnum.OK_PAID.getCode())
                    .ne("delivery_type", OrderEnum.DELIVERY_TYPE_PICK.getCode()) //到店自提不参与自动完成
                    .lt("express_time + " + confirmTime, TimeUtils.timestamp()));

            if (list.isEmpty()) {
                return;
            }

            for (Order order : list) {
                // 更新订单状态
                order.setOrderStatus(OrderEnum.ORDER_STATUS_COMPLETED.getCode());
                order.setConfirmTime(System.currentTimeMillis() / 1000);
                order.setAfterDeadline(iOrderManageService.getAfterSaleDeadline());
                orderMapper.updateById(order);

                // 更新订单日志
                Integer channel = OrderLogEnum.CHANNEL_ADMIN_CANCEL_ORDER.getCode();
                logOrderMapper.add(
                        order.getId(),
                        OrderLogEnum.TYPE_SYSTEM.getCode(),
                        channel,
                        0,
                        OrderLogEnum.getValue(channel));
            }
        } catch (Exception ignored) {
        }
    }

}
