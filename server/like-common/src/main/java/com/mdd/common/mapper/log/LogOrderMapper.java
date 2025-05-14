package com.mdd.common.mapper.log;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.log.LogOrder;
import com.mdd.common.util.TimeUtils;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单日志Mapper
 */
@Mapper
public interface LogOrderMapper extends IBaseMapper<LogOrder> {


    /**
     * 写入订单日志
     *
     * @author mjf
     * @param orderId Integer 订单id
     * @param type Integer 类型 1系统 2商家 3买家
     * @param channel Integer 动作
     * @param operatorId Integer 操作人id
     * @param content String 内容
     */
    default void add(Integer orderId, Integer type, Integer channel, Integer operatorId, String content) {
        LogOrder orderLog = new LogOrder();
        orderLog.setOrderId(orderId);
        orderLog.setType(type);
        orderLog.setOperatorId(operatorId);
        orderLog.setChannel(channel);
        orderLog.setContent(content);
        orderLog.setCreateTime(System.currentTimeMillis() / 1000);
        orderLog.setUpdateTime(System.currentTimeMillis() / 1000);
        this.insert(orderLog);
    }

}
