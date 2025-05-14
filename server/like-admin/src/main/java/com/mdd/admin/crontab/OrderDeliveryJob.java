package com.mdd.admin.crontab;

import com.mdd.common.mapper.order.OrderMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("orderDeliveryJob")
public class OrderDeliveryJob {

    @Resource
    private OrderMapper orderMapper;

    public void handle(){

    }
}
