package com.mdd.common.mapper.log;

import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.log.LogOrder;
import com.mdd.common.entity.log.LogOrderAfter;
import com.mdd.common.enums.OrderAfterEnum;
import com.mdd.common.util.TimeUtils;
import org.apache.ibatis.annotations.Mapper;

/**
 * 售后日志Mapper
 */
@Mapper
public interface LogOrderAfterMapper extends IBaseMapper<LogOrderAfter> {

    /**
     * 售后日志
     *
     * @author mjf
     * @param type Integer 类型: [1=系统 2=商家 3=买家]
     * @param operatorId Integer 操作人id
     * @param orderAfterId Integer 售后订单id
     * @param content String 内容
     */
    default void add(Integer type, Integer operatorId, Integer orderAfterId, String content) {
        LogOrderAfter logOrderAfter = new LogOrderAfter();
        logOrderAfter.setType(type);
        logOrderAfter.setOrderAfterId(orderAfterId);
        logOrderAfter.setOperatorId(operatorId);
        logOrderAfter.setContent(content);
        logOrderAfter.setCreateTime(System.currentTimeMillis() / 1000);
        logOrderAfter.setUpdateTime(System.currentTimeMillis() / 1000);
        this.insert(logOrderAfter);
    }


}
