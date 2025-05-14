package com.mdd.common.mapper.distribution;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.distribution.DistributionOrder;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.ToolUtils;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分销订单Mapper
 */
@Mapper
public interface DistributionOrderMapper extends IBaseMapper<DistributionOrder> {

    /**
     * 流水号
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
            DistributionOrder snModel = this.selectOne(
                    new QueryWrapper<DistributionOrder>()
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
