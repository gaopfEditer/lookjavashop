package com.mdd.common.mapper.withdraw;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.log.LogMoney;
import com.mdd.common.entity.withdraw.WithdrawApply;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.ToolUtils;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提现申请Mapper
 */
@Mapper
public interface WithdrawApplyMapper extends IBaseMapper<WithdrawApply> {

    /**
     * 流水号
     *
     * @author mjf
     * @param field String
     * @param prefix String
     * @return String
     */
    default String randMakeOrderSn(String field, String prefix) {
        String date = TimeUtils.timestampToDate(System.currentTimeMillis()/1000, "yyyyMMddHHmmss");
        String sn;
        while (true) {
            sn = date + ToolUtils.randomInt(6);
            if (StringUtils.isNotBlank(prefix)) {
                sn = prefix + sn;
            }
            WithdrawApply snModel = this.selectOne(
                    new QueryWrapper<WithdrawApply>()
                            .select("id")
                            .eq(field, sn)
                            .last("limit 1"));
            if (snModel == null) {
                break;
            }
        }
        return sn;
    }


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
            WithdrawApply snModel = this.selectOne(
                    new QueryWrapper<WithdrawApply>()
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
