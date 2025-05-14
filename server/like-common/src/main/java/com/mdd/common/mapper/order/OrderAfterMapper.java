package com.mdd.common.mapper.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.order.OrderAfter;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.ToolUtils;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单售后Mapper
 */
@Mapper
public interface OrderAfterMapper extends IBaseMapper<OrderAfter> {

    /**
     * 生成唯一流水号
     *
     * @author mjf
     * @param field String
     * @return String
     */
    default String randMakeOrderSn(String field) {
        String date = TimeUtils.timestampToDate(System.currentTimeMillis()/1000, "yyyyMMddHHmmss");
        String sn;
        while (true) {
            sn = date + ToolUtils.randomInt(6);
            OrderAfter snModel = this.selectOne(
                    new QueryWrapper<OrderAfter>()
                            .select("id")
                            .eq(field, sn)
                            .last("limit 1"));
            if (snModel == null) {
                break;
            }
        }
        return sn;
    }

}
